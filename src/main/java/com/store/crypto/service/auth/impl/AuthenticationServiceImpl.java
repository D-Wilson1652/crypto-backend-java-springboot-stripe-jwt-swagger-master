package com.store.crypto.service.auth.impl;

import com.store.crypto.dto.auth.request.SignUpRequest;
import com.store.crypto.dto.auth.request.SigninRequest;
import com.store.crypto.dto.auth.response.JwtAuthenticationResponse;
import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.model.user.Permission;
import com.store.crypto.model.user.Role;
import com.store.crypto.model.user.User;
import com.store.crypto.repository.user.PermissionRepository;
import com.store.crypto.repository.user.RoleRepository;
import com.store.crypto.repository.user.UserRepository;
import com.store.crypto.service.auth.AuthenticationService;
import com.store.crypto.service.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public ResponseEntity<Object> signup(SignUpRequest request) {
        GenericResponse genericResponse = new GenericResponse();
        //Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            genericResponse.setMessage("User already exists against this email");
            genericResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            genericResponse.setData(null);
            return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
        }

        //Check if user exists against that phone number.
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            genericResponse.setMessage("User already exists against this phone number");
            genericResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            genericResponse.setData(null);
            return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
        }

        Permission permissions = new Permission();
        permissions.setReadPermission(true);
        permissions.setUpdatePermission(true);
        Optional<Role> role = roleRepository.findByName("USER");
        User user = null;
        if (role.isEmpty()) {
            Role userRole = new Role("USER");
            Role savedRole = roleRepository.save(userRole);
            Permission savedPermission = permissionRepository.save(permissions);
            user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNumber(request.getPhoneNumber())
                    .role(savedRole)
                    .permissions(savedPermission)
                    .build();
        } else {
            Permission savedPermission = permissionRepository.save(permissions);
            user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNumber(request.getPhoneNumber())
                    .role(role.get())
                    .permissions(savedPermission)
                    .build();
        }
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .token(jwt)
                .email(user.getEmail())
                .role(user.getRole().getName())
                .fullName(user.getFullName())
                .permissions(user.getPermissions())
                .build();

        genericResponse.setData(response);
        genericResponse.setMessage("Signup successful");
        genericResponse.setStatusCode(HttpStatus.OK.value());
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> signin(SigninRequest request) {
        GenericResponse genericResponse = new GenericResponse();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            genericResponse.setMessage("Invalid email or password");
            genericResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            genericResponse.setData(null);
            return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
        }

        var user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return new ResponseEntity<>("User does not exist against this email", HttpStatus.BAD_REQUEST);
        }
        var jwt = jwtService.generateToken(user.get());
        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .token(jwt)
                .email(user.get().getEmail())
                .role(user.get().getRole().getName())
                .fullName(user.get().getFullName())
                .permissions(user.get().getPermissions())
                .build();

        if (user.get().getAgreementsAndAcknowledgements() != null) {
            response.setOnboardingStatus(true);
        }
        genericResponse.setData(response);
        genericResponse.setMessage("Login successful");
        genericResponse.setStatusCode(HttpStatus.OK.value());
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
}