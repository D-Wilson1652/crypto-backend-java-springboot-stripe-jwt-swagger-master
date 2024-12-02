package com.store.crypto.dto.cars.update;

import com.store.crypto.model.cars.CarAdditionalInfo;
import com.store.crypto.model.cars.CarDetails;
import com.store.crypto.model.cars.CarLocation;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCarDTO {
    private Long id;
    private String name;
    private String make; //for the hyperlinks
    private String model; //for the hyperlinks
    private String price; //Price on request
    private CarLocation carLocation;
    private CarDetails carDetails;
    private String description;
    private CarAdditionalInfo carAdditionalInfo;


}
