package com.store.crypto.controller.auth;

import com.store.crypto.dto.auth.request.SignUpRequest;
import com.store.crypto.dto.auth.request.SigninRequest;
import com.store.crypto.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@Valid @RequestBody SigninRequest request) {
        return authenticationService.signin(request);
    }
}
