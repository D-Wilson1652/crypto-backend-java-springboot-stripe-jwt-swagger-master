package com.store.crypto.dto.realestate.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeatureResponseDTO {
    private Long id;
    private String name;
    private String category;
}
