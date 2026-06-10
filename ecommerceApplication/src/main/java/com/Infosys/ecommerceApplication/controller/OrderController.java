package com.Infosys.ecommerceApplication.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Infosys.ecommerceApplication.dto.CheckoutRequestDto;
import com.Infosys.ecommerceApplication.dto.OrderStatusUpdateRequest;
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
                orderService.checkout(
                        request,
                        email
                );

        return ResponseEntity.ok(order);

    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>>
    getCustomerOrders(
            Principal principal
    ) {

        String email =
                principal.getName();

        return ResponseEntity.ok(
                orderService
                        .getCustomerOrders(email)
        );

    }

    @GetMapping({"/all", ""})
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        orderId,
                        request.getStatus()
                )
        );
    }

}
