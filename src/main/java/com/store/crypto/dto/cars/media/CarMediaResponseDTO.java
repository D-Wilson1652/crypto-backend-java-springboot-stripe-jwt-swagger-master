package com.store.crypto.dto.cars.media;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarMediaResponseDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileUrl; // URL to access the file from S3
}
