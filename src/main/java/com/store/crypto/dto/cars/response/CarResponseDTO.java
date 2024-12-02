package com.store.crypto.dto.cars.response;

import com.store.crypto.dto.cars.media.CarMediaResponseDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarResponseDTO {
    private Long id;
    private String name;
    private String make; //for the hyperlinks
    private String model; //for the hyperlinks
    private String price; //Price on request
    private CarLocationResponseDTO carLocation;
    private CarDetailsResponseDTO carDetails;
    private String description;
    private CarAdditionalInfoResponseDTO carAdditionalInfo;
    private LocalDate listingDate = LocalDate.now();
    private List<CarMediaResponseDTO> mediaList;  // Use DTO for Media>
}
