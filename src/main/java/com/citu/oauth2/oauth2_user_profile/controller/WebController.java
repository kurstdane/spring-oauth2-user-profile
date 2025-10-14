package com.citu.oauth2.oauth2_user_profile.controller;

import com.citu.oauth2.oauth2_user_profile.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    private final UserRepository userRepository;

    public WebController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        String email = oauth2User.getAttribute("email");
        model.addAttribute("oauth2User", oauth2User);
        
        userRepository.findByEmail(email).ifPresent(user -> {
            model.addAttribute("userProfile", user);
        });

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestParam String displayName,
            @RequestParam String bio,
            Model model) {

        String email = oauth2User.getAttribute("email");
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setDisplayName(displayName);
            user.setBio(bio);
            userRepository.save(user);
        });

        return "redirect:/profile";
    }
}