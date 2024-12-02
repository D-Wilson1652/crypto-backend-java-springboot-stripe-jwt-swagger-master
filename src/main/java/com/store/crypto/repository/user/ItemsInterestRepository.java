package com.store.crypto.repository.user;

import com.store.crypto.model.realestate.RealEstateMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsInterestRepository extends JpaRepository<RealEstateMedia, Integer> {
}
