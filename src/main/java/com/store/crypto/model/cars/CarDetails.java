package com.store.crypto.model.cars;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "car_details")
public class CarDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
