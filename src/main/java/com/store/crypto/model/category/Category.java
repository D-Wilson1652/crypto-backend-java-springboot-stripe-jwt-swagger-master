package com.store.crypto.model.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.crypto.model.cars.Car;
import com.store.crypto.model.realestate.RealEstate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<RealEstate> realEstates;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Car> cars;
}
