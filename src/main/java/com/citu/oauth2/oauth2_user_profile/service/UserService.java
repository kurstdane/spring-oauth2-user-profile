package com.citu.oauth2.oauth2_user_profile.service;


import com.citu.oauth2.oauth2_user_profile.config.CustomOAuth2User;
import com.citu.oauth2.oauth2_user_profile.entity.User;
import com.citu.oauth2.oauth2_user_profile.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processOAuthPostLogin(CustomOAuth2User oAuth2User) {
        Optional<User> existingUser = userRepository.findByEmail(oAuth2User.getEmail());
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User();
            newUser.setOauthId(oAuth2User.getId());
            newUser.setName(oAuth2User.getName());
            newUser.setEmail(oAuth2User.getEmail());
            newUser.setProvider("oauth2");
            return userRepository.save(newUser);
        }
    }
}
