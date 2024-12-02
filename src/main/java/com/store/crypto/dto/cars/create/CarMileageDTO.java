package com.store.crypto.dto.cars.create;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CarMileageDTO {
    private String mileage;
    private String mileageType;
}
