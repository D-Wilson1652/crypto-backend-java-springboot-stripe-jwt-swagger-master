package com.store.crypto.service.realestate.impl;

import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.model.realestate.Feature;
import com.store.crypto.repository.realestate.FeatureRepository;
import com.store.crypto.service.realestate.FeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;

    @Override
    public ResponseEntity<Object> findAllFeatures() {
        GenericResponse response = new GenericResponse();
        List<Feature> features = featureRepository.findAll();
        if (features.isEmpty()) {
            response.setData(features);
            response.setMessage("Features are empty in the database.");
        } else {
            response.setData(features);
            response.setMessage("Features retrieved successfully.");
        }
        response.setStatusCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
