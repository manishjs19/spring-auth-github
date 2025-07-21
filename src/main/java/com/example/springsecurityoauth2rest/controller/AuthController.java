package com.example.springsecurityoauth2rest.controller;

import com.example.springsecurityoauth2rest.security.GitHubTokenAuthenticationFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            if (details instanceof GitHubTokenAuthenticationFilter.GitHubUser) {
                GitHubTokenAuthenticationFilter.GitHubUser githubUser = 
                    (GitHubTokenAuthenticationFilter.GitHubUser) details;
                response.put("authenticated", true);
                response.put("username", githubUser.getLogin());
                response.put("name", githubUser.getName());
                response.put("email", githubUser.getEmail());
                response.put("avatar", githubUser.getAvatarUrl());
                response.put("authorities", authentication.getAuthorities());
            } else {
                response.put("authenticated", true);
                response.put("username", authentication.getName());
                response.put("authorities", authentication.getAuthorities());
            }
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/login-info")
    public ResponseEntity<Map<String, Object>> getLoginInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Token-based authentication required");
        response.put("instructions", Map.of(
            "step1", "Get your GitHub Personal Access Token from: https://github.com/settings/tokens",
            "step2", "Include the token in Authorization header: Bearer <your-token>",
            "step3", "Make API calls to protected endpoints with the token",
            "example", "curl -H \"Authorization: Bearer <your-token>\" http://localhost:8085/api/auth/user"
        ));
        response.put("scopes_required", "No specific scopes required for basic authentication, but 'user:email' for email access");
        return ResponseEntity.ok(response);
    }
}
