package com.Infosys.ecommerceApplication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class welcomeController {
	
	@GetMapping("/Welcome")
	public String welcome() {
		return "Welcome to first spring boot development project";
	}
	
}
