package com.store.crypto.config.security;

import com.store.crypto.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final String[] authWhitelistedUrls = {"/api/v1/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/icons/**",
            "/static/**",
            "/resources/**"};
    private final String[] categoryWhitelistedUrls = {
            "/api/v1/category/list"
    };
    private final String[] realEstateWhitelistedUrls = {
            "/api/v1/real-estates/list",
            "/api/v1/real-estates/filter/**",
            "/api/v1/real-estates/get/{id}",
            "/api/v1/real-estates/upload-media/**",
            "/api/v1/features/list/**"
    };

    private final String[] userWhitelistedUrls = {
            "/api/v1/users/onboarding"
    };

    private final String[] carWhitelistedUrls = {
            "/api/v1/cars/list",
            "/api/v1/cars/filter/**",
            "/api/v1/cars/get/{id}"
    };

    private final String[] membershipWhitelistedUrls = {
            "/api/v1/membership/list",
            "/api/v1/membership/payment/success",
            "/api/v1/membership/payment/cancel",
            "/api/v1/membership/webhooks/stripe"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(authWhitelistedUrls).permitAll()
                                .requestMatchers(categoryWhitelistedUrls).permitAll()
                                .requestMatchers(realEstateWhitelistedUrls).permitAll()
                                .requestMatchers(userWhitelistedUrls).permitAll()
                                .requestMatchers(carWhitelistedUrls).permitAll()
                                .requestMatchers(membershipWhitelistedUrls).permitAll()
                                .anyRequest()
                                .authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
