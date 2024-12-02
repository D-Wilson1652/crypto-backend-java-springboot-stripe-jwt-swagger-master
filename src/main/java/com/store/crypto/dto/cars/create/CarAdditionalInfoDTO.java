package com.store.crypto.dto.cars.create;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarAdditionalInfoDTO {
    private String vatType;
    private String licenseNumber;
}
