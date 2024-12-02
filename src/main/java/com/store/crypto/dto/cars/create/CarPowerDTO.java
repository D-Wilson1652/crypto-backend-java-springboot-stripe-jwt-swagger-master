package com.store.crypto.dto.cars.create;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CarPowerDTO {
    private String power;
    private String powerType;
}
