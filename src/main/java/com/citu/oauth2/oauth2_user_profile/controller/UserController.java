package com.citu.oauth2.oauth2_user_profile.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OAuth2User oauthUser) {
        System.out.println("OAuth user: " + oauthUser);

        if (oauthUser == null) {
            return "redirect:/login";
        }

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        model.addAttribute("name", name != null ? name : "Unknown");
        model.addAttribute("email", email != null ? email : "No email");

        return "profile";
    }
}
