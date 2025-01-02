package com.example.backend.service;

import com.example.backend.dto.RestaurantDto;
import com.example.backend.model.Restaurant;
import com.example.backend.model.User;
import com.example.backend.request.CreateRestaurantRequest;

import java.util.List;

public interface RestaurantService {
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user);
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) throws Exception;
    public void deleteRestaurant(Long restaurantId) throws Exception;
    public List<Restaurant> findAllRestaurants();
    public List<Restaurant> searchRestaurant(String keyword);
    public Restaurant findRestaurantById(Long restaurantId) throws Exception;
    public Restaurant getRestaurantByUserId(Long userId) throws Exception;
    public RestaurantDto addToFavourites(Long restaurantId, User user) throws Exception;
    public Restaurant updateRestaurantStatus(Long id) throws Exception;
}
