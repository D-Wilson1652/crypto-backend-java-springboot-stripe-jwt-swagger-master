package com.store.crypto.dto.user.user.onboarding;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AgreementsAndAcknowledgementsDTO {
    private boolean termsAndConditions;
    private boolean privacyPolicy;
    private boolean riskDisclosure;
}
