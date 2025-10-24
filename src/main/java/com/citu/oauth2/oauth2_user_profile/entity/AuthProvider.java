package com.citu.oauth2.oauth2_user_profile.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_providers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "providerUserId"})
})
public class AuthProvider {

    public enum ProviderName {
        GOOGLE,
        GITHUB
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ProviderName provider;

    private String providerUserId;

    private String providerEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProviderName getProvider() {
        return provider;
    }

    public void setProvider(ProviderName provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }
}
