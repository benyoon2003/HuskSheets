package org.example;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.model.SheetDTO;
import org.example.service.UserService;
import org.example.view.ILoginView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

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

  @PostMapping("/save")
  public ResponseEntity<?> saveSheet(@RequestBody SheetDTO sheetDTO) {
    try {
      byte[] fileBytes = Base64.getDecoder().decode(sheetDTO.getContent());
      Path path = Paths.get("sheets/" + sheetDTO.getFilename());
      Files.write(path, fileBytes);
      return ResponseEntity.ok("Sheet saved successfully");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Error saving sheet: " + e.getMessage());
    }
  }

  @GetMapping("/open")
  public ResponseEntity<?> openSheet(@RequestParam String filename) {
    try {
      Path path = Paths.get("sheets/" + filename);
      byte[] fileBytes = Files.readAllBytes(path);
      String fileContent = Base64.getEncoder().encodeToString(fileBytes);
      return ResponseEntity.ok(fileContent);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Error opening sheet: " + e.getMessage());
    }
  }
}
