package org.example;

import org.example.model.AppUser;
import org.example.model.SheetDTO;
import org.example.service.UserService;
import org.example.model.Sheet;
import org.example.repository.SheetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class server {

    @Autowired
    private UserService userService;

    @Autowired
    private SheetRepository sheetRepository;

    // Get all publishers
    @GetMapping("/getPublishers")
    public ResponseEntity<List<AppUser>> getPublishers() {
        try {
            List<AppUser> publishers = userService.getAllUsers();
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Create a new sheet
    @PostMapping("/createSheet")
    public ResponseEntity<?> createSheet(@RequestBody SheetDTO sheetDTO) {
        try {
            Sheet sheet = new Sheet();
            sheet.setName(sheetDTO.getFilename());
            sheet.setContent(sheetDTO.getContent());
            sheetRepository.save(sheet);
            return ResponseEntity.ok("Sheet created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    // Get all sheets for a publisher
    @PostMapping("/getSheets")
    public ResponseEntity<List<Sheet>> getSheets(@RequestBody SheetDTO sheetDTO) {
        try {
            List<Sheet> sheets = sheetRepository.findAllByPublisher(sheetDTO.getPublisher());
            return ResponseEntity.ok(sheets);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Update published sheet
    @PostMapping("/updatePublished")
    public ResponseEntity<?> updatePublished(@RequestBody SheetDTO sheetDTO) {
        try {
            Sheet sheet = sheetRepository.findById(sheetDTO.getFilename()).orElse(null);
            if (sheet != null) {
                sheet.setContent(sheetDTO.getContent());
                sheetRepository.save(sheet);
                return ResponseEntity.ok("Sheet updated successfully");
            } else {
                return ResponseEntity.status(404).body("Sheet not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    // Register a publisher (new implementation)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SheetDTO sheetDTO) {
        try {
            // Your registration logic here
            return ResponseEntity.ok("Publisher registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    // Get updates for subscription
    @PostMapping("/getUpdatedForSubscription")
    public ResponseEntity<?> getUpdatedForSubscription(@RequestBody SheetDTO sheetDTO) {
        try {
            // Your logic to get updates for subscription here
            return ResponseEntity.ok("Updates for subscription retrieved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    // Get updates for published sheets
    @PostMapping("/getUpdatedForPublished")
    public ResponseEntity<?> getUpdatedForPublished(@RequestBody SheetDTO sheetDTO) {
        try {
            // Your logic to get updates for published here
            return ResponseEntity.ok("Updates for published retrieved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    // Update subscription
    @PostMapping("/updateSubscription")
    public ResponseEntity<?> updateSubscription(@RequestBody SheetDTO sheetDTO) {
        try {
            // Your logic to update subscription here
            return ResponseEntity.ok("Subscription updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }
}
