package com.store.crypto.dto.user.user.onboarding;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IdentityVerificationDTO {
    private String governmentIdType;
    private String governmentIdNumber;
    @Lob
    @Column( columnDefinition = "BLOB")
    private String photoOfGovernmentId;
    @Lob
    @Column( columnDefinition = "BLOB")
    private String selfiePhotoWithId;
}
