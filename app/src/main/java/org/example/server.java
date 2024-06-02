package org.example;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.model.Result;
import org.example.model.SheetDTO;
import java.util.Base64;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class server {

//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private SheetRepository sheetRepository;


    /**
     * List of all available users
     */
    private List<IAppUser> availUsers = new ArrayList<>();


    // Get all publishers
//    @GetMapping("/getPublishers")
//    public ResponseEntity<List<AppUser>> getPublishers() {
//        try {
//            List<AppUser> publishers = userService.getAllUsers();
//            return ResponseEntity.ok(publishers);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(null);
//        }
//    }
//
    // Create a new sheet
    @GetMapping("/createSheet")
    public ResponseEntity<Result> createSheet(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody SheetDTO sheetDTO) {
        try {
            // Decode the Basic Auth header
            String[] credentials = decodeBasicAuth(authHeader);
            String username = credentials[0];
            String password = credentials[1];
            System.out.println(username + ": " + password);
            if (credentials == null || credentials.length != 2 ||
                    !existingUser(username, password)) {
                return ResponseEntity.status(401).body(new Result(
                        false, "Unauthorized", new ArrayList<>()));
            }

            String publisher = sheetDTO.getPublisher();
            String sheet = sheetDTO.getSheet();

            if (publisher != username) {
                return ResponseEntity.status(401).body(new Result(
                        false, "Unauthorized: sender is not owner of sheet",
                        new ArrayList<>()));
            }
            else if (sheet == "") {
                return ResponseEntity.status(401).body(new Result(
                        false, "Sheet name cannot be blank ",
                        new ArrayList<>()));
            }
            else if (hasSheet(sheet, publisher)) {
                return ResponseEntity.status(401).body(new Result(
                        false, "Sheet already exists: " + sheet,
                        new ArrayList<>()));
            }
            else {
                findUser(username, password).addSheet(sheet);
                return ResponseEntity.status(401).body(new Result(
                        true, null,
                        new ArrayList<>()));

            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Result(
                    false, "Internal Server Error: " + e.getMessage(), new ArrayList<>()));
        }
    }

    private boolean hasSheet(String sheet, String publisher) {
        for (IAppUser user : availUsers) {
            if (user.getUsername().equals(publisher) && user.doesSheetExist(sheet)) {
                return true;
            }
        }
        return false;
    }

    private IAppUser findUser(String username, String password) {
        for (IAppUser user : this.availUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

//
//    // Get all sheets for a publisher
//    @PostMapping("/getSheets")
//    public ResponseEntity<List<Sheet>> getSheets(@RequestBody SheetDTO sheetDTO) {
//        try {
//            List<Sheet> sheets = sheetRepository.findAllByPublisher(sheetDTO.getPublisher());
//            return ResponseEntity.ok(sheets);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(null);
//        }
//    }
//
//    // Update published sheet
//    @PostMapping("/updatePublished")
//    public ResponseEntity<?> updatePublished(@RequestBody SheetDTO sheetDTO) {
//        try {
//            Sheet sheet = sheetRepository.findById(sheetDTO.getFilename()).orElse(null);
//            if (sheet != null) {
//                sheet.setContent(sheetDTO.getContent());
//                sheetRepository.save(sheet);
//                return ResponseEntity.ok("Sheet updated successfully");
//            } else {
//                return ResponseEntity.status(404).body("Sheet not found");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
//        }
//    }


    // Register a publisher (new implementation)
    @GetMapping("/register")
    public ResponseEntity<Result> register(@RequestHeader("Authorization") String authHeader) {
        try {
            // Decode the Basic Auth header
            String[] credentials = decodeBasicAuth(authHeader);
            if (credentials == null || credentials.length != 2) {
                return ResponseEntity.status(401).body(new Result(
                        false, "Unauthorized", new ArrayList<>()));
            }


            String username = credentials[0];
            String password = credentials[1];
            System.out.println(username + ": " + password);

            // Check if the user already exists
            if (findByUsername(username)) {
                return ResponseEntity.status(401).body(new Result(
                        false, "User already exists", new ArrayList<>()));
            }

            // Create a new user
            AppUser newUser = new AppUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            availUsers.add(newUser);

            return ResponseEntity.ok(new Result(
                    true, "Publisher registered successfully", new ArrayList<>()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Result(
                    false, "Internal Server Error: " + e.getMessage(), new ArrayList<>()));
        }
    }

    // Login a user
    @GetMapping("/login")
    public ResponseEntity<Result> login(@RequestHeader("Authorization") String authHeader) {
        try {
            // Decode the Basic Auth header
            String[] credentials = decodeBasicAuth(authHeader);
            if (credentials == null || credentials.length != 2) {
                return ResponseEntity.status(401).body(new Result(
                        false, "Unauthorized", new ArrayList<>()));
            }


            String username = credentials[0];
            String password = credentials[1];
            System.out.println(username + ": " + password);

            // Check if the user already exists
            if (existingUser(username, password)) {
                return ResponseEntity.ok(new Result(
                        true, "Publisher logged in successfully", new ArrayList<>()));
            }
            else {
                return ResponseEntity.status(401).body(new Result(
                        false, "Wrong username or password", new ArrayList<>()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Result(
                    false, "Internal Server Error: " + e.getMessage(), new ArrayList<>()));
        }
    }

    private boolean existingUser(String username, String password) {
        for (IAppUser user : availUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    // Method to decode the Basic Auth header
    private String[] decodeBasicAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(authHeader.substring(6));
        String decodedString = new String(decodedBytes);
        return decodedString.split(":", 2);
    }

    // Method to find a user by username
    private boolean findByUsername(String username) {
        for (IAppUser user : availUsers) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
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
