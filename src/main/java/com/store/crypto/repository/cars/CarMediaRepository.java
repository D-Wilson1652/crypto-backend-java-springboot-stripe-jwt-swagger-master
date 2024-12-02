package com.store.crypto.repository.cars;

import com.store.crypto.model.cars.CarMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarMediaRepository extends JpaRepository<CarMedia, Long> {

    Optional<CarMedia> findFirstByFileNameEqualsIgnoreCase(String fileName);
}
