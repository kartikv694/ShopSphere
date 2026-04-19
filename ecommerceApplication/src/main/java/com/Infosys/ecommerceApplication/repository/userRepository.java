<<<<<<< HEAD
package com.Infosys.ecommerceApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Infosys.ecommerceApplication.model.User;

public interface userRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
}
=======
package com.Infosys.ecommerceApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Infosys.ecommerceApplication.model.User;

public interface userRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
}
>>>>>>> f9560452447b15875efaba1e20a1c6bcf0260cab
