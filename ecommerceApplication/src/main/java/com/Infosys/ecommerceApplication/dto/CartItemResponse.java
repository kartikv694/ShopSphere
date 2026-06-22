package com.Infosys.ecommerceApplication.dto;

import java.util.List;

import com.Infosys.ecommerceApplication.model.Cart;

/**
 * Flat, frontend-friendly shape for a cart row: product fields merged with
 * the quantity, instead of exposing the raw JPA Cart entity (which would
 * otherwise also serialize the full related User).
 */
public class CartItemResponse {

    private Long cartId;
    private Long productId;
    private String name;
    private String description;
    private double price;
    private String category;
    private List<String> imageUrls;
    private int quantity;

    public CartItemResponse() {
    }

    public static CartItemResponse fromEntity(Cart cart) {
        CartItemResponse response = new CartItemResponse();

        response.cartId = cart.getId();
        response.quantity = cart.getQuantity();

        if (cart.getProduct() != null) {
            response.productId = cart.getProduct().getId();
            response.name = cart.getProduct().getName();
            response.description = cart.getProduct().getDescription();
            response.price = cart.getProduct().getPrice();
            response.category = cart.getProduct().getCategory();
            response.imageUrls = cart.getProduct().getImageUrls();
        }

        return response;
    }

    public Long getCartId() {
        return cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public int getQuantity() {
        return quantity;
    }
}
