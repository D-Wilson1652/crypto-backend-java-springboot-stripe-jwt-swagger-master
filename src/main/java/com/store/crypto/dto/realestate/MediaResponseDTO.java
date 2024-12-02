package com.store.crypto.dto.realestate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaResponseDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileUrl; // URL to access the file from S3
}
