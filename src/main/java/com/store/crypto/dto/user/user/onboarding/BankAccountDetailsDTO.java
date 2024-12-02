package com.store.crypto.dto.user.user.onboarding;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankAccountDetailsDTO {
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String swiftBicCode;
    private String iban;
}
