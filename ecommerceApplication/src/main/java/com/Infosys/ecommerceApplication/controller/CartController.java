package com.Infosys.ecommerceApplication.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Infosys.ecommerceApplication.dto.CartItemResponse;
import com.Infosys.ecommerceApplication.model.Cart;
import com.Infosys.ecommerceApplication.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // The cart owner is always taken from the authenticated JWT principal,
    // never from a client-supplied userId, so one logged-in user can never
    // read or modify another user's cart.

    // 1. ADD TO CART
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            Principal principal) {

        Cart cart = cartService.addToCart(principal.getName(), productId, quantity);
        return ResponseEntity.ok(CartItemResponse.fromEntity(cart));
    }

    // 2. GET CART (for the logged-in user)
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(Principal principal) {
        List<CartItemResponse> items = cartService.getCartForUser(principal.getName())
                .stream()
                .map(CartItemResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(items);
    }

    // 3. UPDATE QUANTITY
    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(
            @RequestParam Long productId,
            @RequestParam int quantity,
            Principal principal) {

        Cart updated = cartService.updateQuantity(principal.getName(), productId, quantity);
        return ResponseEntity.ok(CartItemResponse.fromEntity(updated));
    }

    // 4. REMOVE ITEM
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItem(
            @RequestParam Long productId,
            Principal principal) {

        cartService.removeItem(principal.getName(), productId);
        return ResponseEntity.ok("Item removed");
    }

    // 5. CLEAR CART
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Principal principal) {

        cartService.clearCart(principal.getName());
        return ResponseEntity.ok("Cart cleared");
    }
}
