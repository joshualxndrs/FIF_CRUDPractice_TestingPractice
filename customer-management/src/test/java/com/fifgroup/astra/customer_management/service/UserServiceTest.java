package com.fifgroup.astra.customer_management.service;

import com.fifgroup.astra.customer_management.model.User;
import com.fifgroup.astra.customer_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(9L);
        testUser.setName("Bernardyo Rikbo");
        testUser.setAddress("Surabaya, Indonesia");
        testUser.setBirthDate(LocalDate.parse("2002-05-27"));
        testUser.setKtpNumber("5678910");
    }

    @Test
    void shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(9L)).thenReturn(Optional.of(testUser));

        Optional<User> user = userService.getUserById(9L);

        assertTrue(user.isPresent());
        assertEquals("Bernardyo Rikbo", user.get().getName());
        verify(userRepository, times(1)).findById(9L);
    }

    @Test
    void shouldCreateUsers() {
        List<User> users = List.of(testUser);
        when(userRepository.saveAll(users)).thenReturn(users);

        List<User> createdUsers = userService.createUsers(users);

        assertEquals(1, createdUsers.size());
        verify(userRepository, times(1)).saveAll(users);
    }

    @Test
    void shouldUpdateUser() {
        when(userRepository.findById(9L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = new User();
        updatedUser.setName("John Doe");
        updatedUser.setAddress("Jakarta, Indonesia");
        updatedUser.setBirthDate(LocalDate.parse("1990-06-15"));
        updatedUser.setKtpNumber("9876543");

        User result = userService.updateUser(9L, updatedUser);

        assertEquals("John Doe", result.getName());
        assertEquals("Jakarta, Indonesia", result.getAddress());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(9L);

        userService.deleteUser(9L);

        verify(userRepository, times(1)).deleteById(9L);
    }
}
