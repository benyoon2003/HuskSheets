package org.example;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.service.UserService;
import org.example.view.ILoginView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api")
public class server {

  private static final Logger logger = LoggerFactory.getLogger(org.example.controller.UserController.class);

  @Autowired
  private UserService userService;



  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody AppUser user) {
    logger.info("Received request to register user: {}", user.getUsername());

    try {
      userService.registerUser(user);
      logger.info("User registered successfully: {}", user.getUsername());
      return ResponseEntity.ok("User registered successfully");
    } catch (Exception e) {
      logger.error("Error registering user: {}", user.getUsername(), e);
      return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
    }
  }

  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticateUser(@RequestBody AppUser user) {
    logger.info("Received request to authenticate user: {}", user.getUsername());

    AppUser foundUser = userService.findUserByUsername(user.getUsername());
    if (foundUser != null && foundUser.getPassword().equals(user.getPassword())) {
      logger.info("User authenticated successfully: {}", user.getUsername());
      return ResponseEntity.ok("Login successful");
    } else {
      logger.warn("Authentication failed for user: {}", user.getUsername());
      return ResponseEntity.status(401).body("Invalid credentials");
    }
  }

}
