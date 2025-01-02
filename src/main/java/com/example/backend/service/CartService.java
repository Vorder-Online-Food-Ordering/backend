package com.example.backend.service;

import com.example.backend.model.Cart;
import com.example.backend.model.CartItem;
import com.example.backend.request.AddCartItemRequest;

public interface CartService {
    public CartItem additemToCart(AddCartItemRequest req, String jwt) throws Exception;

    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) throws Exception;

    public Cart removeItemFromCart(Long cartItemId, String jwt) throws Exception;

    public Long calculateCartTotals(Cart cart) throws Exception;
    public Cart findCartById(Long id) throws Exception;
    public Cart findCartByUserId(Long userId) throws Exception;
    public Cart clearCart(Long userId) throws Exception;
}
