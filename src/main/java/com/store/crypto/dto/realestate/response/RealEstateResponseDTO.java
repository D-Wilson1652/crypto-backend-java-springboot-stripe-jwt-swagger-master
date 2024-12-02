package com.store.crypto.dto.realestate.response;

import com.store.crypto.dto.realestate.MediaResponseDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealEstateResponseDTO {
    private Long id;
    private String title;
    private Double price;
    private String country;
    private String city;
    private String region;
    private SpecificationsResponseDTO specifications;
    private String description;
    private LocalDate listingDate;
    private LocationResponseDTO location;
    private List<FeatureResponseDTO> features;  // Use DTO for Feature
    private String propertyType; //i.e House
    private String propertySubType; //i.e Villa
    private List<MediaResponseDTO> mediaList;  // Use DTO for Media>
}
