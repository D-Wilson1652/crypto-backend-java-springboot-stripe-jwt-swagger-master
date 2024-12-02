package com.store.crypto.service.category.impl;


import com.store.crypto.dto.category.CategoryItemDTO;
import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.model.cars.Car;
import com.store.crypto.model.category.Category;
import com.store.crypto.model.realestate.RealEstate;
import com.store.crypto.model.user.User;
import com.store.crypto.repository.cars.CarRepository;
import com.store.crypto.repository.category.CategoryRepository;
import com.store.crypto.repository.realestate.RealEstateRepository;
import com.store.crypto.service.category.CategoryService;
import com.store.crypto.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final RealEstateRepository realEstateRepository;
    private final CarRepository carRepository;
    private final SessionUtils sessionUtils;

    @Override
    public ResponseEntity<Object> getCategories() {
        GenericResponse response = new GenericResponse();
        try {
            List<Category> categoryList = categoryRepository.findAll();
            if (categoryList.isEmpty()) {
                response.setData(new ArrayList<>());
                response.setMessage("No category found in the database.");
            } else {
                response.setMessage("List of categories");
                response.setData(categoryList);
            }
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> createCategory(Category category) {
        GenericResponse genericResponse = new GenericResponse();
        try {
            if (categoryRepository.findByName(category.getName()).isPresent()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Category already exists");
                genericResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
            }
            Category categorySaved = categoryRepository.save(category);
            genericResponse.setData(categorySaved);
            genericResponse.setMessage("Category created successfully.");
            genericResponse.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            genericResponse.setData(null);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> updateCategory(Category updatedCategory) {
        GenericResponse genericResponse = new GenericResponse();
        try {
            Optional<Category> existingCategory = categoryRepository.findById(updatedCategory.getId());
            if (existingCategory.isEmpty()) {
                genericResponse.setData(null);
                genericResponse.setMessage("Category doesn't exist that you want to update.");
                genericResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
            }

            Optional<Category> categoryWithSameName = categoryRepository.findByName(updatedCategory.getName());
            if (categoryWithSameName.isPresent() && !existingCategory.get().getId().equals(categoryWithSameName.get().getId())) {
                genericResponse.setData(null);
                genericResponse.setMessage("Category with this name already exists.");
                genericResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
            } else {
                existingCategory.get().setName(updatedCategory.getName());
                categoryRepository.save(existingCategory.get());
                genericResponse.setData(existingCategory.get());
                genericResponse.setMessage("Category updated successfully");
                genericResponse.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(genericResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            genericResponse.setData(null);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> deleteCategory(Integer categoryId) {
        GenericResponse response = new GenericResponse();
        response.setData(null);
        try {
            Optional<Category> category = categoryRepository.findById(categoryId);
            if (category.isEmpty()) {
                response.setMessage("Category doesn't exist that you want to delete.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                categoryRepository.delete(category.get());
                response.setMessage("Category deleted successfully");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getCategoriesByName(String name, int pageNumber, int pageSize) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<Category> category = categoryRepository.findByName(name);
            if (category.isEmpty()) {
                response.setMessage("No category found in the database against this name.");
                response.setData(carRepository.findAll());
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (!sessionUtils.isLoggedIn()) {
                response.setData(null);
                response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                response.setMessage("You need to be logged in to access this resource");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                User user = sessionUtils.getLoggedInUser();
                if (user == null) {
                    response.setData(null);
                    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                    response.setMessage("You need to be logged in to access this resource");
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                } else {
                    switch (name) {
                        case "Cars":
                            Page<Car> cars = carRepository.findByCategoryAndUser(category.get(), user, pageable);
                            response.setMessage("List of cars");
                            response.setData(cars);
                            response.setStatusCode(HttpStatus.OK.value());
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        case "Real Estate":
                            Page<RealEstate> realEstates = realEstateRepository.findByCategoryAndUser(category.get(), user, pageable);
                            response.setMessage("List of Real Estates");
                            response.setData(realEstates);
                            response.setStatusCode(HttpStatus.OK.value());
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        case "all":
                            // Fetch Cars and Real Estates and merge results
                            Page<CategoryItemDTO> combinedItems = getPaginatedItemsFromAllSources(category.get(), user, pageNumber, pageSize);
                            response.setMessage("List of all items (cars and real estate)");
                            response.setData(combinedItems);
                            response.setStatusCode(HttpStatus.OK.value());
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        default:
                            response.setMessage("Invalid category name");
                            response.setData(null);
                            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }

            }
        } catch (Exception e) {
            response.setData(null);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public Page<CategoryItemDTO> getPaginatedItemsFromAllSources(Category category, User user, int pageNumber, int pageSize) {
        // Fetch all Cars and Real Estates for the user and category
        List<Car> cars = carRepository.findByCategoryAndUser(category, user);
        List<RealEstate> realEstates = realEstateRepository.findByCategoryAndUser(category, user);

        // Convert Cars and Real Estate to unified DTO list
        List<CategoryItemDTO> allItems = new ArrayList<>();

        // Map Cars to CategoryItemDTO
        cars.forEach(car -> {
            CategoryItemDTO dto = new CategoryItemDTO(
                    car.getId(),
                    car.getTitle(),        // Assuming Car entity has a 'title' field
                    car.getDescription(),  // Assuming Car entity has a 'description' field
                    car.getListingDate(),  // Assuming Car entity has a 'createdAt' field
                    car.getPrice(),        // Assuming Car entity has a 'price' field
                    "Car"                  // Type identifier
            );
            allItems.add(dto);
        });

        // Map Real Estates to CategoryItemDTO
        realEstates.forEach(realEstate -> {
            CategoryItemDTO dto = new CategoryItemDTO(
                    realEstate.getId(),
                    realEstate.getTitle(),        // Assuming RealEstate entity has a 'title' field
                    realEstate.getDescription(),  // Assuming RealEstate entity has a 'description' field
                    realEstate.getListingDate(),  // Assuming RealEstate entity has a 'createdAt' field
                    realEstate.getPrice().toString(),        // Assuming RealEstate entity has a 'price' field
                    "RealEstate"                  // Type identifier
            );
            allItems.add(dto);
        });

        // Apply manual pagination
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int total = allItems.size(); // total number of items
        int fromIndex = Math.min((int) pageable.getOffset(), total);
        int toIndex = Math.min((fromIndex + pageable.getPageSize()), total);

        List<CategoryItemDTO> paginatedItems = allItems.subList(fromIndex, toIndex);

        // Wrap in a PageImpl object for pagination metadata
        return new PageImpl<>(paginatedItems, pageable, total);
    }

}
