package com.store.crypto.dto.user.user.response;

import com.store.crypto.model.user.Permission;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private Integer id;
    private String fullName;
    private String email;
    private String role;
    private Permission permissions;
}
