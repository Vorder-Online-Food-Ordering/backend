package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import com.example.backend.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private AddressRepository addressRepository;
    private UserRepository userRepository;
    private RestaurantService restaurantService;
    private CartService cartService;

    @Autowired
    public OrderServiceImpl(AddressRepository addressRepository, CartService cartService, OrderItemRepository orderItemRepository, OrderRepository orderRepository, RestaurantService restaurantService, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.cartService = cartService;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.restaurantService = restaurantService;
        this.userRepository = userRepository;
    }

    @Override
    public Order createOrder(OrderRequest req, User user) throws Exception {
        Address shipAddress = req.getDeliveryAddress();
        Address savedAddress = addressRepository.save(shipAddress);

        if(!user.getAddresses().contains(savedAddress)) {
            user.getAddresses().add(savedAddress);
            userRepository.save(user);
        }

        Restaurant restaurant = restaurantService.findRestaurantById(req.getRestaurantId());

        Order createdOrder = new Order();

        createdOrder.setRestaurant(restaurant);
        createdOrder.setCustomer(user);
        createdOrder.setCreatedAt(new Date());
        createdOrder.setDeleveryAddress(savedAddress);
        createdOrder.setOrderStatus("PENDING");

        Cart cart = cartService.findCartByUserId(user.getId());

        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem cartItem : cart.getItems()){
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());
            orderItem.setIngredients(cartItem.getIngredients());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            orderItems.add(savedOrderItem);
        }

        Long totalPrice = cartService.calculateCartTotals(cart);

        createdOrder.setItems(orderItems);
        createdOrder.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(createdOrder);
        restaurant.getOrders().add(savedOrder);


        return createdOrder;
    }

    @Override
    public Order updateOrder(Long orderId, String orderStatus) throws Exception {
        Order order = findOrderById(orderId);
        if(orderStatus.equals("OUT_FOR_DELIVERY") || orderStatus.equals("DELIVERED") || orderStatus.equals("COMPLETED")
        || orderStatus.equals("PENDING")){
            order.setOrderStatus(orderStatus);
            return orderRepository.save(order);
        }

        throw new Exception("Order status is not cvalid");
    }

    @Override
    public void cancelOrder(Long orderId) throws Exception {
        Order order = findOrderById(orderId);
        orderRepository.deleteById(orderId);

    }

    @Override
    public List<Order> getUserOrders(Long userId) throws Exception {
        return orderRepository.findByCustomerId(userId);
    }

    @Override
    public List<Order> getRestaurantsOrder(Long restaurantId, String orderStatus) throws Exception {

        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);

        if(orderStatus != null){
            orders = orders.stream().filter(order -> order.getOrderStatus().equals(orderStatus)).collect(Collectors.toList());
        }
        return orders;
    }

    @Override
    public Order findOrderById(Long orderId) throws Exception {
        Optional<Order> opt = orderRepository.findById(orderId);

        if(opt.isEmpty()){
            throw new Exception("Order not found");
        }
        return opt.get();
    }
}
