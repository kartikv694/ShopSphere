package com.Infosys.ecommerceApplication.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Infosys.ecommerceApplication.dto.CheckoutRequestDto;
import com.Infosys.ecommerceApplication.model.Order;
import com.Infosys.ecommerceApplication.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequestDto request,
            Principal principal
    ) {

        String email = principal.getName();

        Order order =
                orderService.checkout(request, email);

        return ResponseEntity.ok(order);
    }

}