package com.Infosys.ecommerceApplication.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.Infosys.ecommerceApplication.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public Map<String, Long> getDashboardData() {

        Map<String, Long> response = new HashMap<>();

        response.put("totalUsers", dashboardService.getTotalUsers());
        response.put("totalProducts", dashboardService.getTotalProducts());

        return response;
    }
}
