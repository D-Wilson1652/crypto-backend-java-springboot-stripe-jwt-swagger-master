package com.store.crypto.model.user;

import jakarta.persistence.*;
import lombok.*;

/**
 * o	Bank Name:
 * o	Account Number:
 * o	Account Holder Name:
 * o	SWIFT/BIC Code:
 * o	IBAN (if applicable):
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bank_account_details")
public class BankAccountDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String swiftBicCode;
    private String iban;
}
