package com.store.crypto.service.realestate.impl;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RealEstateS3Service {

    private final S3Client s3Client;
    private final Environment environment;
    private final S3Presigner presigner;


    public RealEstateS3Service(Environment environment) {
        this.environment = environment;

        log.info("Initializing S3 service");
        for (String profile : environment.getActiveProfiles()) {
            log.info("Active Profile: {}", profile);
        }

        log.info("Setting up S3 client");
        log.info("Bucket: {}", environment.getProperty(Objects.requireNonNull(environment.getProperty("aws.region"))));

        this.s3Client = S3Client.builder()
                .region(Region.of(Objects.requireNonNull(environment.getProperty("aws.region"))))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                environment.getProperty("aws.access-key-id"),
                                environment.getProperty("aws.secret-access-key")
                        )
                ))
                .build();

        this.presigner = S3Presigner.builder()
                .region(Region.of(Objects.requireNonNull(environment.getProperty("aws.region"))))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                environment.getProperty("aws.access-key-id"),
                                environment.getProperty("aws.secret-access-key")
                        )
                ))
                .build();
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public String uploadFile(MultipartFile file, Long realEstateId) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String s3Key = "real-estate/" + realEstateId + "/" + fileName;

        CompletableFuture.runAsync(() -> {
            try {
                log.info("Uploading file to S3: {}", s3Key);
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(environment.getProperty("aws.s3.bucket-name"))
                                .key(s3Key)
                                .build(),
                        RequestBody.fromBytes(file.getBytes()));
            } catch (IOException e) {
                log.error("Failed to upload file for the real estate: {} id to S3: {}", realEstateId, e.getMessage());
                throw new RuntimeException("Failed to upload file to S3", e);
            }
        }, executorService);

        return s3Key;
    }


    public URI getSignedFileUrl(String fileName) throws URISyntaxException {
        try {
            // Create the presigned URL
            log.info("Creating presigned URL for file: {}", fileName);
            String bucketName = environment.getProperty("aws.s3.bucket-name");
            log.info("Bucket name: {}", bucketName);
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(environment.getProperty("aws.s3.bucket-name"))
                    .key(fileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(60)) // Set how long the signed URL should be valid for
                    .build();

            return presigner.presignGetObject(presignRequest).url().toURI(); // Generate the presigned URL
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create presigned URL", e);
        }
    }


    public URI getFileUrl(String fileName) throws URISyntaxException {
        URL url = s3Client.utilities().getUrl(
                builder ->
                        builder.bucket(environment.getProperty("aws.s3.bucket-name")).key(fileName));
        return url.toURI();
    }

    public void deleteFile(String s3Key) {
        try {
            log.info("Deleting file from S3: {}", s3Key);
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(environment.getProperty("aws.s3.bucket-name"))
                    .key(s3Key)
                    .build());
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}", s3Key, e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public void deleteFilesByRealEstateId(Long realEstateId) {
        // Get a list of all files under the "real-estate/{realEstateId}/" prefix
        List<String> keysToDelete = s3Client.listObjectsV2Paginator(builder ->
                        builder.bucket(environment.getProperty("aws.s3.bucket-name"))
                                .prefix("real-estate/" + realEstateId + "/"))
                .contents()
                .stream()
                .map(S3Object::key)
                .collect(Collectors.toList());

        // Delete each file
        keysToDelete.forEach(this::deleteFile);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

}
