package com.Infosys.ecommerceApplication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.model.User;
import com.Infosys.ecommerceApplication.repository.userRepository;

@Service
public class userService {

    @Autowired
    private userRepository repo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public User registerUser(User user){

        // check if email already exists
        if(repo.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        // hash password before saving
        user.setPassword(
                encoder.encode(user.getPassword())
        );

        return repo.save(user);
    }

    public List<User> getAllUsers(){
        return repo.findAll();
    }
}