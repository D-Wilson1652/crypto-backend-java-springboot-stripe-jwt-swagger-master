/*
package com.store.crypto.service.realestate.impl;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class S3BatchUpload {

    private final S3TransferManager transferManager;

    public S3BatchUpload(S3TransferManager transferManager) {
        this.transferManager = transferManager;
    }

    public void uploadFilesInBatch(List<File> files, String bucketName, String prefix) {
        for (File file : files) {
            String key = prefix + "/" + file.getName();
            UploadFileRequest request = UploadFileRequest.builder()
                    .putObjectRequest(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build())
                    .source(Paths.get(file.getAbsolutePath()))
                    .build();

            try {
                CompletableFuture<Upload> uploadFuture = transferManager.uploadFile(request);
                // Wait for the upload to complete
                uploadFuture.join(); // This will block until the upload is complete
                System.out.println("Upload completed for file: " + file.getName());
            } catch (Exception e) {
                System.err.println("Failed to upload file: " + file.getName() + ", " + e.getMessage());
            }
        }
    }
}

*/
