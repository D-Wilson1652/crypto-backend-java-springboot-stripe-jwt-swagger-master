package com.store.crypto.dto.realestate.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponseDTO {
    private Double latitude;
    private Double longitude;
}
