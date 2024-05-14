package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user) throws Exception {
        logger.info("Attempting to register user: {}", user.getUsername());

        if (userRepository.findById(user.getUsername()).isPresent()) {
            logger.error("Username already exists: {}", user.getUsername());
            throw new Exception("Username already exists!");
        }

        userRepository.save(user);
        logger.info("User registered successfully: {}", user.getUsername());
    }

    public User findUserByUsername(String username) {
        logger.info("Looking for user by username: {}", username);
        return userRepository.findById(username).orElse(null);
    }
}
