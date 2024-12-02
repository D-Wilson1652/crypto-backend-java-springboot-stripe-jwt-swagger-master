package com.store.crypto.dto.cars.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CarPowerResponseDTO {
    private Long id;
    private String power;
    private String powerType;
}
