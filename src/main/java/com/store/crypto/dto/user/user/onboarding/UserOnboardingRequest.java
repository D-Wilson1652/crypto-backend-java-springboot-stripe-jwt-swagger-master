package com.store.crypto.dto.user.user.onboarding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOnboardingRequest {

    @NotNull(message = "User email is required")
    private String email;

    //1. Personal Information
//    @NotBlank(message = "Full name is required")
//    private String fullName;
    @NotBlank(message = "Gender is required")
    private String gender;
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    @NotBlank(message = "Nationality is required")
    private String nationality;
    //    @NotBlank
//    @Length(max = 15, message = "Phone number should not exceed 15 characters")
//    private String phoneNumber;
    @NotNull(message = "Residential address is required")
    private ResidentialAddressDTO residentialAddress;
//    @NotBlank(message = "Email is required")
//    private String email;


    //Make "identityVerification" transient or serializable.
    //2. Identity Verification
    @NotNull(message = "Identity verification is required")
    private IdentityVerificationDTO identityVerification;


    //3. Account Information
    //don't need to add confirm password (frontend will deal with the matchup)
//    @NotBlank(message = "Password is required")
//    private String password;
    @NotBlank(message = "Preferred language is required")
    private String preferredLanguage;
    private String referralCode;

    //4. Financial Information
    private FinancialInformationDTO financialInformation;

    //5. Item Description for Sale
    private ItemsInterestDTO itemsInterest;

    //6. Regulatory Compliance
    private RegulatoryComplianceDTO regulatoryCompliance;


    //7. Agreements and Acknowledgements
    private AgreementsAndAcknowledgementsDTO agreementsAndAcknowledgements;

    //8. Additional Information (Optional)
    private String hearUsFrom;
    private String commentsOrRequest;

    //9. Confirmation
    private String signature;
    private String printName;
    private LocalDate dateOfSignature;
}
