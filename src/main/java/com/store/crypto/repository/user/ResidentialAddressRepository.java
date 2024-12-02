package com.store.crypto.repository.user;

import com.store.crypto.model.user.ResidentialAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentialAddressRepository extends JpaRepository<ResidentialAddress, Integer> {
}
