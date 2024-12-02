package com.store.crypto.dto.auth.response;

import com.store.crypto.model.user.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String token;
    private String role;
    private String email;
    private String fullName;
    private Permission permissions;
    private boolean onboardingStatus;
}
