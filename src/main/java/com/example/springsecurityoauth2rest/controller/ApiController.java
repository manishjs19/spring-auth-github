package com.example.springsecurityoauth2rest.controller;

import com.example.springsecurityoauth2rest.security.GitHubTokenAuthenticationFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/public/health")
    public ResponseEntity<Map<String, String>> publicHealth() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Public endpoint is accessible");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> profile = new HashMap<>();
        
        // Get GitHub user details from authentication
        if (authentication != null && authentication.getDetails() instanceof GitHubTokenAuthenticationFilter.GitHubUser) {
            GitHubTokenAuthenticationFilter.GitHubUser githubUser = 
                (GitHubTokenAuthenticationFilter.GitHubUser) authentication.getDetails();
            
            profile.put("username", githubUser.getLogin());
            profile.put("name", githubUser.getName());
            profile.put("email", githubUser.getEmail());
            profile.put("avatarUrl", githubUser.getAvatarUrl());
            profile.put("authorities", authentication.getAuthorities());
            profile.put("provider", "GitHub");
        } else {
            profile.put("username", authentication != null ? authentication.getName() : "anonymous");
            profile.put("authorities", authentication != null ? authentication.getAuthorities() : "none");
            profile.put("provider", "Unknown");
        }
        
        profile.put("authenticated", authentication != null && authentication.isAuthenticated());
        
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/user/data")
    public ResponseEntity<Map<String, Object>> getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        
        if (authentication != null) {
            String username = authentication.getName();
            data.put("message", "Hello " + username + "!");
            data.put("githubUsername", username);
            data.put("authorities", authentication.getAuthorities());
            data.put("timestamp", java.time.Instant.now().toString());
            data.put("authProvider", "GitHub Token");
            
            // Add GitHub user details if available
            if (authentication.getDetails() instanceof GitHubTokenAuthenticationFilter.GitHubUser) {
                GitHubTokenAuthenticationFilter.GitHubUser githubUser = 
                    (GitHubTokenAuthenticationFilter.GitHubUser) authentication.getDetails();
                data.put("displayName", githubUser.getName());
                data.put("email", githubUser.getEmail());
            }
        } else {
            data.put("message", "No authentication found");
            data.put("timestamp", java.time.Instant.now().toString());
        }
        
        return ResponseEntity.ok(data);
    }

    @GetMapping("/admin/users")
    public ResponseEntity<Map<String, Object>> getUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", java.util.Arrays.asList("user1", "admin", "developer"));
        response.put("total", 3);
        response.put("message", "Admin endpoint accessed successfully");
        response.put("accessedBy", authentication != null ? authentication.getName() : "unknown");
        response.put("adminAuthorities", authentication != null ? authentication.getAuthorities() : "none");
        response.put("timestamp", java.time.Instant.now().toString());
        
        // Add GitHub user details if available
        if (authentication != null && authentication.getDetails() instanceof GitHubTokenAuthenticationFilter.GitHubUser) {
            GitHubTokenAuthenticationFilter.GitHubUser githubUser = 
                (GitHubTokenAuthenticationFilter.GitHubUser) authentication.getDetails();
            response.put("adminDisplayName", githubUser.getName());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/settings")
    public ResponseEntity<Map<String, Object>> updateSettings(@RequestBody Map<String, Object> settings) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Settings updated successfully");
        response.put("updatedBy", authentication != null ? authentication.getName() : "unknown");
        response.put("timestamp", java.time.Instant.now().toString());
        response.put("settingsCount", settings.size());
        response.put("authProvider", "GitHub Token");
        
        // Add GitHub user details if available
        if (authentication != null && authentication.getDetails() instanceof GitHubTokenAuthenticationFilter.GitHubUser) {
            GitHubTokenAuthenticationFilter.GitHubUser githubUser = 
                (GitHubTokenAuthenticationFilter.GitHubUser) authentication.getDetails();
            response.put("updatedByName", githubUser.getName());
            response.put("updatedByEmail", githubUser.getEmail());
        }
        
        return ResponseEntity.ok(response);
    }
}
