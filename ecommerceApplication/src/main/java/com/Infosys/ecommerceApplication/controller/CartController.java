package com.Infosys.ecommerceApplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Infosys.ecommerceApplication.model.Cart;
import com.Infosys.ecommerceApplication.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ✅ 1. ADD TO CART
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        Cart cart = cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    // ✅ 2. GET CART
    @GetMapping("/{userId}")
    public ResponseEntity<List<Cart>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    // ✅ 3. UPDATE QUANTITY
    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        Cart updated = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(updated);
    }

    // ✅ 4. REMOVE ITEM
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItem(
            @RequestParam Long userId,
            @RequestParam Long productId) {

        cartService.removeItem(userId, productId);
        return ResponseEntity.ok("Item removed");
    }

    // ✅ 5. CLEAR CART
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {

        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared");
    }
}
