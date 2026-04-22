package com.Infosys.ecommerceApplication.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.Infosys.ecommerceApplication.dto.AuthResponse;
import com.Infosys.ecommerceApplication.dto.LoginRequest;
import com.Infosys.ecommerceApplication.model.User;
import com.Infosys.ecommerceApplication.repository.userRepository;
import com.Infosys.ecommerceApplication.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class loginController {

    @Autowired
    private userRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        // return DTO instead of raw token
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail()));
    }
}