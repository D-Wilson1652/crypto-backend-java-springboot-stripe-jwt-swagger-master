package com.store.crypto.model.membership;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.crypto.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_membership")
public class UserMembership {

    //Now, I have the customer id, product id, membership plan name, and new subscription id. I can update the database accordingly.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    @Column(name = "membership_name", unique = true)
    @NotBlank
    private String membershipName; //plan name from stripe
    private Long price; //price in cents
    private String currency; //usd or gbp
    private Date startDate; //subscription start date
    private Date endDate; //subscription expire date
    private String status; //active or cancelled or expired
    //    private String stripeProductId; //product id from stripe
    private String subscriptionId; //subscription id from stripe
    private String membershipInterval; //month or year

    private int listingLimit;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Column(name = "stripe_customer_id", unique = true)
    @NotBlank
    private String stripeCustomerId;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @PrePersist
    protected void onPrePersist() {
        lastUpdated = new Date();
    }


}
