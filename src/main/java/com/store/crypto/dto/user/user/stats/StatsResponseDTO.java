package com.store.crypto.dto.user.user.stats;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsResponseDTO {
    private String title;
    private int count;
    private String description;
}
