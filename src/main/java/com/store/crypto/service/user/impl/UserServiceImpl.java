package com.store.crypto.service.user.impl;

import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.dto.user.user.onboarding.UserOnboardingRequest;
import com.store.crypto.dto.user.user.request.AddUserDTO;
import com.store.crypto.dto.user.user.response.UserResponseDTO;
import com.store.crypto.dto.user.user.stats.StatsResponseDTO;
import com.store.crypto.model.user.*;
import com.store.crypto.repository.membership.UserMembershipRepository;
import com.store.crypto.repository.user.PermissionRepository;
import com.store.crypto.repository.user.RoleRepository;
import com.store.crypto.repository.user.UserRepository;
import com.store.crypto.service.auth.JwtService;
import com.store.crypto.service.user.UserService;
import com.store.crypto.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final JwtService jwtService;
    private final SessionUtils sessionUtils;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public ResponseEntity<Object> createUser(AddUserDTO userDTO) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<Role> role = roleRepository.findByName(userDTO.getRole());
            if (role.isEmpty()) {
                response.setData(null);
                response.setMessage("Role not found against name: " + userDTO.getRole());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            Permission savedPermission = Permission.builder()
                    .createPermission(userDTO.getPermissions().isCreatePermission())
                    .readPermission(userDTO.getPermissions().isReadPermission())
                    .updatePermission(userDTO.getPermissions().isUpdatePermission())
                    .deletePermission(userDTO.getPermissions().isDeletePermission())
                    .build();
            savedPermission = permissionRepository.save(savedPermission);
            User user = User.builder()
                    .fullName(userDTO.getFullName())
                    .email(userDTO.getEmail())
                    .password(passwordEncoder.encode(userDTO.getPassword()))  // Encrypt password
                    .role(role.get())
                    .permissions(savedPermission)
                    .build();

            User savedUser = userRepository.save(user);
            // Map User entity to UserResponseDTO
            UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                    .id(savedUser.getId())
                    .fullName(savedUser.getFullName())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole().getName())  // Assuming Role has a getName() method
                    .permissions(savedUser.getPermissions())
                    .build();
            response.setData(userResponseDTO);
            response.setMessage("User created successfully.");
            response.setStatusCode(HttpStatus.CREATED.value());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> updateUser(Integer id, AddUserDTO userDTO) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty()) {
                response.setData(null);
                response.setMessage("User not found against ID: " + id);
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Optional<Role> role = roleRepository.findByName(userDTO.getRole());
            if (role.isEmpty()) {
                response.setData(null);
                response.setMessage("Role not found against name: " + userDTO.getRole());
                response.setStatusCode(HttpStatus.NO_CONTENT.value());
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }


            if (null != userDTO.getFullName() && !userDTO.getFullName().isEmpty()) {
                user.get().setFullName(userDTO.getFullName());
            } else {
                user.get().setFullName(null);
            }

            if (null != userDTO.getEmail() && !userDTO.getEmail().isEmpty()) {
                user.get().setEmail(userDTO.getEmail());
            } else {
                user.get().setEmail(null);
            }

            // Encrypt password
            if (null != userDTO.getPassword() && !userDTO.getPassword().isEmpty()) {
                user.get().setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            if (null != userDTO.getRole() && !userDTO.getRole().isEmpty()) {
                user.get().setRole(role.get());
            }

            if (null != userDTO.getPermissions()) {
                user.get().setPermissions(userDTO.getPermissions());
            }

            User updatedUser = userRepository.save(user.get());
            response.setData(updatedUser);
            response.setMessage("User updated successfully.");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> getUserById(Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                // Map User entity to UserResponseDTO
                UserResponseDTO userDTO = UserResponseDTO.builder()
                        .id(user.get().getId())
                        .fullName(user.get().getFullName())
                        .email(user.get().getEmail())
                        .role(user.get().getRole().getName())  // Assuming Role has a getName() method
                        .permissions(user.get().getPermissions())
                        .build();

                response.setData(userDTO);
                response.setMessage("User retrieved successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setData(null);
                response.setMessage("User not found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> getAllUsers() {
        GenericResponse response = new GenericResponse();
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                response.setData(null);
                response.setMessage("No users found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                // Map each User entity to UserResponseDTO
                List<UserResponseDTO> userDTOs = users.stream().map(user -> UserResponseDTO.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole().getName())  // Assuming Role has a getName() method
                                .permissions(user.getPermissions())
                                .build())
                        .collect(Collectors.toList());

                response.setData(userDTOs);
                response.setMessage("Users retrieved successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> deleteUser(Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                userRepository.delete(user.get());
                response.setData(null);
                response.setMessage("User deleted successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setData(null);
                response.setMessage("User not found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> onboardUser(UserOnboardingRequest userOnboardingRequest) {
        GenericResponse response = new GenericResponse();
        try {
            // Check if onboarding user email is the same user that is logged in right now
            if (!sessionUtils.getLoggedInUserUserName().equalsIgnoreCase(userOnboardingRequest.getEmail())) {
                response.setData(null);
                response.setMessage("Email that you are sending in the request isn't the email of the logged in user: " + userOnboardingRequest.getEmail());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Optional<User> existingUser = userRepository.findByEmail(Objects.requireNonNull(userOnboardingRequest.getEmail()));
            if (existingUser.isEmpty()) {
                response.setData(null);
                response.setMessage("User not found against email: " + Objects.requireNonNull(userOnboardingRequest.getEmail()));
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            // Check if user already onboarded or not.
            if (sessionUtils.getLoggedInUser().getAgreementsAndAcknowledgements() != null) {
                response.setData(null);
                response.setMessage("User is already onboarded with email: " + sessionUtils.getLoggedInUserUserName());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            // Create new user
            User user = existingUser.get();

            user.setGender(userOnboardingRequest.getGender() != null ? userOnboardingRequest.getGender() : null);
            user.setDateOfBirth(userOnboardingRequest.getDateOfBirth() != null ? userOnboardingRequest.getDateOfBirth() : null);
            user.setNationality(userOnboardingRequest.getNationality() != null ? userOnboardingRequest.getNationality() : null);

            if (userOnboardingRequest.getResidentialAddress() != null) {
                ResidentialAddress residentialAddress = ResidentialAddress.builder()
                        .streetAddress(userOnboardingRequest.getResidentialAddress().getStreetAddress() != null ? userOnboardingRequest.getResidentialAddress().getStreetAddress() : null)
                        .city(userOnboardingRequest.getResidentialAddress().getCity() != null ? userOnboardingRequest.getResidentialAddress().getCity() : null)
                        .state(userOnboardingRequest.getResidentialAddress().getState() != null ? userOnboardingRequest.getResidentialAddress().getState() : null)
                        .country(userOnboardingRequest.getResidentialAddress().getCountry() != null ? userOnboardingRequest.getResidentialAddress().getCountry() : null)
                        .postalCode(userOnboardingRequest.getResidentialAddress().getPostalCode() != null ? userOnboardingRequest.getResidentialAddress().getPostalCode() : null)
                        .build();
                user.setResidentialAddress(residentialAddress);
            }

            // 2. Identity Verification

            if (userOnboardingRequest.getIdentityVerification() != null) {
                IdentityVerification identityVerification = IdentityVerification.builder()
                        .governmentIdType(userOnboardingRequest.getIdentityVerification().getGovernmentIdType() != null ? userOnboardingRequest.getIdentityVerification().getGovernmentIdType() : null)
                        .governmentIdNumber(userOnboardingRequest.getIdentityVerification().getGovernmentIdNumber() != null ? userOnboardingRequest.getIdentityVerification().getGovernmentIdNumber() : null)
                        .build();

                // Decode the Base64 strings
                // Only decode if the string is not null and not empty
                if (userOnboardingRequest.getIdentityVerification().getPhotoOfGovernmentId() != null && !userOnboardingRequest.getIdentityVerification().getPhotoOfGovernmentId().isEmpty()) {
                    byte[] decodedPhotoOfGovernmentId = Optional.of(userOnboardingRequest.getIdentityVerification().getPhotoOfGovernmentId())
                            .map(Base64.getDecoder()::decode)
                            .orElse(null);
                    identityVerification.setPhotoOfGovernmentId(decodedPhotoOfGovernmentId);
                }

                if (userOnboardingRequest.getIdentityVerification().getSelfiePhotoWithId() != null && !userOnboardingRequest.getIdentityVerification().getSelfiePhotoWithId().isEmpty()) {
                    byte[] decodedSelfiePhotoWithId = Optional.of(userOnboardingRequest.getIdentityVerification().getSelfiePhotoWithId())
                            .map(Base64.getDecoder()::decode)
                            .orElse(null);
                    identityVerification.setSelfiePhotoWithId(decodedSelfiePhotoWithId);
                }

                user.setIdentityVerification(identityVerification);
            }

            // 3. Account Information
            user.setPreferredLanguage(userOnboardingRequest.getPreferredLanguage() != null ? userOnboardingRequest.getPreferredLanguage() : null);
            user.setReferralCode(userOnboardingRequest.getReferralCode() != null ? userOnboardingRequest.getReferralCode() : null);

            // 4. Financial Information
            if (userOnboardingRequest.getFinancialInformation() != null) {
                BankAccountDetails bankAccountDetails = BankAccountDetails.builder()
                        .bankName(userOnboardingRequest.getFinancialInformation().getBankAccountDetails() != null && userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getBankName() != null ? userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getBankName() : null)
                        .accountNumber(userOnboardingRequest.getFinancialInformation().getBankAccountDetails() != null && userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getAccountNumber() != null ? userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getAccountNumber() : null)
                        .accountHolderName(userOnboardingRequest.getFinancialInformation().getBankAccountDetails() != null && userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getAccountHolderName() != null ? userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getAccountHolderName() : null)
                        .swiftBicCode(userOnboardingRequest.getFinancialInformation().getBankAccountDetails() != null && userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getSwiftBicCode() != null ? userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getSwiftBicCode() : null)
                        .iban(userOnboardingRequest.getFinancialInformation().getBankAccountDetails() != null && userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getIban() != null ? userOnboardingRequest.getFinancialInformation().getBankAccountDetails().getIban() : null)
                        .build();
                FinancialInformation financialInformation = FinancialInformation.builder()
                        .sourceOfFunds(userOnboardingRequest.getFinancialInformation().getSourceOfFunds() != null ? userOnboardingRequest.getFinancialInformation().getSourceOfFunds() : null)
                        .annualIncome(userOnboardingRequest.getFinancialInformation().getAnnualIncome() != null ? userOnboardingRequest.getFinancialInformation().getAnnualIncome() : null)
                        .cryptoWalletAddress(userOnboardingRequest.getFinancialInformation().getCryptoWalletAddress() != null ? userOnboardingRequest.getFinancialInformation().getCryptoWalletAddress() : null)
                        .bankAccountDetails(bankAccountDetails)
                        .build();
                user.setFinancialInformation(financialInformation);
            }

            // 5. Item Description for Sale
            if (userOnboardingRequest.getItemsInterest() != null) {
                ShippingDetails shippingDetails = ShippingDetails.builder()
                        .weight(userOnboardingRequest.getItemsInterest().getShippingDetails() != null && userOnboardingRequest.getItemsInterest().getShippingDetails().getWeight() != null ? userOnboardingRequest.getItemsInterest().getShippingDetails().getWeight() : null)
                        .dimensions(userOnboardingRequest.getItemsInterest().getShippingDetails() != null && userOnboardingRequest.getItemsInterest().getShippingDetails().getDimensions() != null ? userOnboardingRequest.getItemsInterest().getShippingDetails().getDimensions() : null)
                        .build();

                ItemsInterest itemsInterest = ItemsInterest.builder()
                        .itemName(userOnboardingRequest.getItemsInterest().getItemName() != null ? userOnboardingRequest.getItemsInterest().getItemName() : null)
                        .category(userOnboardingRequest.getItemsInterest().getCategory() != null ? userOnboardingRequest.getItemsInterest().getCategory() : null)
                        .itemDescription(userOnboardingRequest.getItemsInterest().getItemDescription() != null ? userOnboardingRequest.getItemsInterest().getItemDescription() : null)
                        .itemPrice(userOnboardingRequest.getItemsInterest().getItemPrice() != null ? userOnboardingRequest.getItemsInterest().getItemPrice() : null)
                        .hasItemBeenAuthenticated(userOnboardingRequest.getItemsInterest().isHasItemBeenAuthenticated())
                        .itemCondition(userOnboardingRequest.getItemsInterest().getItemCondition() != null ? userOnboardingRequest.getItemsInterest().getItemCondition() : null)
                        .warranty(userOnboardingRequest.getItemsInterest().isWarranty())
                        .shippingDetails(shippingDetails)
                        .build();
                if (userOnboardingRequest.getItemsInterest().getAuthenticationCertificate() != null
                        && !userOnboardingRequest.getItemsInterest().getAuthenticationCertificate().isEmpty()
                        && userOnboardingRequest.getItemsInterest().isHasItemBeenAuthenticated()) {
                    byte[] decodedPhotoOfAuthenticationCertificate = Optional.of(userOnboardingRequest.getItemsInterest().getAuthenticationCertificate())
                            .map(Base64.getDecoder()::decode)
                            .orElse(null);
                    itemsInterest.setAuthenticationCertificate(decodedPhotoOfAuthenticationCertificate);
                }
                user.setItemsInterest(itemsInterest);
            }

            // 6. Regulatory Compliance
            if (userOnboardingRequest.getRegulatoryCompliance() != null) {
                RegulatoryCompliance regulatoryCompliance = RegulatoryCompliance.builder()
                        .pep(userOnboardingRequest.getRegulatoryCompliance().isPep())
                        .residentOrCitizen(userOnboardingRequest.getRegulatoryCompliance().isResidentOrCitizen())
                        .financialCrime(userOnboardingRequest.getRegulatoryCompliance().isFinancialCrime())
                        .build();
                user.setRegulatoryCompliance(regulatoryCompliance);
            }

            // 7. Agreements and Acknowledgements
            if (userOnboardingRequest.getAgreementsAndAcknowledgements() != null) {
                AgreementsAndAcknowledgements agreementsAndAcknowledgements = AgreementsAndAcknowledgements.builder()
                        .termsAndConditions(userOnboardingRequest.getAgreementsAndAcknowledgements().isTermsAndConditions())
                        .privacyPolicy(userOnboardingRequest.getAgreementsAndAcknowledgements().isPrivacyPolicy())
                        .riskDisclosure(userOnboardingRequest.getAgreementsAndAcknowledgements().isRiskDisclosure())
                        .build();
                user.setAgreementsAndAcknowledgements(agreementsAndAcknowledgements);
            }

            // 8. Additional Information (Optional)
            user.setHearUsFrom(userOnboardingRequest.getHearUsFrom() != null ? userOnboardingRequest.getHearUsFrom() : null);
            user.setCommentsOrRequest(userOnboardingRequest.getCommentsOrRequest() != null ? userOnboardingRequest.getCommentsOrRequest() : null);

            // 9. Confirmation
            user.setSignature(userOnboardingRequest.getSignature() != null ? userOnboardingRequest.getSignature() : null);
            user.setPrintName(userOnboardingRequest.getPrintName() != null ? userOnboardingRequest.getPrintName() : null);
            user.setDateOfSignature(userOnboardingRequest.getDateOfSignature() != null ? userOnboardingRequest.getDateOfSignature() : null);

            // Save user
            userRepository.save(user);
            response.setData(user.getEmail());
            response.setMessage("Onboarding completed successfully.");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error onboarding user: {}", e.getMessage());
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> onboardUserStatus() {
        log.info("Request came from the user with email: {}", sessionUtils.getLoggedInUserUserName());
        GenericResponse response = new GenericResponse();
        try {
            User user = sessionUtils.getLoggedInUser();
            Map<String, Boolean> onboardingStatus = new HashMap<>();
            if (user != null) {
                if (user.getAgreementsAndAcknowledgements() != null) {
                    onboardingStatus.put("onboardingStatus", true);
                    response.setData(onboardingStatus);
                    response.setMessage("User onboarding status fetched successfully.");
                    response.setStatusCode(HttpStatus.OK.value());
                } else {
                    onboardingStatus.put("onboardingStatus", false);
                    response.setData(onboardingStatus);
                    response.setMessage("User onboarding status fetched successfully.");
                    response.setStatusCode(HttpStatus.OK.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

                log.info("Response: {}", response);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setData(null);
                response.setMessage("User not found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error fetching user onboarding status: {}", e.getMessage());
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getUserStats() {
        GenericResponse response = new GenericResponse();
        List<StatsResponseDTO> statsResponseDTOList = new ArrayList<>();
        try {
            User user = sessionUtils.getLoggedInUser();
            if (user != null) {
                StatsResponseDTO realEstateCountDetails = new StatsResponseDTO();
                realEstateCountDetails.setTitle("Real Estate");
                realEstateCountDetails.setDescription("Total Real Estate Listings");
                realEstateCountDetails.setCount(user.getRealEstates().size());
                statsResponseDTOList.add(realEstateCountDetails);

                StatsResponseDTO carsCountDetails = new StatsResponseDTO();
                carsCountDetails.setTitle("Cars");
                carsCountDetails.setDescription("Total Cars Listings");
                carsCountDetails.setCount(user.getCars().size());
                statsResponseDTOList.add(carsCountDetails);

                StatsResponseDTO watchesCountDetails = new StatsResponseDTO();
                watchesCountDetails.setTitle("Watches");
                watchesCountDetails.setDescription("Total Cars Listings");
                watchesCountDetails.setCount(0);
                statsResponseDTOList.add(watchesCountDetails);

                StatsResponseDTO motorCycleCountDetails = new StatsResponseDTO();
                motorCycleCountDetails.setTitle("MotorCycles");
                motorCycleCountDetails.setDescription("Total MotorCycles Listings");
                motorCycleCountDetails.setCount(0);
                statsResponseDTOList.add(motorCycleCountDetails);

                StatsResponseDTO jetsCountDetails = new StatsResponseDTO();
                jetsCountDetails.setTitle("Jets");
                jetsCountDetails.setDescription("Total Jets Listings");
                jetsCountDetails.setCount(0);
                statsResponseDTOList.add(jetsCountDetails);

                StatsResponseDTO helicopterCountDetails = new StatsResponseDTO();
                helicopterCountDetails.setTitle("Helicopters");
                helicopterCountDetails.setDescription("Total Helicopters Listings");
                helicopterCountDetails.setCount(0);
                statsResponseDTOList.add(helicopterCountDetails);


                StatsResponseDTO totalListingsCountDetails = new StatsResponseDTO();
                totalListingsCountDetails.setTitle("Total Listings");
                totalListingsCountDetails.setDescription("Total Listings");
                totalListingsCountDetails.setCount(realEstateCountDetails.getCount()
                        + carsCountDetails.getCount()
                        + watchesCountDetails.getCount()
                        + motorCycleCountDetails.getCount()
                        + jetsCountDetails.getCount()
                        + helicopterCountDetails.getCount()
                );
                statsResponseDTOList.add(totalListingsCountDetails);


                StatsResponseDTO listingsLimitCountDetails = new StatsResponseDTO();
                listingsLimitCountDetails.setTitle("Listings Limit");
                listingsLimitCountDetails.setDescription("Max Listing Limit");
                userMembershipRepository.findByUser(user).ifPresent(userMembership -> {
                    listingsLimitCountDetails.setCount(userMembership.getListingLimit());
                });


                StatsResponseDTO remainingListingsLimitCountDetails = new StatsResponseDTO();
                remainingListingsLimitCountDetails.setTitle("Listings Limit");
                remainingListingsLimitCountDetails.setDescription("Max Listing Limit");
                if (listingsLimitCountDetails.getCount() != 0) {
                    remainingListingsLimitCountDetails.setCount(listingsLimitCountDetails.getCount() - totalListingsCountDetails.getCount());
                }


                statsResponseDTOList.add(remainingListingsLimitCountDetails);
                response.setMessage("List of all user stats fetched successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                response.setData(statsResponseDTOList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setData(null);
                response.setMessage("User isn't logged in.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error fetching user onboarding status: {}", e.getMessage());
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}