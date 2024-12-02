package com.store.crypto.repository.user;

import com.store.crypto.model.user.RegulatoryCompliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegulatoryComplianceRepository extends JpaRepository<RegulatoryCompliance, Integer> {
}
