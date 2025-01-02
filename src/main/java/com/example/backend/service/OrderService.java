package com.example.backend.service;

import com.example.backend.model.Order;
import com.example.backend.model.User;
import com.example.backend.request.OrderRequest;

import java.util.List;

public interface OrderService {
    public Order createOrder(OrderRequest req, User user) throws Exception;

    public Order updateOrder(Long orderId, String orderStatus) throws Exception;
    public void cancelOrder(Long orderId) throws Exception;
    public List<Order> getUserOrders(Long userId) throws Exception;
    public List<Order> getRestaurantsOrder(Long restaurantId, String orderStatus) throws Exception;
    public Order findOrderById(Long orderId) throws Exception;
}
