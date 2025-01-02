package com.example.backend.service;

import com.example.backend.model.Cart;
import com.example.backend.model.CartItem;
import com.example.backend.model.Food;
import com.example.backend.model.User;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.CartRepository;
import com.example.backend.request.AddCartItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private UserService userService;
    private FoodService foodService;

    @Autowired
    public CartServiceImpl(CartItemRepository cartItemRepository, CartRepository cartRepository, FoodService foodRepository, UserService userService) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.foodService = foodRepository;
        this.userService = userService;
    }


    @Override
    public CartItem additemToCart(AddCartItemRequest req, String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        Food food = foodService.findFoodById(req.getFoodId());

        Cart cart = cartRepository.findByCustomerId(user.getId());

        for(CartItem cartItem : cart.getItems()){
            if(cartItem.getFood().equals(food)){
                int newQuantity = cartItem.getQuantity() + req.getQuantity();
                return updateCartItemQuantity(cartItem.getId(), newQuantity);
            }
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setFood(food);
        newCartItem.setQuantity(req.getQuantity());
        newCartItem.setCart(cart);
        newCartItem.setIngredients(req.getIngredients());
        newCartItem.setTotalPrice(req.getQuantity()*food.getPrice());

        CartItem savedCartItem = cartItemRepository.save(newCartItem);
        cart.getItems().add(savedCartItem);

        return savedCartItem;
    }

    @Override
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) throws Exception {
        Optional<CartItem> opt = cartItemRepository.findById(cartItemId);

        if(opt.isEmpty()){
            throw new Exception("Cart item not found");
        }
        CartItem item = opt.get();
        item.setQuantity(quantity);

        item.setTotalPrice(quantity*item.getFood().getPrice());

        return cartItemRepository.save(item);
    }

    @Override
    public Cart removeItemFromCart(Long cartItemId, String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        Cart cart = cartRepository.findByCustomerId(user.getId());

        Optional<CartItem> opt = cartItemRepository.findById(cartItemId);

        if(opt.isEmpty()){
            throw new Exception("Cart item not found");
        }
        CartItem item = opt.get();
        cart.getItems().remove(item);

        return cartRepository.save(cart);
    }

    @Override
    public Long calculateCartTotals(Cart cart) throws Exception {
        Long total = 0L;
        for(CartItem cartItem : cart.getItems()){
            total += cartItem.getFood().getPrice()*cartItem.getQuantity();
        }
        return total;
    }

    @Override
    public Cart findCartById(Long id) throws Exception {
        Optional<Cart> opt = cartRepository.findById(id);

        if(opt.isEmpty()){
            throw new Exception("Cart not found");
        }
        return opt.get();
    }

    @Override
    public Cart findCartByUserId(Long userId) throws Exception {
//        User user = userService.findUserByJwtToken(jwt);
        Cart cart =  cartRepository.findByCustomerId(userId);
        cart.setTotal(calculateCartTotals(cart));
        return cart;
    }

    @Override
    public Cart clearCart(Long userId) throws Exception {
//        User user = userService.findUserByJwtToken(jwt);
        Cart cart = findCartByUserId(userId);
        cart.getItems().clear();
        return cartRepository.save(cart);
    }
}
