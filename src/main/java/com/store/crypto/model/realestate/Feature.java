package com.store.crypto.model.realestate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    //Image link.
    private String imageUrl;
    private String category;

    @JsonIgnore
    @ManyToMany(mappedBy = "features")
    private List<RealEstate> realEstates;
}

