package com.store.crypto.service.auth;

import com.store.crypto.dto.auth.request.SignUpRequest;
import com.store.crypto.dto.auth.request.SigninRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<Object> signup(SignUpRequest request);

    ResponseEntity<Object> signin(SigninRequest request);
}
