package com.store.crypto.service.cars.impl;

import com.store.crypto.dto.cars.create.CarAdditionalInfoDTO;
import com.store.crypto.dto.cars.create.CarDetailsDTO;
import com.store.crypto.dto.cars.create.CarLocationDTO;
import com.store.crypto.dto.cars.create.CreateCarDTO;
import com.store.crypto.dto.cars.media.CarMediaResponseDTO;
import com.store.crypto.dto.cars.media.CarMediaUploadDTO;
import com.store.crypto.dto.cars.response.*;
import com.store.crypto.dto.cars.update.UpdateCarDTO;
import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.model.cars.*;
import com.store.crypto.model.category.Category;
import com.store.crypto.model.user.User;
import com.store.crypto.repository.cars.CarMediaRepository;
import com.store.crypto.repository.cars.CarMileageRepository;
import com.store.crypto.repository.cars.CarPowerRepository;
import com.store.crypto.repository.cars.CarRepository;
import com.store.crypto.repository.category.CategoryRepository;
import com.store.crypto.service.cars.CarService;
import com.store.crypto.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarMediaRepository carMediaRepository;
    private final CarMileageRepository carMileageRepository;
    private final CarRepository carRepository;
    private final CarPowerRepository carPowerRepository;
    private final CarS3Service carS3Service;
    private final CategoryRepository categoryRepository;
    private final SessionUtils sessionUtils;

    public ResponseEntity<Object> uploadMedia(CarMediaUploadDTO carMediaUploadDTO) throws URISyntaxException {
        GenericResponse genericResponse = new GenericResponse();
        try {
            Optional<Car> car = carRepository.findById(carMediaUploadDTO.getCarId());
            if (car.isEmpty()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Car not found");
                genericResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
            }

            Car existingCar = car.get();
            for (MultipartFile file : carMediaUploadDTO.getFiles()) {
                String fileName = carS3Service.uploadFile(file, carMediaUploadDTO.getCarId());
                String fileUrl = carS3Service.getFileUrl(fileName).toString();

                CarMedia realEstateMedia = CarMedia.builder()
                        .fileName(fileName)
                        .fileType(Objects.requireNonNull(file.getContentType()).startsWith("video") ? "video" : "image")
                        .fileUrl(fileUrl)
                        .car(existingCar)
                        .build();
                carMediaRepository.save(realEstateMedia);
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
    public ResponseEntity<Object> createCar(CreateCarDTO createCarDTO) {
        GenericResponse response = new GenericResponse();
        try {
            Car car = buildCar(createCarDTO);
            setCarCategory(car);

            // Associate the car with the logged-in user
            User loggedInUser = sessionUtils.getLoggedInUser();
            car.setUser(loggedInUser);

            // Save the car and create a response DTO
            Car savedCar = carRepository.save(car);
            CarResponseDTO responseDTO = mapToCarResponseDTO(savedCar);

            // Set response attributes
            response.setData(responseDTO);
            response.setMessage("Car created successfully.");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions and set error response
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Car buildCar(CreateCarDTO createCarDTO) {
        return Car.builder()
                .title(getOrNull(createCarDTO.getTitle()))
                .make(getOrNull(createCarDTO.getMake()))
                .model(getOrNull(createCarDTO.getModel()))
                .price(getOrNull(createCarDTO.getPrice()))
                .carLocation(buildCarLocation(createCarDTO.getCarLocation()))
                .carDetails(buildCarDetails(createCarDTO.getCarDetails()))
                .description(getOrNull(createCarDTO.getDescription()))
                .carAdditionalInfo(buildCarAdditionalInfo(createCarDTO.getCarAdditionalInfo()))
                .listingDate(LocalDate.now())
                .build();
    }

    private CarLocation buildCarLocation(CarLocationDTO carLocationDTO) {
        if (carLocationDTO == null) return null;

        return CarLocation.builder()
                .address(getOrNull(carLocationDTO.getAddress()))
                .country(getOrNull(carLocationDTO.getCountry()))
                .state(getOrNull(carLocationDTO.getState()))
                .city(getOrNull(carLocationDTO.getCity()))
                .zipCode(getOrNull(carLocationDTO.getZipCode()))
                .latitude(getOrNull(carLocationDTO.getLatitude()))
                .longitude(getOrNull(carLocationDTO.getLongitude()))
                .build();
    }

    private CarDetails buildCarDetails(CarDetailsDTO carDetailsDTO) {
        if (carDetailsDTO == null) return null;

        return CarDetails.builder()
                .year(getOrNull(carDetailsDTO.getYear()))
                .mileage(getOrNull(carMileageRepository.save(carDetailsDTO.getMileage())))
                .carPower(getOrNull(carPowerRepository.save(carDetailsDTO.getCarPower())))
                .gearBox(getOrNull(carDetailsDTO.getGearBox()))
                .fuelType(getOrNull(carDetailsDTO.getFuelType()))
                .carType(getOrNull(carDetailsDTO.getCarType()))
                .carCondition(getOrNull(carDetailsDTO.getCarCondition()))
                .color(getOrNull(carDetailsDTO.getColor()))
                .drive(getOrNull(carDetailsDTO.getDrive()))
                .engine(getOrNull(carDetailsDTO.getEngine()))
                .driveTrain(getOrNull(carDetailsDTO.getDriveTrain()))
                .interiorColor(getOrNull(carDetailsDTO.getInteriorColor()))
                .build();
    }

    private CarAdditionalInfo buildCarAdditionalInfo(CarAdditionalInfoDTO carAdditionalInfoDTO) {
        if (carAdditionalInfoDTO == null) return null;

        return CarAdditionalInfo.builder()
                .vatType(getOrNull(carAdditionalInfoDTO.getVatType()))
                .licenseNumber(getOrNull(carAdditionalInfoDTO.getLicenseNumber()))
                .build();
    }

    private void setCarCategory(Car car) {
        Optional<Category> category = categoryRepository.findByName("Cars");
        if (category.isEmpty()) {
            Category newCategory = Category.builder().name("Cars").build();
            categoryRepository.save(newCategory);
            car.setCategory(newCategory);
        } else {
            car.setCategory(category.get());
        }
    }

    private <T> T getOrNull(T value) {
        return value;
    }

    @Override
    public ResponseEntity<Object> updateCar(Long id, UpdateCarDTO updateCarDTO) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<Car> existingCar = carRepository.findById(id);
            if (existingCar.isEmpty()) {
                response.setData(null);
                response.setMessage("Car not found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            Car car = getCarUpdatedEntity(updateCarDTO, existingCar);

            Car updatedCar = carRepository.save(car);

            CarResponseDTO responseDTO = mapToCarResponseDTO(updatedCar);
            response.setData(responseDTO);
            response.setMessage("Car updated successfully.");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static Car getCarUpdatedEntity(UpdateCarDTO updateCarDTO, Optional<Car> optionalCar) {
        Car car = optionalCar.get(); //Existing car
        car.setTitle(updateCarDTO.getName());
        car.setMake(updateCarDTO.getMake());
        car.setModel(updateCarDTO.getModel());
        car.setPrice(updateCarDTO.getPrice());
        //Fetch from db

        car.setCarLocation(CarLocation.builder()
                .id(updateCarDTO.getCarLocation().getId())
                .address(updateCarDTO.getCarLocation().getAddress())
                .country(updateCarDTO.getCarLocation().getCountry())
                .state(updateCarDTO.getCarLocation().getState())
                .city(updateCarDTO.getCarLocation().getCity())
                .zipCode(updateCarDTO.getCarLocation().getZipCode())
                .latitude(updateCarDTO.getCarLocation().getLatitude())
                .longitude(updateCarDTO.getCarLocation().getLongitude())
                .build());

        //Fetch from database with id.
        car.setCarDetails(
                CarDetails.builder()
                        .id(updateCarDTO.getCarDetails().getId())
                        .year(updateCarDTO.getCarDetails().getYear())
                        .mileage(updateCarDTO.getCarDetails().getMileage())
                        .gearBox(updateCarDTO.getCarDetails().getGearBox())
                        .fuelType(updateCarDTO.getCarDetails().getFuelType())
                        .carType(updateCarDTO.getCarDetails().getCarType())
                        .carCondition(updateCarDTO.getCarDetails().getCarCondition())
                        .color(updateCarDTO.getCarDetails().getColor())

                        .drive(updateCarDTO.getCarDetails().getDrive())
                        .engine(updateCarDTO.getCarDetails().getEngine())
                        .driveTrain(updateCarDTO.getCarDetails().getDriveTrain())
                        .interiorColor(updateCarDTO.getCarDetails().getInteriorColor())
                        .carPower(updateCarDTO.getCarDetails().getCarPower())
                        .build());

        //Fetch from database.
        car.setCarAdditionalInfo(CarAdditionalInfo.builder()
                .id(updateCarDTO.getCarAdditionalInfo().getId())
                .vatType(updateCarDTO.getCarAdditionalInfo().getVatType())
                .licenseNumber(updateCarDTO.getCarAdditionalInfo().getLicenseNumber())
                .build());
        car.setDescription(updateCarDTO.getDescription());
        return car;
    }

    @Override
    public ResponseEntity<Object> getCarById(Long id) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<Car> realEstate = carRepository.findById(id);
            if (realEstate.isPresent()) {
                CarResponseDTO responseDTO = mapToCarResponseDTO(realEstate.get());
                response.setData(responseDTO);
                response.setMessage("Car retrieved successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setData(null);
                response.setMessage("Car not found.");
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
    public ResponseEntity<Object> getAllCars() {
        GenericResponse response = new GenericResponse();
        try {
            List<Car> realEstates = carRepository.findAll();
            if (realEstates.isEmpty()) {
                response.setData(null);
                response.setMessage("No Car found.");
            } else {
                List<CarResponseDTO> responseDTOs = realEstates.stream()
                        .map(this::mapToCarResponseDTO)
                        .collect(Collectors.toList());
                response.setData(responseDTOs);
                response.setMessage("Cars retrieved successfully.");
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
    public ResponseEntity<Object> deleteCar(Long id) {
        GenericResponse response = new GenericResponse();
        response.setData(null);
        try {
            Optional<Car> car = carRepository.findById(id);
            if (car.isEmpty()) {
                response.setMessage("Car doesn't exist that you want to delete with id: " + id);
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                // Delete associated media files from S3
                carS3Service.deleteFilesByCarId(id);
                carRepository.delete(car.get());
                response.setMessage("Car deleted successfully with id: " + id);
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private CarResponseDTO mapToCarResponseDTO(Car car) {
        List<CarMediaResponseDTO> mediaResponseDTOList = mapCarMedia(car.getCarMedia());

        return CarResponseDTO.builder()
                .id(getValueOrNull(car.getId()))
                .name(getValueOrNull(car.getTitle()))
                .make(getValueOrNull(car.getMake()))
                .model(getValueOrNull(car.getModel()))
                .price(getValueOrNull(car.getPrice()))
                .carLocation(mapCarLocation(car.getCarLocation()))
                .carDetails(mapCarDetails(car.getCarDetails()))
                .description(getValueOrNull(car.getDescription()))
                .carAdditionalInfo(mapCarAdditionalInfo(car.getCarAdditionalInfo()))
                .mediaList(mediaResponseDTOList)
                .listingDate(getValueOrNull(car.getListingDate()))
                .build();
    }

    private List<CarMediaResponseDTO> mapCarMedia(List<CarMedia> carMediaList) {
        if (carMediaList == null || carMediaList.isEmpty()) {
            return new ArrayList<>();
        }

        return carMediaList.stream()
                .map(this::mapCarMediaResponseDTO)
                .collect(Collectors.toList());
    }

    private CarMediaResponseDTO mapCarMediaResponseDTO(CarMedia carMedia) {
        try {
            return CarMediaResponseDTO.builder()
                    .id(getValueOrNull(carMedia.getId()))
                    .fileName(getValueOrNull(carMedia.getFileName()))
                    .fileType(getValueOrNull(carMedia.getFileType()))
                    .fileUrl(getFileUrl(carMedia.getFileName()))
                    .build();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private CarLocationResponseDTO mapCarLocation(CarLocation carLocation) {
        if (carLocation == null) {
            return null;
        }

        return CarLocationResponseDTO.builder()
                .id(getValueOrNull(carLocation.getId()))
                .address(getValueOrNull(carLocation.getAddress()))
                .country(getValueOrNull(carLocation.getCountry()))
                .state(getValueOrNull(carLocation.getState()))
                .city(getValueOrNull(carLocation.getCity()))
                .zipCode(getValueOrNull(carLocation.getZipCode()))
                .latitude(getValueOrNull(carLocation.getLatitude()))
                .longitude(getValueOrNull(carLocation.getLongitude()))
                .build();
    }

    private CarDetailsResponseDTO mapCarDetails(CarDetails carDetails) {
        if (carDetails == null) {
            return null;
        }

        return CarDetailsResponseDTO.builder()
                .id(getValueOrNull(carDetails.getId()))
                .year(getValueOrNull(carDetails.getYear()))
                .mileage(getValueOrNull(CarMileageResponseDTO.builder()
                        .id(getValueOrNull(carDetails.getMileage().getId()))
                        .mileage(getValueOrNull(carDetails.getMileage().getMileage()))
                        .mileageType(getValueOrNull(carDetails.getMileage().getMileageType()))
                        .build()))
                .carPower(getValueOrNull(CarPowerResponseDTO.builder()
                        .id(getValueOrNull(carDetails.getCarPower().getId()))
                        .power(getValueOrNull(carDetails.getCarPower().getPower()))
                        .powerType(getValueOrNull(carDetails.getCarPower().getPowerType()))
                        .build()))
                .gearBox(getValueOrNull(carDetails.getGearBox()))
                .fuelType(getValueOrNull(carDetails.getFuelType()))
                .carType(getValueOrNull(carDetails.getCarType()))
                .carCondition(getValueOrNull(carDetails.getCarCondition()))
                .color(getValueOrNull(carDetails.getColor()))
                .drive(getValueOrNull(carDetails.getDrive()))
                .engine(getValueOrNull(carDetails.getEngine()))
                .driveTrain(getValueOrNull(carDetails.getDriveTrain()))
                .interiorColor(getValueOrNull(carDetails.getInteriorColor()))
                .build();
    }

    private CarAdditionalInfoResponseDTO mapCarAdditionalInfo(CarAdditionalInfo carAdditionalInfo) {
        if (carAdditionalInfo == null) {
            return null;
        }

        return CarAdditionalInfoResponseDTO.builder()
                .id(getValueOrNull(carAdditionalInfo.getId()))
                .vatType(getValueOrNull(carAdditionalInfo.getVatType()))
                .licenseNumber(getValueOrNull(carAdditionalInfo.getLicenseNumber()))
                .build();
    }

    private String getFileUrl(String fileName) throws URISyntaxException {
        if (fileName == null) {
            return null;
        }
        return carS3Service.getFileUrl(fileName).toString();
    }

    private <T> T getValueOrNull(T value) {
        return value != null ? value : null;
    }

    @Override
    public ResponseEntity<Object> deleteMedia(Long mediaFileId, Long carId) {
        GenericResponse genericResponse = new GenericResponse();
        try {
            Optional<Car> car = carRepository.findById(carId);
            if (car.isEmpty()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Car not found.");
                genericResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.NOT_FOUND);
            }

            if (car.get().getCarMedia() == null || car.get().getCarMedia().isEmpty()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Car media not found.");
                genericResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.NOT_FOUND);
            }


            Optional<CarMedia> existingCarMedia = carMediaRepository.findById(mediaFileId);
            if (existingCarMedia.isEmpty()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Car media not found.");
                genericResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.NOT_FOUND);
            }

            List<CarMedia> carMediaList = car.get().getCarMedia();
            for (CarMedia carMedia : carMediaList) {
                if (carMedia.getId().equals(mediaFileId)) {
                    car.get().getCarMedia().remove(carMedia);
                    carRepository.save(car.get());
                    break;
                }
            }
            carS3Service.deleteFile(existingCarMedia.get().getFileName());
            carMediaRepository.delete(existingCarMedia.get());
            genericResponse.setData(null);
            genericResponse.setMessage("Car media file deleted successfully.");
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
