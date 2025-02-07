package com.fifgroup.astra.customer_management.service;

import com.fifgroup.astra.customer_management.model.User;
import com.fifgroup.astra.customer_management.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        logger.info("Fetching all users from database");
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        return userRepository.findById(id);
    }

    public List<User> createUsers(List<User> users) {
        logger.info("Creating {} users", users.size());
        return userRepository.saveAll(users);
    }

    public User updateUser(Long id, User userDetails) {
        logger.info("Updating user with ID: {}", id);
        return userRepository.findById(id).map(user -> {
            user.setName(userDetails.getName());
            user.setAddress(userDetails.getAddress());
            user.setBirthDate(userDetails.getBirthDate());
            user.setKtpNumber(userDetails.getKtpNumber());
            logger.info("Updated user with ID: {}", id);
            return userRepository.save(user);
        }).orElseThrow(() -> {
            logger.error("User with ID {} not found", id);
            return new RuntimeException("User not found");
        });
    }

    public void deleteUser(Long id) {
        logger.warn("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
    }
}
