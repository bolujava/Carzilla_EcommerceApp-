package com.decagon.ecommerce.repositories;

import com.decagon.ecommerce.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepositories extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByUsernameAndRole(String username, String role);

}
