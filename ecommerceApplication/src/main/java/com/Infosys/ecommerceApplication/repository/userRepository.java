
package com.Infosys.ecommerceApplication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Infosys.ecommerceApplication.model.User;

public interface userRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}

