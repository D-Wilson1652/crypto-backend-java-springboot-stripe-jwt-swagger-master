package com.store.crypto.model.user;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "agreements_and_acknowledgements")
public class AgreementsAndAcknowledgements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private boolean termsAndConditions;
    private boolean privacyPolicy;
    private boolean riskDisclosure;

    @OneToOne(mappedBy = "agreementsAndAcknowledgements", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;
}
