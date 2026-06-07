package com.Infosys.ecommerceApplication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class welcomeController {
	
	@GetMapping("/api/welcome")
	public String welcome() {
	    return "This is a protected API ✅";
	}
	
}
