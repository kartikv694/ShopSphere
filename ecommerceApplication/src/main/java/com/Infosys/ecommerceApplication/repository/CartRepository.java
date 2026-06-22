package com.Infosys.ecommerceApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Infosys.ecommerceApplication.model.Cart;
import com.Infosys.ecommerceApplication.model.User;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser(User user);

    Cart findByUserAndProduct_Id(User user, Long productId);
}
