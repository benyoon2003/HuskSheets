package org.example;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.model.Result;
import org.example.model.Spreadsheet;
import org.example.service.SheetService;
import org.example.service.UserService;
import org.example.view.ILoginView;

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
  private SheetService sheetService;



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


  @GetMapping("/getPublishers")
  public ResponseEntity<?> getPublishers() {
    logger.info("Received request to get publishers");

    List<AppUser> publishers = userService.getPublishers();

    if(publishers.size() > 0){
      logger.info("Publishers found");
      return ResponseEntity.ok(publishers);
    } else {
      logger.warn("No publishers found");
      return ResponseEntity.status(401).body("No publishers found");
    }
  }

  @PostMapping("/createSheet")
  public ResponseEntity<?> createSheet(@RequestBody AppUser user, String name) {
    logger.info("Received request to create sheet");

    try{
      Spreadsheet sheet = sheetService.createSheet(user, name);
    } catch(Exception e){
      return ResponseEntity.status(401).body("Sheet could not be created; already exists");
    }
    return ResponseEntity.ok("Sheet created successfully");
  }

  @PostMapping("/getSheets")
  public ResponseEntity<?> getSheets(@RequestBody AppUser user) {
    logger.info("Received request to get sheets");

    try {
      List<Spreadsheet> sheets = userService.getSheets(user);
    } catch(Exception e){
      return ResponseEntity.status(401).body("User has no sheets");
    }
    return ResponseEntity.ok("sheets returned");
  }

  @DeleteMapping("/deleteSheet")
  public ResponseEntity<?> deleteSheet(@RequestBody AppUser user, Spreadsheet sheet) {
    logger.info("Received request to delete sheet");

    try {
      boolean deleted = sheetService.deleteSheet(user, sheet);
      if (deleted) {
        return ResponseEntity.ok("Sheet deleted successfully");
      } else {
        return ResponseEntity.ok("Failed to delete sheet");
      }
    } catch(Exception e){
      return ResponseEntity.status(401).body("Sheet cannot be deleted");
    }
  }

//  @PostMapping("/getUpdatesForSubscription")
//  public ResponseEntity<Result<?> getUpdatesForSubscription(@RequestBody Argument argument) {
//    logger.info("Received request to get updates for subscription");
//
//    List<Spreadsheet> updates = sheetService.getUpdatesForSubscription(argument);
//    return ResponseEntity.ok(new Result<>(true, updates, null));
//  }


//  @PostMapping("/getUpdatesForPublished")
//  public ResponseEntity<Result<List<Spreadsheet>>> getUpdatesForPublished(@RequestBody Argument argument) {
//    logger.info("Received request to get updates for published sheets");
//
//    List<Spreadsheet> updates = sheetService.getUpdatesForPublished(argument);
//    return ResponseEntity.ok(new Result<>(true, updates, null));
//  }
//
//  @PutMapping("/updatePublished")
//  public ResponseEntity<Result<Spreadsheet>> updatePublished(@RequestBody Argument argument) {
//    logger.info("Received request to update published sheet");
//
//    Spreadsheet updatedSheet = sheetService.updatePublished(argument);
//    return ResponseEntity.ok(new Result<>(true, updatedSheet, "Published sheet updated successfully"));
//  }
//
//  @PutMapping("/updateSubscription")
//  public ResponseEntity<Result<Spreadsheet>> updateSubscription(@RequestBody Argument argument) {
//    logger.info("Received request to update subscription");
//
//    Spreadsheet updatedSheet = sheetService.updateSubscription(argument);
//    return ResponseEntity.ok(new Result<>(true, updatedSheet, "Subscription updated successfully"));
//  }

}
