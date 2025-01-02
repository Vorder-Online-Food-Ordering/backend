package com.example.backend.service;

import com.example.backend.model.Category;
import com.example.backend.model.Food;
import com.example.backend.model.Restaurant;
import com.example.backend.request.CreateFoodRequest;

import java.util.List;

public interface FoodService {
    public Food createFood(CreateFoodRequest req, Category category, Restaurant restaurant);
    void deleteFood(Long foodId) throws Exception;
    public List<Food> getRestaurantsFood(Long restaurantId, boolean isVegitarian, boolean isNonvegarian, boolean isSeasonal, String foodCategory);
    public List<Food> searchFood(String keyword);
    public Food findFoodById(Long foodId) throws Exception;
    public Food updateAvailabilityStatus(Long foodId) throws Exception;
}
