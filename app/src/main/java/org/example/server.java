package org.example;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.service.UserService;
import org.example.model.Sheet;
import org.example.repository.SheetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api")
public class server {

  private static final Logger logger = LoggerFactory.getLogger(org.example.controller.UserController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private SheetRepository sheetRepository;

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

  @PostMapping("/saveSheet")
  public ResponseEntity<?> saveSheet(@RequestBody Sheet sheet) {
    logger.info("Received request to save sheet: {}", sheet.getName());

    try {
      sheetRepository.save(sheet);
      logger.info("Sheet saved successfully: {}", sheet.getName());
      return ResponseEntity.ok("Sheet saved successfully");
    } catch (Exception e) {
      logger.error("Error saving sheet: {}", sheet.getName(), e);
      return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
    }
  }

  @GetMapping("/getSheets")
  public ResponseEntity<List<Sheet>> getSheets() {
    logger.info("Received request to get all sheets");

    try {
      List<Sheet> sheets = sheetRepository.findAll();
      return ResponseEntity.ok(sheets);
    } catch (Exception e) {
      logger.error("Error getting sheets", e); // add time stamps
      return ResponseEntity.status(500).body(null);
    }
  }

  @DeleteMapping("/deleteSheet/{name}")
  public ResponseEntity<?> deleteSheet(@PathVariable String name) {
      logger.info("Received request to delete sheet: {}", name);
  
      try {
          sheetRepository.deleteById(name);
          logger.info("Sheet deleted successfully: {}", name);
          return ResponseEntity.ok("Sheet deleted successfully");
      } catch (Exception e) {
          logger.error("Error deleting sheet: {}", name, e);
          return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
      }
  }
}
