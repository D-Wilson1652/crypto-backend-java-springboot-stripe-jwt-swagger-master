package com.store.crypto.model.cars;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "car_location")
public class CarLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

