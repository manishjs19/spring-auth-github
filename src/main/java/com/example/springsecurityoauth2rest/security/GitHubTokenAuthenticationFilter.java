package com.example.springsecurityoauth2rest.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitHubTokenAuthenticationFilter extends OncePerRequestFilter {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Define admin users here - Replace with your actual GitHub usernames
    private final List<String> adminUsers = Arrays.asList(
        "your-github-username", // Replace with your GitHub username
        "admin-user2"           // Add more admin usernames as needed
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                GitHubUser githubUser = validateTokenWithGitHub(token);
                
                if (githubUser != null) {
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    
                    // Check if user is admin
                    if (adminUsers.contains(githubUser.getLogin())) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            githubUser.getLogin(), 
                            null, 
                            authorities
                        );
                    
                    // Set additional user details
                    authentication.setDetails(githubUser);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                logger.warn("GitHub token validation failed: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private GitHubUser validateTokenWithGitHub(String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/user"))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "Spring-Boot-App")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode jsonNode = objectMapper.readTree(response.body());
            return new GitHubUser(
                jsonNode.get("login").asText(),
                jsonNode.has("name") && !jsonNode.get("name").isNull() ? jsonNode.get("name").asText() : null,
                jsonNode.has("email") && !jsonNode.get("email").isNull() ? jsonNode.get("email").asText() : null,
                jsonNode.has("avatar_url") ? jsonNode.get("avatar_url").asText() : null
            );
        }
        
        return null;
    }

    // Inner class for GitHub user data
    public static class GitHubUser {
        private String login;
        private String name;
        private String email;
        private String avatarUrl;

        public GitHubUser(String login, String name, String email, String avatarUrl) {
            this.login = login;
            this.name = name;
            this.email = email;
            this.avatarUrl = avatarUrl;
        }

        // Getters
        public String getLogin() { return login; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getAvatarUrl() { return avatarUrl; }
    }
}
