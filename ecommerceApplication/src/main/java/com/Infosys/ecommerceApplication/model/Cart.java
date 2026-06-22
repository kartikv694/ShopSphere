package com.Infosys.ecommerceApplication.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "cart",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_cart_user_product",
        columnNames = {"user_id", "product_id"}
    )
)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Proper relational FK to the owning user, instead of a raw userId
    // column. This is what makes the cart <-> user <-> product relationship
    // explicit at the database level (with a real foreign key constraint)
    // rather than just an unenforced numeric column.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    public Cart() {}

    public Cart(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
