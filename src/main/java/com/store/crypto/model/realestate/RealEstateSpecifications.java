package com.store.crypto.model.realestate;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "real_estate_specifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEstateSpecifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer numOfBeds;
    private Integer numOfBaths;
    private Double areaInSqft;
    private Double pricePerSqft;
}
