package com.store.crypto.dto.cars.media;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CarMediaUploadDTO {
    private Long carId;
    private MultipartFile[] files;
}

