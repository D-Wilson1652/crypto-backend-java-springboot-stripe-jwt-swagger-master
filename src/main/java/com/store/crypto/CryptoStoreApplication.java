package com.store.crypto;


import com.store.crypto.utils.PreLoadDatabase;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CryptoStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoStoreApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private PreLoadDatabase preLoadDatabase;

    @PostConstruct
    public void insertSampleData() {
        preLoadDatabase.preloadRealEstateFeatures();
        preLoadDatabase.preloadRoles();
        preLoadDatabase.insertMembershipDetails();
        preLoadDatabase.insertDummyUsers(passwordEncoder());
        preLoadDatabase.insertCategories();
    }
}
