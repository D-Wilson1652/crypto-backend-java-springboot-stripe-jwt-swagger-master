package com.store.crypto.service.realestate.impl;

import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.dto.realestate.MediaResponseDTO;
import com.store.crypto.dto.realestate.create.CreateRealEstateDTO;
import com.store.crypto.dto.realestate.media.MediaUploadDTO;
import com.store.crypto.dto.realestate.response.FeatureResponseDTO;
import com.store.crypto.dto.realestate.response.LocationResponseDTO;
import com.store.crypto.dto.realestate.response.RealEstateResponseDTO;
import com.store.crypto.dto.realestate.response.SpecificationsResponseDTO;
import com.store.crypto.dto.realestate.update.UpdateRealEstateDTO;
import com.store.crypto.model.category.Category;
import com.store.crypto.model.realestate.*;
import com.store.crypto.model.user.User;
import com.store.crypto.repository.category.CategoryRepository;
import com.store.crypto.repository.realestate.FeatureRepository;
import com.store.crypto.repository.realestate.RealEstateMediaRepository;
import com.store.crypto.repository.realestate.RealEstateRepository;
import com.store.crypto.service.realestate.RealEstateService;
import com.store.crypto.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RealEstateServiceImpl implements RealEstateService {

    private final RealEstateRepository realEstateRepository;
    private final FeatureRepository featureRepository;
    private final RealEstateMediaRepository realEstateMediaRepository;
    private final RealEstateS3Service realEstateS3Service;
    private final CategoryRepository categoryRepository;
    private final SessionUtils sessionUtils;

    public ResponseEntity<Object> uploadMedia(MediaUploadDTO mediaUploadDTO) throws URISyntaxException {
        GenericResponse genericResponse = new GenericResponse();

        try {
            Optional<RealEstate> realEstateOptional = realEstateRepository.findById(mediaUploadDTO.getRealEstateId());
            if (realEstateOptional.isEmpty()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Real estate not found");
                genericResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
            }

            RealEstate realEstate = realEstateOptional.get();

            for (MultipartFile file : mediaUploadDTO.getFiles()) {
                String fileName = realEstateS3Service.uploadFile(file, mediaUploadDTO.getRealEstateId());
                String fileUrl = realEstateS3Service.getFileUrl(fileName).toString();

                RealEstateMedia realEstateMedia = RealEstateMedia.builder()
                        .fileName(fileName)
                        .fileType(Objects.requireNonNull(file.getContentType()).startsWith("video") ? "video" : "image")
                        .fileUrl(fileUrl)
                        .realEstate(realEstate)
                        .build();

                realEstateMediaRepository.save(realEstateMedia);
            }

            genericResponse.setData(null);
            genericResponse.setMessage("Files uploaded successfully");
            genericResponse.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            genericResponse.setData(null);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> createRealEstate(CreateRealEstateDTO realEstateDTO) {
        GenericResponse response = new GenericResponse();
        try {
            List<Feature> features = featureRepository.findAllById(realEstateDTO.getFeatureIds());

            if (features.size() != realEstateDTO.getFeatureIds().size()) {
                response.setData(null);
                response.setMessage("One or more provided features do not exist.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            RealEstate realEstate = RealEstate.builder()
                    .title(realEstateDTO.getTitle())
                    .price(realEstateDTO.getPrice())
                    .country(realEstateDTO.getCountry())
                    .city(realEstateDTO.getCity())
                    .region(realEstateDTO.getRegion())
                    .realEstateSpecifications(
                            RealEstateSpecifications.builder()
                                    .numOfBeds(realEstateDTO.getSpecifications().getNumOfBeds())
                                    .numOfBaths(realEstateDTO.getSpecifications().getNumOfBaths())
                                    .areaInSqft(realEstateDTO.getSpecifications().getAreaInSqft())
                                    .pricePerSqft(realEstateDTO.getSpecifications().getPricePerSqft())
                                    .build()
                    )
                    .description(realEstateDTO.getDescription())
                    .listingDate(realEstateDTO.getListingDate())
                    .location(
                            Location.builder()
                                    .latitude(realEstateDTO.getLocation().getLatitude())
                                    .longitude(realEstateDTO.getLocation().getLongitude())
                                    .build()
                    )
                    .features(features)
                    .propertyType(realEstateDTO.getPropertyType())
                    .propertySubType(realEstateDTO.getPropertySubType())
                    .build();


            Optional<Category> category = categoryRepository.findByName("Real Estate");
            if (category.isEmpty()) {
                Category newCategory = Category.builder().name("Real Estate").build();
                categoryRepository.save(newCategory);
                realEstate.setCategory(newCategory);
            } else {
                realEstate.setCategory(category.get());
            }

            User loggedInUser = sessionUtils.getLoggedInUser();
            realEstate.setUser(loggedInUser);

            RealEstate savedRealEstate = realEstateRepository.save(realEstate);

            RealEstateResponseDTO responseDTO = mapToRealEstateResponseDTO(savedRealEstate);
            response.setData(responseDTO);
            response.setMessage("Real Estate created successfully.");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> updateRealEstate(Long id, UpdateRealEstateDTO updateRealEstateDTO) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<RealEstate> existingRealEstate = realEstateRepository.findById(id);
            if (existingRealEstate.isEmpty()) {
                response.setData(null);
                response.setMessage("Real Estate not found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<Feature> features = featureRepository.findAllById(updateRealEstateDTO.getFeaturesIds());

            RealEstate realEstate = getRealEstateUpdatedEntity(updateRealEstateDTO, existingRealEstate, features);

            RealEstate updatedRealEstate = realEstateRepository.save(realEstate);

            RealEstateResponseDTO responseDTO = mapToRealEstateResponseDTO(updatedRealEstate);
            response.setData(responseDTO);
            response.setMessage("Real Estate updated successfully.");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static RealEstate getRealEstateUpdatedEntity(UpdateRealEstateDTO updateRealEstateDTO, Optional<RealEstate> existingRealEstate, List<Feature> features) {
        RealEstate realEstate = existingRealEstate.get();
        realEstate.setTitle(updateRealEstateDTO.getTitle());
        realEstate.setPrice(updateRealEstateDTO.getPrice());
        realEstate.setCountry(updateRealEstateDTO.getCountry());
        realEstate.setCity(updateRealEstateDTO.getCity());
        realEstate.setRegion(updateRealEstateDTO.getRegion());
        realEstate.setRealEstateSpecifications(updateRealEstateDTO.getRealEstateSpecifications());
        realEstate.setDescription(updateRealEstateDTO.getDescription());
        realEstate.setListingDate(updateRealEstateDTO.getListingDate());
        realEstate.setLocation(updateRealEstateDTO.getLocation());
        realEstate.setFeatures(features);
        realEstate.setPropertyType(updateRealEstateDTO.getPropertyType());
        if (updateRealEstateDTO.getPropertySubType() != null && !updateRealEstateDTO.getPropertySubType().isEmpty()) {
            realEstate.setPropertySubType(updateRealEstateDTO.getPropertySubType());
        }
        return realEstate;
    }

    @Override
    public ResponseEntity<Object> getRealEstateById(Long id) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<RealEstate> realEstate = realEstateRepository.findById(id);
            if (realEstate.isPresent()) {
                RealEstateResponseDTO responseDTO = mapToRealEstateResponseDTO(realEstate.get());
                response.setData(responseDTO);
                response.setMessage("Real Estate retrieved successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setData(null);
                response.setMessage("Real Estate not found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> getAllRealEstates() {
        GenericResponse response = new GenericResponse();
        try {
            List<RealEstate> realEstates = realEstateRepository.findAll();
            if (realEstates.isEmpty()) {
                response.setData(null);
                response.setMessage("No Real Estates found.");
            } else {
                List<RealEstateResponseDTO> responseDTOs = realEstates.stream()
                        .map(this::mapToRealEstateResponseDTO)
                        .collect(Collectors.toList());
                response.setData(responseDTOs);
                response.setMessage("Real Estates retrieved successfully.");
            }
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> deleteRealEstate(Long id) {
        GenericResponse response = new GenericResponse();
        response.setData(null);
        try {
            Optional<RealEstate> realEstate = realEstateRepository.findById(id);
            if (realEstate.isEmpty()) {
                response.setMessage("Real Estate doesn't exist that you want to delete with id: " + id);
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                // Delete associated media files from S3
                realEstateS3Service.deleteFilesByRealEstateId(id);
                realEstateRepository.delete(realEstate.get());
                response.setMessage("Real Estate deleted successfully with id: " + id);
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> filterRealEstatesByCountryOrCityOrRegion(String country, String city, String region) {
        GenericResponse response = new GenericResponse();
        try {
            if ((country != null && !country.isEmpty()) && city == null && region == null) {
                List<RealEstate> realEstates = realEstateRepository.findAllByCountryEqualsIgnoreCase(country);
                if (realEstates.isEmpty()) {
                    response.setData(null);
                    response.setMessage("No Real Estates found against country: " + country);
                } else {
                    List<RealEstateResponseDTO> responseDTOs = realEstates.stream()
                            .map(this::mapToRealEstateResponseDTO)
                            .collect(Collectors.toList());
                    response.setData(responseDTOs);
                    response.setMessage("Real Estates retrieved successfully against country: " + country);
                }
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if ((country != null && !country.isEmpty()) && (city != null && !city.isEmpty()) && region == null) {
                List<RealEstate> realEstates = realEstateRepository.findAllByCountryEqualsIgnoreCaseAndCityEqualsIgnoreCase(country, city);
                if (realEstates.isEmpty()) {
                    response.setData(null);
                    response.setMessage("No Real Estates found against country: " + country + " and city: " + city);
                } else {
                    List<RealEstateResponseDTO> responseDTOs = realEstates.stream()
                            .map(this::mapToRealEstateResponseDTO)
                            .collect(Collectors.toList());
                    response.setData(responseDTOs);
                    response.setMessage("Real Estates retrieved successfully.");
                }
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if ((country != null && !country.isEmpty()) && (city != null && !city.isEmpty()) && (region != null && !region.isEmpty())) {
                List<RealEstate> realEstates = realEstateRepository.findAllByCountryEqualsIgnoreCaseAndCityEqualsIgnoreCaseAndRegionEqualsIgnoreCase(country, city, region);
                if (realEstates.isEmpty()) {
                    response.setData(null);
                    response.setMessage("No Real Estates found against country: " + country + " and city: " + city + " and region: " + region);
                } else {
                    List<RealEstateResponseDTO> responseDTOs = realEstates.stream()
                            .map(this::mapToRealEstateResponseDTO)
                            .collect(Collectors.toList());
                    response.setData(responseDTOs);
                    response.setMessage("Real Estates retrieved successfully against country: " + country + " and city: " + city + " and region: " + region);
                }
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setData(null);
                response.setMessage("Please provide the valid parameters.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private RealEstateResponseDTO mapToRealEstateResponseDTO(RealEstate realEstate) {
        List<MediaResponseDTO> mediaResponseDTOList = new ArrayList<>();

        if (null != realEstate.getRealEstateMediaList() && !realEstate.getRealEstateMediaList().isEmpty()) {
            mediaResponseDTOList = realEstate.getRealEstateMediaList()
                    .stream()
                    .map(realEstateMedia -> {
                        try {
                            return MediaResponseDTO.builder()
                                    .id(realEstateMedia.getId())
                                    .fileName(realEstateMedia.getFileName())
                                    .fileType(realEstateMedia.getFileType())
                                    .fileUrl(realEstateS3Service.getSignedFileUrl(Objects.requireNonNull(realEstateMedia.getFileName())).toString())
                                    .build();
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        }

        return RealEstateResponseDTO.builder()
                .id(realEstate.getId())
                .country(realEstate.getCountry())
                .city(realEstate.getCity())
                .region(realEstate.getRegion())
                .title(realEstate.getTitle())
                .price(realEstate.getPrice())
                .specifications(
                        SpecificationsResponseDTO.builder()
                                .id(realEstate.getRealEstateSpecifications().getId())
                                .numOfBeds(realEstate.getRealEstateSpecifications().getNumOfBeds())
                                .numOfBaths(realEstate.getRealEstateSpecifications().getNumOfBaths())
                                .areaInSqft(realEstate.getRealEstateSpecifications().getAreaInSqft())
                                .pricePerSqft(realEstate.getRealEstateSpecifications().getPricePerSqft())
                                .build()
                )
                .description(realEstate.getDescription())
                .listingDate(realEstate.getListingDate())
                .location(
                        LocationResponseDTO.builder()
                                .latitude(realEstate.getLocation().getLatitude())
                                .longitude(realEstate.getLocation().getLongitude())
                                .build()
                )
                .features(
                        realEstate.getFeatures().stream()
                                .map(feature -> FeatureResponseDTO.builder()
                                        .id(feature.getId())
                                        .category(feature.getCategory())
                                        .name(feature.getName())
                                        .build())
                                .collect(Collectors.toList())
                )
                .propertyType(realEstate.getPropertyType())
                .propertySubType(realEstate.getPropertySubType())
                .mediaList(mediaResponseDTOList)
                .build();
    }


    @Override
    public ResponseEntity<Object> deleteMedia(Long mediaFileId, Long realEstateId) {
        GenericResponse genericResponse = new GenericResponse();
        try {
            Optional<RealEstate> realEstate = realEstateRepository.findById(realEstateId);
            if (realEstate.isEmpty()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Real Estate not found.");
                genericResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.NOT_FOUND);
            }

            if (realEstate.get().getRealEstateMediaList() == null || realEstate.get().getRealEstateMediaList().isEmpty()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Real Estate media not found.");
                genericResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.NOT_FOUND);
            }


            Optional<RealEstateMedia> existingRealEstateMedia = realEstateMediaRepository.findById(mediaFileId);
            if (existingRealEstateMedia.isEmpty()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Real Estate media not found.");
                genericResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.NOT_FOUND);
            }

            List<RealEstateMedia> realEstateMediaList = realEstate.get().getRealEstateMediaList();
            for (RealEstateMedia realEstateMedia : realEstateMediaList) {
                if (realEstateMedia.getId().equals(mediaFileId)) {
                    realEstate.get().getRealEstateMediaList().remove(realEstateMedia);
                    realEstateRepository.save(realEstate.get());
                    break;
                }
            }

            realEstateS3Service.deleteFile(existingRealEstateMedia.get().getFileName());
            realEstateMediaRepository.delete(existingRealEstateMedia.get());
            genericResponse.setData(null);
            genericResponse.setMessage("Real Estate media file deleted successfully.");
            genericResponse.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            genericResponse.setData(null);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
