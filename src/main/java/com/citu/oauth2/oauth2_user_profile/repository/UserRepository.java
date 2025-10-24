package com.citu.oauth2.oauth2_user_profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citu.oauth2.oauth2_user_profile.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
