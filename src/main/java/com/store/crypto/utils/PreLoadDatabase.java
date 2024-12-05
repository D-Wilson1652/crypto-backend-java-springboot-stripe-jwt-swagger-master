package com.store.crypto.utils;

import com.store.crypto.dto.auth.request.SignUpRequest;
import com.store.crypto.model.category.Category;
import com.store.crypto.model.membership.MembershipDetails;
import com.store.crypto.model.realestate.Feature;
import com.store.crypto.model.user.Permission;
import com.store.crypto.model.user.Role;
import com.store.crypto.model.user.User;
import com.store.crypto.repository.category.CategoryRepository;
import com.store.crypto.repository.membership.MembershipDetailsRepository;
import com.store.crypto.repository.realestate.FeatureRepository;
import com.store.crypto.repository.user.RoleRepository;
import com.store.crypto.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PreLoadDatabase {
    private final FeatureRepository featureRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MembershipDetailsRepository membershipDetailsRepository;
    private final CategoryRepository categoryRepository;

    public void insertCategories() {
        Optional<Category> realEstateCategory = categoryRepository.findByName("Real Estate");
        if (realEstateCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Real Estate")
                    .build());
        }

        Optional<Category> helicopterCategory = categoryRepository.findByName("Helicopters");
        if (helicopterCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Helicopters")
                    .build());
        }

        Optional<Category> watchCategory = categoryRepository.findByName("Watches");
        if (watchCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Watches")
                    .build());
        }

        Optional<Category> jetCategory = categoryRepository.findByName("Jets");
        if (jetCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Jets")
                    .build());
        }

        //Motorcycles

        Optional<Category> motorcycleCategory = categoryRepository.findByName("Motorcycles");
        if (motorcycleCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Motorcycles")
                    .build());
        }

        //Jewelry

        Optional<Category> jewelleryCategory = categoryRepository.findByName("Jewelry");
        if (jewelleryCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Jewelry")
                    .build());
        }

        //Collectibles

        Optional<Category> collectiblesCategory = categoryRepository.findByName("Collectibles");
        if (collectiblesCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Collectibles")
                    .build());
        }

        //Rentals

        Optional<Category> rentalsCategory = categoryRepository.findByName("Rentals");
        if (rentalsCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Rentals")
                    .build());
        }

        //Journal

        Optional<Category> journalCategory = categoryRepository.findByName("Journal");
        if (journalCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Journal")
                    .build());
        }

        Optional<Category> yachtsCategory = categoryRepository.findByName("Yachts");
        if (yachtsCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Yachts")
                    .build());
        }

        Optional<Category> carCategory = categoryRepository.findByName("Cars");
        if (carCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("Cars")
                    .build());
        }

        Optional<Category> allCategory = categoryRepository.findByName("All");
        if (allCategory.isEmpty()) {
            categoryRepository.save(Category.builder()
                    .name("All")
                    .build());
        }


    }

    public void preloadRealEstateFeatures() {
        Optional<Feature> feature1 = featureRepository.findByName("Beachfront");
        if (feature1.isEmpty()) {
            featureRepository.save(Feature.builder()
                    .name("Beachfront")
                    .category("Lot")
                    .imageUrl("/icons/vacations.png")
                    .build());
        }

        Optional<Feature> feature2 = featureRepository.findByName("Water View");
        if (feature2.isEmpty()) {
            featureRepository.save(Feature.builder()
                    .name("Water View")
                    .category("Lot")
                    .imageUrl("/icons/water-view.png")
                    .build());
        }

        Optional<Feature> feature3 = featureRepository.findByName("Renovated");
        if (feature3.isEmpty()) {
            featureRepository.save(Feature.builder()
                    .name("Renovated")
                    .category("Lot")
                    .imageUrl("/icons/renovated.png")
                    .build());
        }

        Optional<Feature> feature4 = featureRepository.findByName("Office");
        if (feature4.isEmpty()) {
            featureRepository.save(Feature.builder()
                    .name("Office")
                    .category("Indoor")
                    .imageUrl("/icons/office.png")
                    .build());
        }

        Optional<Feature> feature5 = featureRepository.findByName("Fitness Center / Gym");
        if (feature5.isEmpty()) {
            featureRepository.save(Feature.builder()
                    .name("Fitness Center / Gym")
                    .category("Indoor")
                    .imageUrl("/icons/gym.png")
                    .build());
        }

        Optional<Feature> feature6 = featureRepository.findByName("Library");
        if (feature6.isEmpty()) {
            featureRepository.save(Feature.builder()
                    .name("Library")
                    .category("Indoor")
                    .imageUrl("/icons/library.png")
                    .build());
        }


        Optional<Feature> feature7 = featureRepository.findByName("Terrace");
        if (feature7.isEmpty()) {
            featureRepository.save(Feature.builder()
                    .name("Terrace")
                    .category("Outdoor")
                    .imageUrl("/icons/terrace.png")
                    .build());
        }

        Optional<Feature> feature8 = featureRepository.findByName("Pool");
        if (feature8.isEmpty()) {
            featureRepository.save(Feature.builder()
                    .name("Pool")
                    .category("Outdoor")
                    .imageUrl("/icons/pool.png")
                    .build());
        }


    }

    public void preloadRoles() {
        Optional<Role> role = roleRepository.findByName("ADMIN");
        if (role.isEmpty()) {
            Role admin = new Role("ADMIN");
            roleRepository.save(admin);
        }

        Optional<Role> userRole = roleRepository.findByName("USER");
        if (userRole.isEmpty()) {
            Role newUserRole = new Role("USER");
            roleRepository.save(newUserRole);
        }

        Optional<Role> agentRole = roleRepository.findByName("AGENT");
        if (agentRole.isEmpty()) {
            Role newAgentRole = new Role("AGENT");
            roleRepository.save(newAgentRole);
        }

        Optional<Role> managerRole = roleRepository.findByName("MANAGER");
        if (managerRole.isEmpty()) {
            Role newManagerRole = new Role("MANAGER");
            roleRepository.save(newManagerRole);
        }
    }

    public void insertDummyUsers(PasswordEncoder passwordEncoder) {
        insertAdmin(SignUpRequest.builder()
                .email("kamran@kamran.com")
                .fullName("Kamran Abbasi")
                .password(passwordEncoder.encode("Kamran@123"))
                .phoneNumber("+44712345233")
                .build());

        insertAdmin(SignUpRequest.builder()
                .email("rohan@rohan.com")
                .fullName("Rohan Awan")
                .password(passwordEncoder.encode("Rohan@123"))
                .phoneNumber("+44712345234")
                .build());

        insertAdmin(SignUpRequest.builder()
                .email("arham@arham.com")
                .fullName("Arham Khawar")
                .password(passwordEncoder.encode("Arham@123"))
                .phoneNumber("+44712345235")
                .build());

        insertAdmin(SignUpRequest.builder()
                .email("admin@admin.com")
                .fullName("Admin")
                .password(passwordEncoder.encode("Admin@123"))
                .phoneNumber("+44712345236")
                .build());

        insertUser(SignUpRequest.builder()
                .email("user@user.com")
                .fullName("User")
                .password(passwordEncoder.encode("User@123"))
                .phoneNumber("+44712345237")
                .build());

        insertAgent(SignUpRequest.builder()
                .email("agent@agent.com")
                .fullName("Agent")
                .password(passwordEncoder.encode("Agent@123"))
                .phoneNumber("+44712345238")
                .build());


    }

    public void insertAdmin(SignUpRequest request) {
        //Check if user already exists
        if (!userRepository.existsByEmail(request.getEmail())) {
            Permission permissions = new Permission();
            permissions.setReadPermission(true);
            permissions.setUpdatePermission(true);
            permissions.setDeletePermission(true);
            permissions.setCreatePermission(true);
            Optional<Role> role = roleRepository.findByName("ADMIN");
            User admin = null;
            if (role.isPresent()) {
                admin = User.builder()
                        .fullName(request.getFullName())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .phoneNumber(request.getPhoneNumber())
                        .role(role.get())
                        .permissions(permissions)
                        .build();
                userRepository.save(admin);
            }
        }
    }

    public void insertUser(SignUpRequest request) {
        //Check if user already exists
        if (!userRepository.existsByEmail(request.getEmail())) {
            Permission permissions = new Permission();
            permissions.setReadPermission(true);
            permissions.setUpdatePermission(true);
            Optional<Role> role = roleRepository.findByName("USER");
            if (role.isPresent()) {
                User admin = User.builder()
                        .fullName(request.getFullName())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .phoneNumber(request.getPhoneNumber())
                        .role(role.get())
                        .permissions(permissions)
                        .build();
                userRepository.save(admin);

            }
        }
    }

    public void insertAgent(SignUpRequest request) {
        //Check if user already exists
        if (!userRepository.existsByEmail(request.getEmail())) {
            Permission permissions = new Permission();
            permissions.setReadPermission(true);
            permissions.setCreatePermission(true);
            Optional<Role> role = roleRepository.findByName("AGENT");
            if (role.isPresent()) {
                User agent = User.builder()
                        .fullName(request.getFullName())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .phoneNumber(request.getPhoneNumber())
                        .role(role.get())
                        .permissions(permissions)
                        .build();
                userRepository.save(agent);

            }
        }
    }

    public void insertMembershipDetails() {
        if (!membershipDetailsRepository.existsByName("Carbon")) {
            MembershipDetails free = new MembershipDetails();
            free.setName("Carbon");
            free.setDescription("Carbon Membership");
            free.setPrice(2500L);
            free.setStripeProductId("prod_QpZRywfNXiOk52");
            free.setCurrency("gbp");
            free.setListingLimit(10);
            free.setPlanInterval("year");
            membershipDetailsRepository.save(free);
        }


        if (!membershipDetailsRepository.existsByName("Silver")) {
            MembershipDetails free = new MembershipDetails();
            free.setName("Silver");
            free.setDescription("Silver Membership");
            free.setPrice(4999L);
            free.setStripeProductId("prod_QopcJMubNoEyqx");
            free.setCurrency("gbp");
            free.setListingLimit(25);
            free.setPlanInterval("year");
            membershipDetailsRepository.save(free);
        }

        if (!membershipDetailsRepository.existsByName("Gold")) {
            MembershipDetails free = new MembershipDetails();
            free.setName("Gold");
            free.setDescription("Gold Membership");
            free.setPrice(6999L);
            free.setStripeProductId("prod_QpZQloQak0GqOt");
            free.setCurrency("gbp");
            free.setListingLimit(55);
            free.setPlanInterval("year");
            membershipDetailsRepository.save(free);
        }


        if (!membershipDetailsRepository.existsByName("Platinum")) {
            MembershipDetails free = new MembershipDetails();
            free.setName("Platinum");
            free.setDescription("Platinum Membership");
            free.setPrice(9999L);
            free.setStripeProductId("prod_Qpqw9q23kHN9ls");
            free.setCurrency("gbp");
            free.setListingLimit(75);
            free.setPlanInterval("2 years");
            membershipDetailsRepository.save(free);
        }


        if (!membershipDetailsRepository.existsByName("Diamond")) {
            MembershipDetails free = new MembershipDetails();
            free.setName("Diamond");
            free.setDescription("Diamond Membership");
            free.setPrice(14999L);
            free.setStripeProductId("prod_QpqsxM3JMY2lxr");
            free.setCurrency("gbp");
            free.setListingLimit(100);
            free.setPlanInterval("3 years");
            membershipDetailsRepository.save(free);
        }
    }
}
