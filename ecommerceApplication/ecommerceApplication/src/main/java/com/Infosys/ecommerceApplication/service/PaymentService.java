package com.Infosys.ecommerceApplication.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.config.EnvConfig;
import com.Infosys.ecommerceApplication.dto.PaymentOrderRequest;
import com.Infosys.ecommerceApplication.dto.PaytmVerifyRequest;
import com.Infosys.ecommerceApplication.dto.RazorpayVerifyRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.pg.merchant.PaytmChecksum;

@Service
public class PaymentService {

    @Autowired
    private EnvConfig envConfig;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> createRazorpayOrder(
            PaymentOrderRequest request
    ) {
        try {
            String keyId = requireEnv("RAZORPAY_KEY_ID");
            String keySecret = requireEnv("RAZORPAY_KEY_SECRET");

            long amountInPaise =
                    Math.round(request.getAmount() * 100);

            Map<String, Object> payload =
                    new HashMap<>();

            payload.put("amount", amountInPaise);
            payload.put(
                    "currency",
                    request.getCurrency() == null ||
                            request.getCurrency().isBlank()
                            ? "INR"
                            : request.getCurrency()
            );
            payload.put(
                    "receipt",
                    "shopsphere_" + Instant.now().toEpochMilli()
            );

            HttpRequest httpRequest =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            "https://api.razorpay.com/v1/orders"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Authorization",
                                    "Basic " +
                                            Base64
                                                    .getEncoder()
                                                    .encodeToString(
                                                            (keyId + ":" + keySecret)
                                                                    .getBytes(
                                                                            StandardCharsets.UTF_8
                                                                    )
                                                    )
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            objectMapper.writeValueAsString(
                                                    payload
                                            )
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (
                    response.statusCode() < 200 ||
                    response.statusCode() >= 300
            ) {
                throw new RuntimeException(
                        "Unable to create Razorpay order"
                );
            }

            Map<String, Object> responseMap =
                    objectMapper.readValue(
                            response.body(),
                            new TypeReference<>() {}
                    );

            responseMap.put("key", keyId);
            responseMap.put("keyId", keyId);
            responseMap.put("orderId", responseMap.get("id"));

            return responseMap;
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Unable to create Razorpay order",
                    exception
            );
        }
    }

    public void verifyRazorpayPayment(
            RazorpayVerifyRequest request
    ) {
        String keySecret =
                requireEnv("RAZORPAY_KEY_SECRET");

        String payload =
                request.getRazorpay_order_id() +
                        "|" +
                        request.getRazorpay_payment_id();

        String generatedSignature =
                hmacSha256(payload, keySecret);

        if (
                request.getRazorpay_signature() == null ||
                !generatedSignature.equals(
                        request.getRazorpay_signature()
                )
        ) {
            throw new RuntimeException(
                    "Invalid Razorpay payment signature"
            );
        }
    }

    public Map<String, Object> initiatePaytmPayment(
            PaymentOrderRequest request
    ) {
        try {
            String mid = requireEnv("PAYTM_MID");
            String merchantKey = requireEnv("PAYTM_MERCHANT_KEY");
            String website = defaultValue(
                    envConfig.get("PAYTM_WEBSITE"),
                    "WEBSTAGING"
            );
            String channelId = defaultValue(
                    envConfig.get("PAYTM_CHANNEL_ID"),
                    "WEB"
            );
            String environment = defaultValue(
                    envConfig.get("PAYTM_ENVIRONMENT"),
                    "STAGING"
            );

            String orderId =
                    "ORDER_" + Instant.now().toEpochMilli();

            Map<String, Object> body =
                    new HashMap<>();

            Map<String, Object> txnAmount =
                    new HashMap<>();
            txnAmount.put(
                    "value",
                    String.format("%.2f", request.getAmount())
            );
            txnAmount.put("currency", "INR");

            Map<String, Object> userInfo =
                    new HashMap<>();
            userInfo.put(
                    "custId",
                    request.getCustomer() != null &&
                            request.getCustomer().get("email") != null &&
                            !request.getCustomer().get("email").isBlank()
                            ? request.getCustomer().get("email")
                            : "CUSTOMER"
            );

            body.put("requestType", "Payment");
            body.put("mid", mid);
            body.put("websiteName", website);
            body.put("orderId", orderId);
            body.put("txnAmount", txnAmount);
            body.put("userInfo", userInfo);
            body.put(
                    "callbackUrl",
                    getPaytmBaseUrl(environment) +
                            "/theia/paytmCallback?ORDER_ID=" +
                            orderId
            );

            String bodyJson =
                    objectMapper.writeValueAsString(body);

            String signature =
                    PaytmChecksum.generateSignature(
                            bodyJson,
                            merchantKey
                    );

            Map<String, Object> head =
                    new HashMap<>();
            head.put("signature", signature);
            head.put("channelId", channelId);

            Map<String, Object> payload =
                    new HashMap<>();
            payload.put("body", body);
            payload.put("head", head);

            HttpRequest httpRequest =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            getPaytmBaseUrl(environment) +
                                                    "/theia/api/v1/initiateTransaction?mid=" +
                                                    mid +
                                                    "&orderId=" +
                                                    orderId
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            objectMapper.writeValueAsString(
                                                    payload
                                            )
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (
                    response.statusCode() < 200 ||
                    response.statusCode() >= 300
            ) {
                throw new RuntimeException(
                        "Unable to initiate Paytm payment"
                );
            }

            Map<String, Object> responseMap =
                    objectMapper.readValue(
                            response.body(),
                            new TypeReference<>() {}
                    );

            Map<String, Object> responseBody =
                    (Map<String, Object>) responseMap.get("body");

            Map<String, Object> result =
                    new HashMap<>();
            result.put("mid", mid);
            result.put("orderId", orderId);
            result.put(
                    "txnToken",
                    responseBody == null
                            ? null
                            : responseBody.get("txnToken")
            );
            result.put(
                    "scriptUrl",
                    getPaytmBaseUrl(environment) +
                            "/merchantpgpui/checkoutjs/merchants/" +
                            mid +
                            ".js"
            );

            return result;
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Unable to initiate Paytm payment",
                    exception
            );
        }
    }

    public void verifyPaytmPayment(
            PaytmVerifyRequest request
    ) {
        try {
            String mid = requireEnv("PAYTM_MID");
            String merchantKey = requireEnv("PAYTM_MERCHANT_KEY");
            String environment = defaultValue(
                    envConfig.get("PAYTM_ENVIRONMENT"),
                    "STAGING"
            );

            if (
                    request.getOrderId() == null ||
                    request.getOrderId().isBlank()
            ) {
                throw new RuntimeException(
                        "Invalid Paytm order id"
                );
            }

            Map<String, Object> body =
                    new HashMap<>();
            body.put("mid", mid);
            body.put("orderId", request.getOrderId());

            String bodyJson =
                    objectMapper.writeValueAsString(body);

            String signature =
                    PaytmChecksum.generateSignature(
                            bodyJson,
                            merchantKey
                    );

            Map<String, Object> head =
                    new HashMap<>();
            head.put("signature", signature);

            Map<String, Object> payload =
                    new HashMap<>();
            payload.put("body", body);
            payload.put("head", head);

            HttpRequest httpRequest =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            getPaytmBaseUrl(environment) +
                                                    "/v3/order/status"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            objectMapper.writeValueAsString(
                                                    payload
                                            )
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (
                    response.statusCode() < 200 ||
                    response.statusCode() >= 300
            ) {
                throw new RuntimeException(
                        "Unable to verify Paytm payment"
                );
            }

            Map<String, Object> responseMap =
                    objectMapper.readValue(
                            response.body(),
                            new TypeReference<>() {}
                    );

            Map<String, Object> responseBody =
                    (Map<String, Object>) responseMap.get("body");

            Map<String, Object> resultInfo =
                    responseBody == null
                            ? null
                            : (Map<String, Object>) responseBody.get(
                                    "resultInfo"
                            );

            Object resultStatus =
                    resultInfo == null
                            ? null
                            : resultInfo.get("resultStatus");

            if (!"TXN_SUCCESS".equals(resultStatus)) {
                throw new RuntimeException(
                        "Paytm payment failed"
                );
            }
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Unable to verify Paytm payment",
                    exception
            );
        }
    }

    private String requireEnv(String key) {
        String value = envConfig.get(key);

        if (value == null || value.isBlank()) {
            throw new RuntimeException(
                    key + " is not configured"
            );
        }

        return value;
    }

    private String defaultValue(
            String value,
            String fallback
    ) {
        return value == null || value.isBlank()
                ? fallback
                : value;
    }

    private String getPaytmBaseUrl(String environment) {
        return "PRODUCTION".equalsIgnoreCase(environment)
                ? "https://securegw.paytm.in"
                : "https://securegw-stage.paytm.in";
    }

    private String hmacSha256(
            String payload,
            String secret
    ) {
        try {
            Mac mac =
                    Mac.getInstance("HmacSHA256");

            mac.init(
                    new SecretKeySpec(
                            secret.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    )
            );

            byte[] hash =
                    mac.doFinal(
                            payload.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder builder =
                    new StringBuilder();

            for (byte currentByte : hash) {
                builder.append(
                        String.format("%02x", currentByte)
                );
            }

            return builder.toString();
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Unable to generate signature",
                    exception
            );
        }
    }
}
