package com.citu.oauth2.oauth2_user_profile.repository;

import com.citu.oauth2.oauth2_user_profile.model.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
    Optional<AuthProvider> findByProviderAndProviderUserId(AuthProvider.Provider provider, String providerUserId);
}