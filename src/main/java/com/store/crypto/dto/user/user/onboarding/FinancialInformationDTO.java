package com.store.crypto.dto.user.user.onboarding;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FinancialInformationDTO {
    private String sourceOfFunds;
    private String annualIncome;
    private BankAccountDetailsDTO bankAccountDetails;
    private String cryptoWalletAddress;

}
