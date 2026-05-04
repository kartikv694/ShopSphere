package com.Infosys.ecommerceApplication.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.model.Product;
import com.Infosys.ecommerceApplication.repository.ProductRepository;
import com.cloudinary.Cloudinary;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Cloudinary cloudinary;

    // ✅ ADD
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // ✅ GET ALL
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ✅ GET BY ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // ✅ DELETE (NON-BLOCKING CLOUDINARY)
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 🗑 Delete product from DB FIRST (fast response)
        productRepository.delete(product);

        // 🔥 Delete images in background (non-blocking)
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            new Thread(() -> {
                for (String imageUrl : product.getImageUrls()) {
                    deleteImageFromCloudinary(imageUrl);
                }
            }).start();
        }
    }

    // ✅ SEARCH
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // ✅ CATEGORY
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // ✅ ADV SEARCH
    public List<Product> searchByNameAndCategory(String keyword, String category) {
        return productRepository
                .findByNameContainingIgnoreCaseAndCategory(keyword, category);
    }

    // ==============================
    // 🔥 CLOUDINARY HELPERS
    // ==============================

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return null;

        try {
            String[] parts = imageUrl.split("/");
            String fileName = parts[parts.length - 1];

            if (!fileName.contains(".")) return fileName;

            return fileName.substring(0, fileName.lastIndexOf("."));
        } catch (Exception e) {
            return null;
        }
    }

    private void deleteImageFromCloudinary(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);

            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, new HashMap<>());
            }

        } catch (Exception e) {
            System.out.println("Error deleting image: " + imageUrl);
            e.printStackTrace();
        }
    }
}