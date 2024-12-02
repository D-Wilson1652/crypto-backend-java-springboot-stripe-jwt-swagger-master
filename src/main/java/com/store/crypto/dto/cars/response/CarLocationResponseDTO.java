package com.store.crypto.dto.cars.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarLocationResponseDTO {
    private Long id;
    private String address;
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String latitude;
    private String longitude;
}
