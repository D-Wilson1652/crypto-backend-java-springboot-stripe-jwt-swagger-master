package com.store.crypto.repository.realestate;

import com.store.crypto.model.realestate.RealEstateMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RealEstateMediaRepository extends JpaRepository<RealEstateMedia, Long> {

    Optional<RealEstateMedia> findFirstByFileNameEqualsIgnoreCase(String fileName);
}
