package com.store.crypto.model.user;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "identity_verification")
public class IdentityVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String governmentIdType;
    private String governmentIdNumber;
    @Lob
    @Column(name = "photo_of_government_id", columnDefinition = "LONGBLOB")
    private byte[] photoOfGovernmentId;

    @Lob
    @Column(name = "selfie_photo_with_id", columnDefinition = "LONGBLOB")
    private byte[] selfiePhotoWithId;

    @OneToOne(mappedBy = "identityVerification", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;
}
