package com.Infosys.ecommerceApplication.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Infosys.ecommerceApplication.dto.PaymentOrderRequest;
import com.Infosys.ecommerceApplication.dto.PaytmVerifyRequest;
import com.Infosys.ecommerceApplication.dto.RazorpayVerifyRequest;
import com.Infosys.ecommerceApplication.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/razorpay/order")
    public ResponseEntity<Map<String, Object>> createRazorpayOrder(
            @RequestBody PaymentOrderRequest request
    ) {
        return ResponseEntity.ok(
                paymentService.createRazorpayOrder(request)
        );
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<String> verifyRazorpayPayment(
            @RequestBody RazorpayVerifyRequest request
    ) {
        paymentService.verifyRazorpayPayment(request);
        return ResponseEntity.ok("Payment verified");
    }

    @PostMapping("/paytm/initiate")
    public ResponseEntity<Map<String, Object>> initiatePaytmPayment(
            @RequestBody PaymentOrderRequest request
    ) {
        return ResponseEntity.ok(
                paymentService.initiatePaytmPayment(request)
        );
    }

    @PostMapping("/paytm/verify")
    public ResponseEntity<String> verifyPaytmPayment(
            @RequestBody PaytmVerifyRequest request
    ) {
        paymentService.verifyPaytmPayment(request);
        return ResponseEntity.ok("Payment verified");
    }
}
