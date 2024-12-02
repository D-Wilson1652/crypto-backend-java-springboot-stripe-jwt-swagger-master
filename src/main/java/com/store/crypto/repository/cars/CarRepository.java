package com.store.crypto.repository.cars;

import com.store.crypto.model.cars.Car;
import com.store.crypto.model.category.Category;
import com.store.crypto.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Page<Car> findByCategoryAndUser(Category category, User user, Pageable pageable);
    List<Car> findByCategoryAndUser(Category category, User user);

}
