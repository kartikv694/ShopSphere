package com.Infosys.ecommerceApplication.model;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String category;

    //  CORRECT MULTIPLE IMAGE MAPPING
    @ElementCollection
    @CollectionTable(
        name = "product_image_urls",
        joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image_urls")
    private List<String> imageUrls;

    // Constructors
    public Product() {}

    public Product(String name, String description, double price, List<String> imageUrls, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
        this.category = category;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}