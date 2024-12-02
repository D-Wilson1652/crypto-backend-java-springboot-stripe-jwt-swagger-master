package com.store.crypto.dto.cars.create;


import com.store.crypto.model.cars.CarMileage;
import com.store.crypto.model.cars.CarPower;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDetailsDTO {
    @NotBlank(message = "Car model year is required.")
    private String year;

    @NotBlank(message = "Car transmission is required.")
    private String gearBox;
    @NotBlank(message = "Car fuel type is required.")
    private String fuelType;
    private String carType;
    private String carCondition; //New or used
    private String color;



    private String drive; //LHD, or RHD
    private String engine; //10 cylinders
    private String driveTrain; //4x4, or 2x4, FWD
    private String interiorColor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_power_id")
    private CarPower carPower;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_mileage_id")
    private CarMileage mileage;



}
