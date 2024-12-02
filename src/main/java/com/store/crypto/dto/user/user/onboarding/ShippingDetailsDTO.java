package com.store.crypto.dto.user.user.onboarding;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShippingDetailsDTO {
    private String weight;
    private String dimensions;
}
