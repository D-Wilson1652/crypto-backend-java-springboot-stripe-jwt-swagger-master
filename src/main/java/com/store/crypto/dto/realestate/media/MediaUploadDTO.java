package com.store.crypto.dto.realestate.media;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MediaUploadDTO {
    private Long realEstateId;
    private MultipartFile[] files;
}

