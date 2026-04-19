package com.Infosys.ecommerceApplication.controller;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Infosys.ecommerceApplication.model.User;
import com.Infosys.ecommerceApplication.service.userService;

@RestController
@RequestMapping("/auth")
public class userController {
	
	@Autowired
    private userService service;

    @PostMapping("/register")
    public User register(@Valid @RequestBody User user){
        return service.registerUser(user);
    }

    @GetMapping
    public List<User> getUsers(){
        return service.getAllUsers();
    }
}
