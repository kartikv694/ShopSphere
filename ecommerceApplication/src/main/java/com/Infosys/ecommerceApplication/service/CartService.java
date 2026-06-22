package com.Infosys.ecommerceApplication.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.model.Cart;
import com.Infosys.ecommerceApplication.model.Product;
import com.Infosys.ecommerceApplication.model.User;
import com.Infosys.ecommerceApplication.repository.CartRepository;
import com.Infosys.ecommerceApplication.repository.ProductRepository;
import com.Infosys.ecommerceApplication.repository.userRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private userRepository userRepository;

    // Resolves the authenticated principal's email into the actual User
    // entity, so every cart operation is scoped to the logged-in user only.
    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 1. Add to Cart
    public Cart addToCart(String email, Long productId, int quantity) {

        User user = resolveUser(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart existingCartItem = cartRepository.findByUserAndProduct_Id(user, productId);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            return cartRepository.save(existingCartItem);
        } else {
            Cart newCartItem = new Cart(user, product, quantity);
            return cartRepository.save(newCartItem);
        }
    }

    // 2. Get Cart for the current user
    public List<Cart> getCartForUser(String email) {
        User user = resolveUser(email);
        return cartRepository.findByUser(user);
    }

    // 3. Update Quantity
    public Cart updateQuantity(String email, Long productId, int quantity) {

        User user = resolveUser(email);

        Cart cartItem = cartRepository.findByUserAndProduct_Id(user, productId);

        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            return cartRepository.save(cartItem);
        } else {
            throw new RuntimeException("Product not found in cart");
        }
    }

    // 4. Remove Single Item
    public void removeItem(String email, Long productId) {

        User user = resolveUser(email);

        Cart cartItem = cartRepository.findByUserAndProduct_Id(user, productId);

        if (cartItem != null) {
            cartRepository.delete(cartItem);
        } else {
            throw new RuntimeException("Product not found in cart");
        }
    }

    // 5. Clear Full Cart
    public void clearCart(String email) {

        User user = resolveUser(email);

        List<Cart> cartItems = cartRepository.findByUser(user);
        cartRepository.deleteAll(cartItems);
    }
}
