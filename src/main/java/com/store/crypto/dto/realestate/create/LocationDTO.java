package com.store.crypto.dto.realestate.create;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDTO {
    private Double latitude;
    private Double longitude;
}
