package com.citu.oauth2.oauth2_user_profile.service;

import com.citu.oauth2.oauth2_user_profile.model.AuthProvider;
import com.citu.oauth2.oauth2_user_profile.model.User;
import com.citu.oauth2.oauth2_user_profile.repository.AuthProviderRepository;
import com.citu.oauth2.oauth2_user_profile.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AuthProviderRepository authProviderRepository;

    public CustomOAuth2UserService(UserRepository userRepository, AuthProviderRepository authProviderRepository) {
        this.userRepository = userRepository;
        this.authProviderRepository = authProviderRepository;
    }

    private Map<String, Object> extractAttributes(String registrationId, Map<String, Object> rawAttributes) {
        Map<String, Object> attributes = new HashMap<>(rawAttributes);

        if ("github".equalsIgnoreCase(registrationId)) {
            if (attributes.containsKey("avatar_url")) {
                attributes.put("picture", attributes.get("avatar_url"));
            }
            if (attributes.get("name") == null && attributes.containsKey("login")) {
                 attributes.put("name", attributes.get("login"));
            }
        }
        
        return attributes;
    }
    

    private Map<String, Object> mergeDbAttributes(Map<String, Object> oauthAttributes, User user) {
        Map<String, Object> finalAttributes = new HashMap<>(oauthAttributes);

        finalAttributes.put("displayName", user.getDisplayName());
        finalAttributes.put("bio", user.getBio());
        finalAttributes.put("email", user.getEmail()); // Ensure the email is the DB value
        
        return finalAttributes;
    }


    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider.Provider provider = AuthProvider.Provider.valueOf(registrationId.toUpperCase());
        
        Map<String, Object> attributes = extractAttributes(registrationId, oauth2User.getAttributes());
        
        String providerUserId = oauth2User.getName(); 
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        return authProviderRepository.findByProviderAndProviderUserId(provider, providerUserId)
            .map(authProvider -> {
                User user = authProvider.getUser();
                
                user.setAvatarUrl(picture); 
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user); 

                Map<String, Object> finalAttributes = mergeDbAttributes(attributes, user);

                return new DefaultOAuth2User(
                    oauth2User.getAuthorities(), 
                    finalAttributes, 
                    userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
                );
            })
            .orElseGet(() -> {
                
                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setDisplayName(name != null ? name : "User");
                    newUser.setAvatarUrl(picture);
                    return userRepository.save(newUser);
                });

                AuthProvider authProvider = new AuthProvider();
                authProvider.setProvider(provider);
                authProvider.setProviderUserId(providerUserId);
                authProvider.setProviderEmail(email);
                authProvider.setUser(user);
                authProviderRepository.save(authProvider);
                
                Map<String, Object> finalAttributes = mergeDbAttributes(attributes, user);

                return new DefaultOAuth2User(
                    oauth2User.getAuthorities(), 
                    finalAttributes, 
                    userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
                );
            });
    }
}