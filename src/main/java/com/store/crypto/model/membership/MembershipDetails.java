package com.store.crypto.model.membership;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "membership_details",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "plan_interval"}))
public class MembershipDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotBlank(message = "Interval is required")
    @Column(name = "plan_interval", unique = true)
    private String planInterval;
    @NotNull
    private Long price;
    @Column(name = "stripe_product_id", unique = true)
    @NotBlank
    private String stripeProductId;
    private String currency;
    private int listingLimit;
}
