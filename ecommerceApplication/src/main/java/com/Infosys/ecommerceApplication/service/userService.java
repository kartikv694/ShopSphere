<<<<<<< HEAD
package com.Infosys.ecommerceApplication.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.model.User;
import com.Infosys.ecommerceApplication.repository.userRepository;

@Service
public class userService {

    @Autowired
    private userRepository repo;

    
    public User registerUser(User user){
    	
    	if(repo.findByEmail(user.getEmail()) != null){
            throw new RuntimeException("Email already exists");
        }
    	
        return repo.save(user);
    }

    public List<User> getAllUsers(){
        return repo.findAll();
    }
=======
package com.Infosys.ecommerceApplication.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.model.User;
import com.Infosys.ecommerceApplication.repository.userRepository;

@Service
public class userService {

    @Autowired
    private userRepository repo;

    
    public User registerUser(User user){
    	
    	if(repo.findByEmail(user.getEmail()) != null){
            throw new RuntimeException("Email already exists");
        }
    	
        return repo.save(user);
    }

    public List<User> getAllUsers(){
        return repo.findAll();
    }
>>>>>>> f9560452447b15875efaba1e20a1c6bcf0260cab
}