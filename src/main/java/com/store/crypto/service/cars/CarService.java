package com.store.crypto.service.cars;


import com.store.crypto.dto.cars.create.CreateCarDTO;
import com.store.crypto.dto.cars.media.CarMediaUploadDTO;
import com.store.crypto.dto.cars.update.UpdateCarDTO;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;

public interface CarService {

    ResponseEntity<Object> uploadMedia(CarMediaUploadDTO carMediaUploadDTO) throws URISyntaxException;

    ResponseEntity<Object> createCar(CreateCarDTO createCarDTO);

    ResponseEntity<Object> updateCar(Long id, UpdateCarDTO updateCarDTO);

    ResponseEntity<Object> getCarById(Long id);

    ResponseEntity<Object> getAllCars();

    ResponseEntity<Object> deleteCar(Long id);

    ResponseEntity<Object> deleteMedia(Long mediaFileId, Long carId);

    /* ResponseEntity<Object> filterRealEstatesByCountryOrCityOrRegion(String country, String city, String region);*/
}

