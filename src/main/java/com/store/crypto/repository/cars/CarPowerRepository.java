package com.store.crypto.repository.cars;

import com.store.crypto.model.cars.CarPower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarPowerRepository extends JpaRepository<CarPower, Long> {
}
