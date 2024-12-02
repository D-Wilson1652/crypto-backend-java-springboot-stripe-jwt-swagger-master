package com.store.crypto.dto.category;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItemDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate createdAt;
    private String price;
    private String category; // Can be 'Car' or 'RealEstate'

}
