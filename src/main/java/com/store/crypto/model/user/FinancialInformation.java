package com.store.crypto.model.user;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "financial_information")
public class FinancialInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String sourceOfFunds;
    private String annualIncome;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_account_details_id")
    private BankAccountDetails bankAccountDetails;
    private String cryptoWalletAddress;

    @OneToOne(mappedBy = "financialInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;
}
