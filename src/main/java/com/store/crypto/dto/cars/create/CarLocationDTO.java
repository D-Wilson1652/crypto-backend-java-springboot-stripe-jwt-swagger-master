package com.store.crypto.dto.cars.create;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarLocationDTO {
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Country is required")
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String latitude;
    private String longitude;
}
