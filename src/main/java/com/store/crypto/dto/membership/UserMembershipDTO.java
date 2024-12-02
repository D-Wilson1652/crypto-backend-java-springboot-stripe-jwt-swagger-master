package com.store.crypto.dto.membership;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserMembershipDTO {
    private String membershipName; //plan name from stripe
    private Date endDate; //subscription expire date
    private boolean isActive; //active or cancelled or expired
}
