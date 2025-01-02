package com.example.backend.service;

import com.example.backend.model.Category;
import com.example.backend.model.Restaurant;
import com.example.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    private CategoryRepository categoryRepository;
    private RestaurantService restaurantService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, RestaurantService restaurantService) {
        this.categoryRepository = categoryRepository;
        this.restaurantService = restaurantService;
    }

    @Override
    public Category createCategory(String name, Long userId) throws Exception {
        Restaurant restaurant = restaurantService.getRestaurantByUserId(userId);
        Category category = new Category();
        category.setName(name);
        category.setRestaurant(restaurant);

        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findCategoryByRestaurantId(Long id) throws Exception {
        Restaurant restaurant = restaurantService.getRestaurantByUserId(id);
        return categoryRepository.findByRestaurantId(restaurant.getId());
    }

    @Override
    public Category findCategoryById(Long id) throws Exception {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()){
            throw new Exception("Category not found");
        }
        return category.get();
    }
}
