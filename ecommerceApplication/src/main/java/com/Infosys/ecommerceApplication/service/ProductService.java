package com.Infosys.ecommerceApplication.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.model.Product;
import com.Infosys.ecommerceApplication.repository.ProductRepository;
import com.cloudinary.Cloudinary;

import jakarta.transaction.Transactional;

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
    @Transactional
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ Access images BEFORE delete (while session is open)
        List<String> images = product.getImageUrls();

        // delete DB
        productRepository.delete(product);

        // delete cloudinary
        if (images != null && !images.isEmpty()) {
            for (String imageUrl : images) {
                deleteImageFromCloudinary(imageUrl);
            }
        }
    }

    // ✅ SEARCH
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // ✅ CATEGORY
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    // ✅ ADV SEARCH
    public List<Product> searchByNameAndCategory(String keyword, String category) {
        return productRepository
                .findByNameContainingIgnoreCaseAndCategoryIgnoreCase(
                        keyword,
                        category
                );
    }

    // ==============================
    // 🔥 CLOUDINARY HELPERS
    // ==============================

    private String extractPublicId(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/upload/");
            String path = parts[1]; // v1778014755/cctpb3rauji1r694nauf.jpg

            // remove version
            path = path.substring(path.indexOf("/") + 1);

            // remove extension
            return path.substring(0, path.lastIndexOf("."));

        } catch (Exception e) {
            return null;
        }
    }

    private void deleteImageFromCloudinary(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);

            System.out.println("Deleting URL: " + imageUrl);
            System.out.println("Extracted public_id: " + publicId);

            if (publicId != null) {
                Map result = cloudinary.uploader().destroy(publicId, new HashMap<>());
                System.out.println("Cloudinary result: " + result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}