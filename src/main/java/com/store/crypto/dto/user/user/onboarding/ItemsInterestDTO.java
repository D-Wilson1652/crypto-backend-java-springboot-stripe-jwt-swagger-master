package com.store.crypto.dto.user.user.onboarding;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemsInterestDTO {
    private String itemName;
    private String category;
    private String itemDescription;
    private String itemPrice;
    //    private String photosOfItem;
    private boolean hasItemBeenAuthenticated; //If yes, upload the authentication certificate.
    @Lob
    @Column( columnDefinition = "BLOB")
    private String authenticationCertificate;
    private String itemCondition;
    private boolean warranty;
    private ShippingDetailsDTO shippingDetails;
}
