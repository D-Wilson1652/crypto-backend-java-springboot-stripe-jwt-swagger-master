package com.store.crypto.repository.user;

import com.store.crypto.model.user.FinancialInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialInformationRepository extends JpaRepository<FinancialInformation, Integer> {
}
