package com.example.backend.controller;

import com.example.backend.model.Food;
import com.example.backend.model.Restaurant;
import com.example.backend.model.User;
import com.example.backend.request.CreateFoodRequest;
import com.example.backend.response.MessageResponse;
import com.example.backend.service.FoodService;
import com.example.backend.service.RestaurantService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/food")
public class AdminFoodController {

    private FoodService foodService;
    private UserService userService;
    private RestaurantService restaurantService;

    @Autowired
    public AdminFoodController(FoodService foodService, RestaurantService restaurantService, UserService userService) {
        this.foodService = foodService;
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody CreateFoodRequest req, @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
//        Restaurant restaurant = restaurantService.findRestaurantById(req.getRestaurantId());
        Restaurant restaurant = restaurantService.getRestaurantByUserId(user.getId());
        Food food = foodService.createFood(req, req.getCategory(), restaurant);

        return new ResponseEntity<>(food, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteFood(@PathVariable Long id, @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        foodService.deleteFood(id);

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Food deleted successfully");

        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFoodAvailabilityStatus(@PathVariable Long id, @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        Food food = foodService.updateAvailabilityStatus(id);


        return new ResponseEntity<>(food, HttpStatus.OK);
    }
}
