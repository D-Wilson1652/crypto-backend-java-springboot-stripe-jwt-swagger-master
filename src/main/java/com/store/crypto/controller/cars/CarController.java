package com.store.crypto.controller.cars;


import com.store.crypto.dto.cars.create.CreateCarDTO;
import com.store.crypto.dto.cars.media.CarMediaUploadDTO;
import com.store.crypto.dto.cars.update.UpdateCarDTO;
import com.store.crypto.service.cars.CarService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @SecurityRequirement(name = "Authorization")
    @PostMapping("/create")
    public ResponseEntity<Object> createCar(@Valid @RequestBody CreateCarDTO createCarDTO) {
        return carService.createCar(createCarDTO);
    }

    @SecurityRequirement(name = "Authorization")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateCar(@PathVariable Long id, @RequestBody UpdateCarDTO updateCarDTO) {
        return carService.updateCar(id, updateCarDTO);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getAllCars() {
        return carService.getAllCars();
    }

    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteCar(@PathVariable Long id) {
        return carService.deleteCar(id);
    }

    @SecurityRequirement(name = "Authorization")
    @PostMapping("/upload-media")
    public ResponseEntity<Object> uploadMedia(@ModelAttribute CarMediaUploadDTO carMediaUploadDTO) throws URISyntaxException {
        return carService.uploadMedia(carMediaUploadDTO);
    }

    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/media/delete")
    public ResponseEntity<Object> deleteMedia(@RequestParam Long mediaFileId, @RequestParam Long carId) throws URISyntaxException {
        return carService.deleteMedia(mediaFileId, carId);
    }
}
