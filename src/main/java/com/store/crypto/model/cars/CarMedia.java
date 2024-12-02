package com.store.crypto.model.cars;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "car_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType; // Either "image" or "video"
    private String fileUrl;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}