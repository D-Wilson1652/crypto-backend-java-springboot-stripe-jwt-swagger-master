package com.store.crypto.service.user;

import com.store.crypto.dto.user.user.onboarding.UserOnboardingRequest;
import com.store.crypto.dto.user.user.request.AddUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();

    ResponseEntity<Object> createUser(AddUserDTO userDTO);

    ResponseEntity<Object> updateUser(Integer id, AddUserDTO userDTO);

    ResponseEntity<Object> getUserById(Integer id);

    ResponseEntity<Object> getAllUsers();

    ResponseEntity<Object> deleteUser(Integer id);

    ResponseEntity<Object> onboardUser(UserOnboardingRequest userOnboardingRequest);

    ResponseEntity<Object> onboardUserStatus();

    ResponseEntity<Object> getUserStats();


}
