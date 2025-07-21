package com.example.springsecurityoauth2rest.service;

import com.example.springsecurityoauth2rest.entity.User;
import com.example.springsecurityoauth2rest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password", "test@example.com", "Test", "User");
        testUser.setId(1L);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user2 = new User("user2", "password2", "user2@example.com", "User", "Two");
        List<User> expectedUsers = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> actualUser = userService.getUserById(1L);

        // Assert
        assertTrue(actualUser.isPresent());
        assertEquals(testUser, actualUser.get());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> actualUser = userService.getUserById(999L);

        // Assert
        assertFalse(actualUser.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    void testGetUserByUsername() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> actualUser = userService.getUserByUsername("testuser");

        // Assert
        assertTrue(actualUser.isPresent());
        assertEquals(testUser, actualUser.get());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> actualUser = userService.getUserByUsername("nonexistent");

        // Assert
        assertFalse(actualUser.isPresent());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void testGetUserByEmail() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> actualUser = userService.getUserByEmail("test@example.com");

        // Assert
        assertTrue(actualUser.isPresent());
        assertEquals(testUser, actualUser.get());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testGetUserByEmailNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> actualUser = userService.getUserByEmail("nonexistent@example.com");

        // Assert
        assertFalse(actualUser.isPresent());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testCreateUser() {
        // Arrange
        User newUser = new User("newuser", "plainpassword", "new@example.com", "New", "User");
        User savedUser = new User("newuser", "encodedpassword", "new@example.com", "New", "User");
        savedUser.setId(2L);
        
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User createdUser = userService.createUser(newUser);

        // Assert
        assertEquals(savedUser, createdUser);
        assertEquals("encodedpassword", newUser.getPassword()); // Password should be encoded
        verify(passwordEncoder).encode("plainpassword");
        verify(userRepository).save(newUser);
    }

    @Test
    void testUpdateUser() {
        // Arrange
        User updatedUser = new User("testuser", "newpassword", "newemail@example.com", "Updated", "User");
        updatedUser.setId(1L);
        
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        // Act
        User actualUser = userService.updateUser(updatedUser);

        // Assert
        assertEquals(updatedUser, actualUser);
        verify(userRepository).save(updatedUser);
    }

    @Test
    void testDeleteUser() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testExistsByUsername() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean exists = userService.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void testExistsByUsernameNotExists() {
        // Arrange
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        boolean exists = userService.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
        verify(userRepository).existsByUsername("nonexistent");
    }

    @Test
    void testExistsByEmail() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean exists = userService.existsByEmail("test@example.com");

        // Assert
        assertTrue(exists);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void testExistsByEmailNotExists() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean exists = userService.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }
}
