package com.store.crypto.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigninRequest {
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
