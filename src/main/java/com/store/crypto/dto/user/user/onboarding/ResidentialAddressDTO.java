package com.store.crypto.dto.user.user.onboarding;

import lombok.*;

/**
 * o	Street Address:
 * o	City:
 * o	State/Province:
 * o	Country:
 * o	Postal/Zip Code:
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResidentialAddressDTO {
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}
