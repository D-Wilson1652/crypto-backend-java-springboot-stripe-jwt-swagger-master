package com.store.crypto.controller.realestate;

import com.store.crypto.service.realestate.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/features")
public class FeatureController {

    private final FeatureService featureService;

    @GetMapping("/list")
    public ResponseEntity<Object> getFeatures() {
        return featureService.findAllFeatures();
    }
}
