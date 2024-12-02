package com.store.crypto.controller.user;

import com.store.crypto.dto.user.user.onboarding.UserOnboardingRequest;
import com.store.crypto.dto.user.user.request.AddUserDTO;
import com.store.crypto.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Object> createUser(@Valid @RequestBody AddUserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PutMapping("/update/{id}")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Object> updateUser(@PathVariable Integer id, @RequestBody AddUserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @GetMapping("/get/{id}")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Object> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/list")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Object> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/delete/{id}")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/onboarding")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Object> onboardingUser(@Valid @RequestBody UserOnboardingRequest userOnboardingRequest) {
        return userService.onboardUser(userOnboardingRequest);
    }

    @GetMapping("/onboarding/status")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Object> onboardingUserStatus() {
        return userService.onboardUserStatus();
    }

    @GetMapping("/listing/stats")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Object> userListingStats() {
        return userService.getUserStats();
    }
}
