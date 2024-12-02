package com.store.crypto.dto.realestate.update;

import com.store.crypto.model.realestate.Location;
import com.store.crypto.model.realestate.RealEstateSpecifications;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRealEstateDTO {
    private String title;
    private Double price;
    private String country;
    private String city;
    private String region;
    private RealEstateSpecifications realEstateSpecifications;
    private String description;
    private LocalDate listingDate;
    private Location location;
    private List<Long> featuresIds;
    private String propertyType; //i.e House
    private String propertySubType; //i.e Villa
}
