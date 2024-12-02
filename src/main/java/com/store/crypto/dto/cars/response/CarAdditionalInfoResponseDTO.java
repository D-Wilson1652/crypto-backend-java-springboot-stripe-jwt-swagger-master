package com.store.crypto.dto.cars.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarAdditionalInfoResponseDTO {
    private Long id;
    private String vatType;
    private String licenseNumber;
}
