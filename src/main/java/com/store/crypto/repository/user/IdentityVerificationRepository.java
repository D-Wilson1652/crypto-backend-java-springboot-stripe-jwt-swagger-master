package com.store.crypto.repository.user;

import com.store.crypto.model.user.IdentityVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityVerificationRepository extends JpaRepository<IdentityVerification, Integer> {
}
