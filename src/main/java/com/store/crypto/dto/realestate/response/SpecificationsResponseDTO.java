package com.store.crypto.dto.realestate.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecificationsResponseDTO {
    private Long id;
    private Integer numOfBeds;
    private Integer numOfBaths;
    private Double areaInSqft;
    private Double pricePerSqft;
}
