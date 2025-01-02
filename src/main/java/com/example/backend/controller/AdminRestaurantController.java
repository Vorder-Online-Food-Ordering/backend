package com.example.backend.controller;

import com.example.backend.model.Restaurant;
import com.example.backend.model.User;
import com.example.backend.request.CreateRestaurantRequest;
import com.example.backend.response.MessageResponse;
import com.example.backend.service.RestaurantService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {
    private RestaurantService restaurantService;
    private UserService userService;

    @Autowired
    public AdminRestaurantController(RestaurantService restaurantService, UserService userService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(
            @RequestBody CreateRestaurantRequest req, @RequestHeader("Authorization") String jwt
            ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        Restaurant restaurant = restaurantService.createRestaurant(req, user);
        return new ResponseEntity<>(restaurant, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @RequestBody CreateRestaurantRequest req, @RequestHeader("Authorization") String jwt, @PathVariable Long id
    ) throws Exception {

        Restaurant restaurant = restaurantService.updateRestaurant(id, req);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRestaurant(
              @RequestHeader("Authorization") String jwt, @PathVariable Long id
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        restaurantService.deleteRestaurant(id);
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Restaurant deleted successfully");
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Restaurant> updateRestaurantStatus(
           @RequestHeader("Authorization") String jwt, @PathVariable Long id
    ) throws Exception {
        Restaurant restaurant = restaurantService.updateRestaurantStatus(id);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<Restaurant> findRestaurantByUserId(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        Restaurant restaurant = restaurantService.getRestaurantByUserId(user.getId());
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }
    
}
