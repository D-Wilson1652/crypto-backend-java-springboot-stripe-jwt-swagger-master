package com.store.crypto.dto.realestate.create;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRealEstateDTO {
    @NotBlank(message = "Title is required")
    private String title;
    @NotNull(message = "Price is required")
    private Double price;
    @NotBlank(message = "Country is required")
    private String country;
    @NotBlank(message = "City is required")
    private String city;
    private String region;
    @NotNull(message = "Specifications are required")
    private SpecificationsDTO specifications;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Listing date is required")
    private LocalDate listingDate;
    @NotNull(message = "Location is required")
    private LocationDTO location;
    @NotNull(message = "Features are required")
    private List<Long> featureIds;  // You can also use List<String> if you want to use feature names instead of IDs
    @NotBlank(message = "Property type is required")
    private String propertyType; //i.e House
    private String propertySubType; //i.e Villa
}
