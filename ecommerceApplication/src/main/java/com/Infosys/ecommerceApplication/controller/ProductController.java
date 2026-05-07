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
    // ADD PRODUCT (MULTIPLE IMAGES)
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

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    Map uploadResult = cloudinary.uploader()
                            .upload(file.getBytes(), Map.of());

                    String url = uploadResult.get("secure_url").toString();
                    imageUrls.add(url);
                }
            }

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.setImageUrls(imageUrls);

            return ResponseEntity.ok(productService.addProduct(product));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // =========================
    // GET ALL PRODUCTS
    // =========================
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        }
        return ResponseEntity.status(404).body("Product not found");
    }

 // =========================
 // UPDATE PRODUCT (WITH IMAGES)
 // =========================
 @PutMapping(value = "/{id}", consumes = "multipart/form-data")
 public ResponseEntity<?> updateProduct(
         @PathVariable Long id,
         @RequestParam("name") String name,
         @RequestParam("description") String description,
         @RequestParam("price") double price,
         @RequestParam("category") String category,
         @RequestParam(value = "images", required = false) List<MultipartFile> files
 ) {
     try {
         Optional<Product> existing = productService.getProductById(id);

         if (existing.isEmpty()) {
             return ResponseEntity.status(404).body("Product not found");
         }

         Product product = existing.get();

         // ✅ update basic fields
         product.setName(name);
         product.setDescription(description);
         product.setPrice(price);
         product.setCategory(category);

         // ✅ handle images (ONLY if new images uploaded)
         if (files != null && !files.isEmpty()) {

             List<String> imageUrls = new ArrayList<>();

             for (MultipartFile file : files) {
                 if (!file.isEmpty()) {
                     Map uploadResult = cloudinary.uploader()
                             .upload(file.getBytes(), Map.of());

                     String url = uploadResult.get("secure_url").toString();
                     imageUrls.add(url);
                 }
             }

             // replace old images
             if (!imageUrls.isEmpty()) {
                 product.setImageUrls(imageUrls);
             }
         }

         return ResponseEntity.ok(productService.addProduct(product));

     } catch (Exception e) {
         return ResponseEntity.status(500).body("Error: " + e.getMessage());
     }
 }

    // =========================
    // 🔥 DELETE PRODUCT
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {

        Optional<Product> product = productService.getProductById(id);

        if (product.isPresent()) {

            productService.deleteProduct(id);

            return ResponseEntity.ok("Product deleted successfully");
        }

        return ResponseEntity.status(404).body("Product not found");
    }
    
 // =========================
 // SEARCH PRODUCTS
 // =========================
 @GetMapping("/search")
 public ResponseEntity<List<Product>> searchProducts(
         @RequestParam String keyword
 ) {
     return ResponseEntity.ok(
             productService.searchProducts(keyword)
     );
 }

 // =========================
 // GET PRODUCTS BY CATEGORY
 // =========================
 @GetMapping("/category/{category}")
 public ResponseEntity<List<Product>> getProductsByCategory(
         @PathVariable String category
 ) {

     // ALL PRODUCTS
     if(category.equalsIgnoreCase("all")) {

         return ResponseEntity.ok(
                 productService.getAllProducts()
         );
     }

     // CATEGORY PRODUCTS
     return ResponseEntity.ok(
             productService.getProductsByCategory(category)
     );
 }

 // =========================
 // SEARCH + CATEGORY FILTER
 // =========================
 @GetMapping("/filter")
 public ResponseEntity<List<Product>> filterProducts(
         @RequestParam String keyword,
         @RequestParam String category
 ) {
     return ResponseEntity.ok(
             productService.searchByNameAndCategory(keyword, category)
     );
 }
}