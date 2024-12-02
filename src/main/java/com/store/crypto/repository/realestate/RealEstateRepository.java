package com.store.crypto.repository.realestate;


import com.store.crypto.model.category.Category;
import com.store.crypto.model.realestate.RealEstate;
import com.store.crypto.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {

    Page<RealEstate> findByCategoryAndUser(Category category, User user, Pageable pageable);
    List<RealEstate> findByCategoryAndUser(Category category, User user);

    List<RealEstate> findAllByCountryEqualsIgnoreCase(String country);

    List<RealEstate> findAllByCityEqualsIgnoreCase(String city);

    List<RealEstate> findAllByRegionEqualsIgnoreCase(String region);

    List<RealEstate> findAllByCountryEqualsIgnoreCaseAndCityEqualsIgnoreCase(String country, String city);


    List<RealEstate> findAllByCountryEqualsIgnoreCaseOrCityEqualsIgnoreCaseOrRegionEqualsIgnoreCase(String country, String city, String region);

    List<RealEstate> findAllByCountryEqualsIgnoreCaseAndCityEqualsIgnoreCaseAndRegionEqualsIgnoreCase(String country, String city, String region);

}

