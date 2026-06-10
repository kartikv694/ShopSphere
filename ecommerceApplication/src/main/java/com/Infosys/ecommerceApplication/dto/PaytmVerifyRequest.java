package com.Infosys.ecommerceApplication.dto;

import java.util.Map;

public class PaytmVerifyRequest {

    private String orderId;
    private Map<String, Object> gatewayResponse;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Map<String, Object> getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(Map<String, Object> gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
}
