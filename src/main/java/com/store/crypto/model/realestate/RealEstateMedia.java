package com.store.crypto.model.realestate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "real_estate_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEstateMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType; // Either "image" or "video"
    private String fileUrl;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "real_estate_id")
    private RealEstate realEstate;
}