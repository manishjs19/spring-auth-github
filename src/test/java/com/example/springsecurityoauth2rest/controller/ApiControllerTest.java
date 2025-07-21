package com.example.springsecurityoauth2rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for unit testing
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/public/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Public endpoint is accessible"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUserProfileEndpoint() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.provider").exists());
    }

    @Test
    @WithMockUser(username = "admin")
    void testAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.message").value("Admin endpoint accessed successfully"))
                .andExpect(jsonPath("$.accessedBy").value("admin"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(username = "user")
    void testUserDataEndpoint() throws Exception {
        mockMvc.perform(get("/api/user/data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.githubUsername").value("user"))
                .andExpect(jsonPath("$.authProvider").value("GitHub Token"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(username = "admin")
    void testAdminSettingsEndpoint() throws Exception {
        String settingsJson = "{\"theme\":\"dark\",\"notifications\":true}";
        
        mockMvc.perform(post("/api/admin/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(settingsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Settings updated successfully"))
                .andExpect(jsonPath("$.updatedBy").value("admin"))
                .andExpect(jsonPath("$.settingsCount").value(2))
                .andExpect(jsonPath("$.authProvider").value("GitHub Token"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
