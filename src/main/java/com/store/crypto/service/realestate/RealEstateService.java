package com.store.crypto.service.realestate;


import com.store.crypto.dto.realestate.create.CreateRealEstateDTO;
import com.store.crypto.dto.realestate.media.MediaUploadDTO;
import com.store.crypto.dto.realestate.update.UpdateRealEstateDTO;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;

public interface RealEstateService {

    ResponseEntity<Object> uploadMedia(MediaUploadDTO mediaUploadDTO) throws URISyntaxException;

    ResponseEntity<Object> createRealEstate(CreateRealEstateDTO realEstateDTO);

    ResponseEntity<Object> updateRealEstate(Long id, UpdateRealEstateDTO updateRealEstateDTO);

    ResponseEntity<Object> getRealEstateById(Long id);

    ResponseEntity<Object> getAllRealEstates();

    ResponseEntity<Object> deleteRealEstate(Long id);

    ResponseEntity<Object> filterRealEstatesByCountryOrCityOrRegion(String country, String city, String region);

    ResponseEntity<Object> deleteMedia(Long mediaFileId, Long realEstateId);
}

