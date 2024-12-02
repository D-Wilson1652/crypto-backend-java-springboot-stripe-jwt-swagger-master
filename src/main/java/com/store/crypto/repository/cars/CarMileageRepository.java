package com.store.crypto.repository.cars;

import com.store.crypto.model.cars.CarMileage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarMileageRepository extends JpaRepository<CarMileage, Long> {
}
