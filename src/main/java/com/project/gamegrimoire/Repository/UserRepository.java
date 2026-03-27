package com.project.gamegrimoire.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.gamegrimoire.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by email
    Optional<User> findByEmail(String email);
    // Check if email already exists
    boolean existsByEmail(String email);
}
