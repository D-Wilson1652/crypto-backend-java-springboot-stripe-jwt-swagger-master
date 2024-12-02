package com.store.crypto.dto.cars.create;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCarDTO {
    private String title;
    private String make; //for the hyperlinks
    private String model; //for the hyperlinks
    private String price; //Price on request
    private CarLocationDTO carLocation;
    private CarDetailsDTO carDetails;
    private String description;
    private CarAdditionalInfoDTO carAdditionalInfo;
}
