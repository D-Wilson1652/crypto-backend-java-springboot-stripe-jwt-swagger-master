package com.store.crypto.model.user;

import com.store.crypto.model.cars.Car;
import com.store.crypto.model.membership.UserMembership;
import com.store.crypto.model.realestate.RealEstate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //1. Personal Information
    @NotBlank(message = "Full name is required")
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String nationality;
    @Column(unique = true)
    @NotBlank
    @Length(max = 15, message = "Phone number should not exceed 15 characters")
    private String phoneNumber;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "residential_address_id")
    private ResidentialAddress residentialAddress;
    @Column(unique = true)
    @NotBlank(message = "Email is required")
    private String email;

    //2. Identity Verification
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "identity_verification_id")
    private IdentityVerification identityVerification;


    //3. Account Information
    //don't need to add confirm password (frontend will deal with the matchup)
    @NotBlank(message = "Password is required")
    private String password;
    private String preferredLanguage;
    private String referralCode;

    //4. Financial Information
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "financial_information_id")
    private FinancialInformation financialInformation;

    //5. Item Description for Sale
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "items_interest_id")
    private ItemsInterest itemsInterest;

    //6. Regulatory Compliance
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "regulatory_compliance_id")
    private RegulatoryCompliance regulatoryCompliance;


    //7. Agreements and Acknowledgements
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "agreements_and_acknowledgements_id")
    private AgreementsAndAcknowledgements agreementsAndAcknowledgements;

    //8. Additional Information (Optional)
    private String hearUsFrom;
    @Column(columnDefinition = "TEXT")
    private String commentsOrRequest;

    //9. Confirmation
    private String signature;
    private String printName;
    private LocalDate dateOfSignature;

    // User Role.
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "permissions_id")
    private Permission permissions;

    @OneToMany(mappedBy = "user")
    private List<RealEstate> realEstates;

    @OneToMany(mappedBy = "user")
    private List<Car> cars;

    //User Membership
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserMembership userMembership;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getUsername() {
        // email in our case
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
