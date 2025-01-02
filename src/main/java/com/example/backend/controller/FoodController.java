package com.example.backend.controller;

import com.example.backend.model.Food;
import com.example.backend.model.User;
import com.example.backend.service.FoodService;
import com.example.backend.service.RestaurantService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    private FoodService foodService;
    private UserService userService;
    private RestaurantService restaurantService;

    @Autowired
    public FoodController(FoodService foodService, RestaurantService restaurantService, UserService userService) {
        this.foodService = foodService;
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity< List<Food>> createFood(@RequestParam String name, @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        List<Food> foods = foodService.searchFood(name);

        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity< List<Food>> getRestaurantFoods(@PathVariable Long restaurantId,
                                                          @RequestParam boolean vegetarian,
                                                          @RequestParam boolean seasonal,
                                                          @RequestParam boolean nonvegetarian,
                                                          @RequestParam(required = false) String food_category,
                                                          @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        List<Food> foods = foodService.getRestaurantsFood(restaurantId, vegetarian, nonvegetarian, seasonal, food_category);

        return new ResponseEntity<>(foods, HttpStatus.OK);
    }


}
