package com.store.crypto.repository.realestate;


import com.store.crypto.model.realestate.RealEstateSpecifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificationsRepository extends JpaRepository<RealEstateSpecifications, Long> {
}
