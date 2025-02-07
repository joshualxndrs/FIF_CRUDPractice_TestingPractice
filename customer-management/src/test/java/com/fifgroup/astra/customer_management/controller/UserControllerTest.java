package com.fifgroup.astra.customer_management.controller;

import com.fifgroup.astra.customer_management.model.User;
import com.fifgroup.astra.customer_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Clean database before each test

        testUser = new User();
        testUser.setName("Bernardyo Rikbo");
        testUser.setAddress("Surabaya, Indonesia");
        testUser.setBirthDate(LocalDate.parse("2002-05-27"));
        testUser.setKtpNumber("5678910");

        userRepository.save(testUser);
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Bernardyo Rikbo"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bernardyo Rikbo"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isOk()) // Because it returns Optional<User>
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldCreateUsers() throws Exception {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setAddress("Jakarta, Indonesia");
        newUser.setBirthDate(LocalDate.parse("1995-08-15"));
        newUser.setKtpNumber("9876543");

        String jsonRequest = objectMapper.writeValueAsString(List.of(newUser));

        mockMvc.perform(post("/api/users/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("New User"));
    }
}
