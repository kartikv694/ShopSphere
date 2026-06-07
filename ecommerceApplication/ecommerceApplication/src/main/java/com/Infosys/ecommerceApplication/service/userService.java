package com.Infosys.ecommerceApplication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.dto.PasswordUpdateRequest;
import com.Infosys.ecommerceApplication.dto.ProfileUpdateRequest;
import com.Infosys.ecommerceApplication.dto.UserProfileResponse;
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

    public UserProfileResponse getProfile(String email) {
        User user = findUserByEmail(email);

        return toProfileResponse(user);
    }

    public UserProfileResponse updateProfile(
            String currentEmail,
            ProfileUpdateRequest request
    ) {
        User user = findUserByEmail(currentEmail);

        repo.findByEmail(request.getEmail())
                .filter(existingUser ->
                        !existingUser.getId().equals(user.getId())
                )
                .ifPresent(existingUser -> {
                    throw new RuntimeException("Email already exists");
                });

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return toProfileResponse(repo.save(user));
    }

    public void updatePassword(
            String email,
            PasswordUpdateRequest request
    ) {
        User user = findUserByEmail(email);

        if (!encoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(
                encoder.encode(request.getNewPassword())
        );

        repo.save(user);
    }

    private User findUserByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    private UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
