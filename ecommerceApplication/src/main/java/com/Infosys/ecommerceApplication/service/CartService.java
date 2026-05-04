package com.Infosys.ecommerceApplication.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.model.Cart;
import com.Infosys.ecommerceApplication.repository.CartRepository;



@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    // ✅ 1. Add to Cart
    public Cart addToCart(Long userId, Long productId, int quantity) {

        Cart existingCartItem = cartRepository.findByUserIdAndProductId(userId, productId);

        if (existingCartItem != null) {
            // Product already exists → update quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            return cartRepository.save(existingCartItem);
        } else {
            // New product → create entry
            Cart newCartItem = new Cart(userId, productId, quantity);
            return cartRepository.save(newCartItem);
        }
    }

    // ✅ 2. Get Cart by User
    public List<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    // ✅ 3. Update Quantity
    public Cart updateQuantity(Long userId, Long productId, int quantity) {

        Cart cartItem = cartRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            return cartRepository.save(cartItem);
        } else {
            throw new RuntimeException("Product not found in cart");
        }
    }

    // ✅ 4. Remove Single Item
    public void removeItem(Long userId, Long productId) {

        Cart cartItem = cartRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null) {
            cartRepository.delete(cartItem);
        } else {
            throw new RuntimeException("Product not found in cart");
        }
    }

    // ✅ 5. Clear Full Cart
    public void clearCart(Long userId) {

        List<Cart> cartItems = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(cartItems);
    }
}