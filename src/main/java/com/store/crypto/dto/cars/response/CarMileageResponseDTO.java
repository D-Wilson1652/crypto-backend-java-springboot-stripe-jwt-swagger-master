package com.store.crypto.dto.cars.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CarMileageResponseDTO {
    private Long id;
    private String mileage;
    private String mileageType;
}
