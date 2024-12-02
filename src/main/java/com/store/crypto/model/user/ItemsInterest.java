package com.store.crypto.model.user;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items_interest")
public class ItemsInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String itemName;
    private String category;
    private String itemDescription;
    private String itemPrice;
    private boolean hasItemBeenAuthenticated; //If yes, upload the authentication certificate.
    @Lob
    @Column(name = "photo_of_authentication_certificate", columnDefinition = "LONGBLOB")
    private byte[] authenticationCertificate;
    private String itemCondition;
    private boolean warranty;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_details_id")
    private ShippingDetails shippingDetails;
    @OneToOne(mappedBy = "itemsInterest", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;
}
