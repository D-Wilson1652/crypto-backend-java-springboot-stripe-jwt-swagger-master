package com.store.crypto.model.user;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "regulatory_compliance")
public class RegulatoryCompliance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private boolean pep;
    private boolean residentOrCitizen;
    private boolean financialCrime;
    @OneToOne(mappedBy = "regulatoryCompliance", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;
}
