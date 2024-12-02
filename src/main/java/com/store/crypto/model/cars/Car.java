package com.store.crypto.model.cars;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.crypto.model.category.Category;
import com.store.crypto.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String make; //for the hyperlinks
    private String model; //for the hyperlinks
    private String price; //Price on request

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_location_id")
    private CarLocation carLocation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_details_id")
    private CarDetails carDetails;

    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_additional_info_id")
    private CarAdditionalInfo carAdditionalInfo;

    private LocalDate listingDate = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CarMedia> carMedia;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
}
