package com.store.crypto.dto.cars.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDetailsResponseDTO {
    private Long id;
    private String year;
    //    private String mileage;
    private String gearBox;
    private String fuelType;
    private String carType;
    private String carCondition; //New or used
    private String color;


    private String drive; //LHD, or RHD
    private String engine; //10 cylinders
    private String driveTrain; //4x4, or 2x4, FWD
    private String interiorColor;

    private CarPowerResponseDTO carPower;

    private CarMileageResponseDTO mileage;
}
