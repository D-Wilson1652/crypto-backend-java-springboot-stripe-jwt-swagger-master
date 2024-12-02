package com.store.crypto.controller.realestate;


import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.dto.realestate.create.CreateRealEstateDTO;
import com.store.crypto.dto.realestate.media.MediaUploadDTO;
import com.store.crypto.dto.realestate.update.UpdateRealEstateDTO;
import com.store.crypto.service.auth.JwtService;
import com.store.crypto.service.realestate.RealEstateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/real-estates")
@RequiredArgsConstructor
public class RealEstateController {

    private final RealEstateService realEstateService;
    private final JwtService jwtService;

    @SecurityRequirement(name = "Authorization")
    @PostMapping("/create")
    public ResponseEntity<Object> createRealEstate(@Valid @RequestBody CreateRealEstateDTO realEstateDTO) {
        return realEstateService.createRealEstate(realEstateDTO);
    }

    @SecurityRequirement(name = "Authorization")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateRealEstate(@PathVariable Long id, @RequestBody UpdateRealEstateDTO updateRealEstateDTO) {
        return realEstateService.updateRealEstate(id, updateRealEstateDTO);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getRealEstateById(@PathVariable Long id) {
        return realEstateService.getRealEstateById(id);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getAllRealEstates() {
        return realEstateService.getAllRealEstates();
    }

    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteRealEstate(@PathVariable Long id) {
        return realEstateService.deleteRealEstate(id);
    }

    @SecurityRequirement(name = "Authorization")
    @PostMapping("/upload-media")
    public ResponseEntity<Object> uploadMedia(@ModelAttribute MediaUploadDTO mediaUploadDTO) throws URISyntaxException {
        return realEstateService.uploadMedia(mediaUploadDTO);
    }

    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/media/delete")
    public ResponseEntity<Object> deleteMedia(@RequestParam Long mediaFileId, @RequestParam Long realEstateId) {
        return realEstateService.deleteMedia(mediaFileId, realEstateId);
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> filterRealEstatesByCountryOrCityOrRegion(@RequestParam(required = true) String country, @RequestParam(required = false) String city, @RequestParam(required = false) String region) {
        if (country == null && city == null && region == null) {
            GenericResponse response = new GenericResponse();
            response.setData(null);
            response.setMessage("Please provide at least the country.");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return realEstateService.filterRealEstatesByCountryOrCityOrRegion(country, city, region);
    }
}
