package com.Infosys.ecommerceApplication.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Infosys.ecommerceApplication.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserId(Long userId);

    Cart findByUserIdAndProductId(Long userId, Long productId);
}