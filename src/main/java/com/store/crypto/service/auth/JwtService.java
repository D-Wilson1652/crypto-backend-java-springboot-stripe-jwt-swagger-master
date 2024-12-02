package com.store.crypto.service.auth;

import com.store.crypto.model.user.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token);

    String generateToken(User userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);
}
