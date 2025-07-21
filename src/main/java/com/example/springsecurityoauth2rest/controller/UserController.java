package com.example.springsecurityoauth2rest.controller;

import com.example.springsecurityoauth2rest.entity.User;
import com.example.springsecurityoauth2rest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        if (userService.existsByUsername(user.getUsername())) {
            response.put("error", "Username is already taken!");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            response.put("error", "Email is already in use!");
            return ResponseEntity.badRequest().body(response);
        }
        
        User createdUser = userService.createUser(user);
        response.put("message", "User created successfully");
        response.put("user", createdUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isEmpty()) {
            response.put("error", "User not found");
            return ResponseEntity.notFound().build();
        }
        
        User user = userOptional.get();
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        
        User updatedUser = userService.updateUser(user);
        response.put("message", "User updated successfully");
        response.put("user", updatedUser);
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        
        if (!userService.getUserById(id).isPresent()) {
            response.put("error", "User not found");
            return ResponseEntity.notFound().build();
        }
        
        userService.deleteUser(id);
        response.put("message", "User deleted successfully");
        
        return ResponseEntity.ok(response);
    }
}
