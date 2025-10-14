package com.citu.oauth2.oauth2_user_profile.repository;

import com.citu.oauth2.oauth2_user_profile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}