package com.store.crypto.model.realestate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.crypto.model.category.Category;
import com.store.crypto.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "real_estate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEstate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Double price;

    private String country;
    private String city;
    private String region;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "specifications_id")
    private RealEstateSpecifications realEstateSpecifications;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private LocalDate listingDate;

    @Embedded
    private Location location;

    @ManyToMany
    @JoinTable(
            name = "real_estate_features",
            joinColumns = @JoinColumn(name = "real_estate_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private List<Feature> features;

    //From the frontend, he can choose property type and sub type
    private String propertyType; //i.e House
    private String propertySubType; //i.e Villa

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "realEstate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RealEstateMedia> realEstateMediaList;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

}
