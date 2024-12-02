package com.store.crypto.repository.user;

import com.store.crypto.model.user.BankAccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountDetailsRepository extends JpaRepository<BankAccountDetails, Integer> {
}
