package com.store.crypto.model.user;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "residential_address")
public class ResidentialAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    @OneToOne(mappedBy = "residentialAddress", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;
}
