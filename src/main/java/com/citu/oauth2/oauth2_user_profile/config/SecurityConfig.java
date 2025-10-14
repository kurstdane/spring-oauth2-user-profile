package com.citu.oauth2.oauth2_user_profile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.citu.oauth2.oauth2_user_profile.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/css/**", "/js/**").permitAll()
                .requestMatchers("/profile", "/profile/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                    .defaultSuccessUrl("/profile", true)
                    .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService) 
                    )
                )
            .logout(logout -> logout
                .logoutSuccessUrl("/") 
                .permitAll()
            );

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}