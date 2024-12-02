package com.store.crypto.dto.realestate.create;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecificationsDTO {
    private Integer numOfBeds;
    private Integer numOfBaths;
    private Double areaInSqft;
    private Double pricePerSqft;
}
