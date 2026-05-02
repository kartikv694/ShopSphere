package com.Infosys.ecommerceApplication.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.Infosys.ecommerceApplication.model.Product;
import com.Infosys.ecommerceApplication.service.ProductService;
import com.cloudinary.Cloudinary;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private Cloudinary cloudinary;

    // =========================
    // 🔥 ADD PRODUCT (MULTIPLE IMAGES)
    // =========================
    @PostMapping("/add")
    public ResponseEntity<?> addProductWithImages(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("category") String category,
            @RequestParam("images") List<MultipartFile> files
    ) {
        try {

            List<String> imageUrls = new ArrayList<>();

            // 🔥 Upload all images
            for (MultipartFile file : files) {

                if (!file.isEmpty()) {
                    Map uploadResult = cloudinary.uploader()
                            .upload(file.getBytes(), Map.of());

                    String url = uploadResult.get("secure_url").toString();

                    imageUrls.add(url);
                }
            }

            // 🔥 Save product
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.setImageUrls(imageUrls);

            Product savedProduct = productService.addProduct(product);

            return ResponseEntity.ok(savedProduct);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // =========================
    // OLD JSON API (KEEP IT)
    // =========================
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.addProduct(product));
    }

    // =========================
    // GET ALL PRODUCTS
    // =========================
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);

        return product
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Product not found"));
    }

    // =========================
    // SEARCH
    // =========================
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    // =========================
    // CATEGORY FILTER
    // =========================
    @GetMapping("/category")
    public ResponseEntity<List<Product>> getByCategory(@RequestParam String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    // =========================
    // ADVANCED SEARCH
    // =========================
    @GetMapping("/search-advanced")
    public ResponseEntity<List<Product>> searchByNameAndCategory(
            @RequestParam String keyword,
            @RequestParam String category) {

        return ResponseEntity.ok(
                productService.searchByNameAndCategory(keyword, category)
        );
    }
}