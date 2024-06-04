package org.example;

import org.example.controller.UserController;
import org.example.model.*;

import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * REST API server for managing publishers, sheets, and related operations.
 */
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
    @GetMapping("/getPublishers")
    public ResponseEntity<?> getPublishers(@RequestHeader("Authorization") String authHeader) {
        try {
            // Decode the Basic Auth header
            String[] credentials = decodeBasicAuth(authHeader);
            if (credentials == null || credentials.length != 2) {
                return ResponseEntity.status(401).body(new Result(
                        false, "Unauthorized", new ArrayList<>()));
            }

            List<Argument> listOfArgument = new ArrayList<>();

            for (IAppUser user : availUsers) {
                if (!user.getUsername().equals(credentials[0])) {
                    listOfArgument.add(new Argument(user.getUsername(), null, null, null));
                }
            }

            return ResponseEntity.ok(new Result(
                    true, null, listOfArgument));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Result(
                    false, "Internal Server Error: " + e.getMessage(), new ArrayList<>()));
        }
    }

    /**
     * Creates a new sheet for a specified publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the sheet details.
     * @return a ResponseEntity containing the result of the sheet creation.
     */
    @PostMapping("/createSheet")
    public ResponseEntity<Result> createSheet(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody Argument argument) {
        try {
            // Decode the Basic Auth header
            String[] credentials = decodeBasicAuth(authHeader);
            if (credentials == null || credentials.length != 2 || !existingUser(credentials[0], credentials[1])) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                        false, "Unauthorized", new ArrayList<>()));
            }

            String username = credentials[0];
            String publisher = argument.getPublisher();
            String sheet = argument.getSheet();

            System.out.println("Username: " + username);
            System.out.println("Publisher: " + publisher);
            System.out.println("Sheet: " + sheet);

            if (!publisher.equals(username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                        false, "Unauthorized: sender is not owner of sheet", new ArrayList<>()));
            } else if (sheet.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result(
                        false, "Sheet name cannot be blank", new ArrayList<>()));
            } else if (hasSheet(sheet, publisher)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Result(
                        false, "Sheet already exists: " + sheet, new ArrayList<>()));
            } else {
                IAppUser user = findUser(username);
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                            false, "User not found", new ArrayList<>()));
                }
                user.addSheet(sheet);
                return ResponseEntity.status(HttpStatus.CREATED).body(new Result(
                        true, "Sheet created successfully", new ArrayList<>()));
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Result(
                    false, "Internal Server Error: " + e.getMessage(), new ArrayList<>()));
        }
    }


    /**
     * Retrieves all sheets for a specified publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the publisher details.
     * @return a ResponseEntity containing the result of the sheets retrieval.
     */
    @PostMapping("/getSheets")
    public ResponseEntity<Result> getSheets(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody Argument argument) {
        try {
            // Decode the Basic Auth header
            String[] credentials = decodeBasicAuth(authHeader);
            if (credentials == null || credentials.length != 2 || !existingUser(credentials[0], credentials[1])) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                        false, "Unauthorized", new ArrayList<>()));
            }

            List<Argument> sheets = new ArrayList<>();;

            String publisher = argument.getPublisher();

            IAppUser user = findUser(publisher);


            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                        false, "User not found", new ArrayList<>()));
            }

            for(ISpreadsheet sheet : user.getSheets()){
                sheets.add(new Argument(publisher, sheet.getName(), null, null));
            }

            return ResponseEntity.ok(new Result(true, "Sheets retrieved successfully", sheets));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Updates a published sheet for a specified publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the sheet details.
     * @return a ResponseEntity containing the result of the sheet update.
     */
    @PostMapping("/updatePublished")
    public ResponseEntity<Result> updatePublished(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody Argument argument) {
        try {
            // Decode the Basic Auth header
            String[] credentials = decodeBasicAuth(authHeader);
            if (credentials == null || credentials.length != 2 || !existingUser(credentials[0], credentials[1])) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                        false, "Unauthorized", new ArrayList<>()));
            }
            String publisher = argument.getPublisher();
            String sheet = argument.getSheet();
            String payload = argument.getPayload();
    
            System.out.println("Publisher: " + publisher);
            System.out.println("Sheet: " + sheet);
            System.out.println("Payload: " + payload);
    
            IAppUser user = findUser(publisher);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                        false, "User not found", new ArrayList<>()));
            }
    
            for (ISpreadsheet existingSheet : user.getSheets()) {
                if (existingSheet.getName().equals(sheet)) {
                    List<List<String>> data = Home.convertStringTo2DArray(payload);
                    for (List<String> ls : data) {
                        System.out.println("Row: " + ls.get(0) + " Col: " + ls.get(1) + " Value: " + ls.get(2));
                        existingSheet.setCellRawdata(Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)), ls.get(2));
                        existingSheet.setCellValue(Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)), ls.get(2));
                    }

                    ISpreadsheet update = new Spreadsheet(existingSheet.getName());

                    //Copy contents of sheet
                    ArrayList<ArrayList<Cell>> copy = new ArrayList<>();
                    ArrayList<ArrayList<Cell>> grid = existingSheet.getCells();

                    for (int i = 0; i < grid.size(); i++){
                        for(int j = 0; j < grid.get(i).size(); j++){
                            update.setCellValue(i, j, grid.get(i).get(j).getValue());
                            update.setCellRawdata(i, j, grid.get(i).get(j).getRawdata());
                        }
                    }
                    existingSheet.addPublished(update);
    
                    return ResponseEntity.ok(new Result(true, "Sheet updated successfully", new ArrayList<>()));
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                    false, "Sheet not found", new ArrayList<>()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Result(
                    false, "Internal Server Error: " + e.getMessage(), new ArrayList<>()));
        }
    }
    
    /**
     * Registers a new publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @return a ResponseEntity containing the result of the registration.
     */
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

    /**
     * Logs in a user.
     *
     * @param authHeader the authorization header containing the credentials.
     * @return a ResponseEntity containing the result of the login.
     */
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


    /**
     * Retrieves updates for a subscription.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the subscription details.
     * @return a ResponseEntity containing the result of the updates retrieval.
     */
    @PostMapping("/getUpdatesForSubscription")
    public ResponseEntity<?> getUpdatesForSubscription(@RequestHeader("Authorization") String authHeader,
                                                       @RequestBody Argument argument) {
        try {
            // Decode the Basic Auth header
            String[] credentials = decodeBasicAuth(authHeader);
            if (credentials == null || credentials.length != 2) {
                System.out.println("Unauthorized: Invalid credentials");
                return ResponseEntity.status(401).body(new Result(
                        false, "Unauthorized", new ArrayList<>()));
            }
            String publisher = argument.getPublisher();
            String sheet = argument.getSheet();
            String id = argument.getId();
            System.out.println("User: " + publisher + ", Sheet Name: " + sheet + ", ID: " + id);
            IAppUser user = findUser(publisher);

            if (user == null) {
                System.out.println("User not found: " + publisher);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                        false, "User not found", new ArrayList<>()));
            }

            List<Argument> arguments = new ArrayList<>();
            for (ISpreadsheet existingSheet : user.getSheets()) {
                if (existingSheet.getName().equals(sheet)) {
                    List<ISpreadsheet> versions = existingSheet.getPublishedVersions();
                    System.out.println("Found sheet: " + sheet + ", Versions: " + versions.size());
                    for (int i = Integer.parseInt(id); i < versions.size(); i++) {
                        String payload = UserController.convertSheetToPayload(versions.get(i));
                        System.out.println("Payload for version " + i + ": " + payload);
                        Argument arg = new Argument(publisher, sheet, String.valueOf(i), payload);
                        arguments.add(arg);
                    }
                    System.out.println("Returning updates: " + arguments.size());
                    return ResponseEntity.ok(new Result(true, "Updates received", arguments));
                }
            }

            System.out.println("Sheet not found: " + sheet);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                    false, "Sheet not found", new ArrayList<>()));

        } catch (Exception e) {
            System.out.println("Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(new Result(false, "Internal Server Error: " + e.getMessage(), new ArrayList<>()));
        }
    }
    
    /**
     * Retrieves updates for published sheets.
     *
     * @param sheetDTO the sheet details.
     * @return a ResponseEntity containing the result of the updates retrieval.
     */
    @PostMapping("/getUpdatedForPublished")
    public ResponseEntity<?> getUpdatedForPublished(@RequestBody SheetDTO sheetDTO) {
        try {
            // Your logic to get updates for published here
            return ResponseEntity.ok("Updates for published retrieved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Updates a subscription.
     *
     * @param sheetDTO the sheet details.
     * @return a ResponseEntity containing the result of the subscription update.
     */
    @PostMapping("/updateSubscription")
    public ResponseEntity<?> updateSubscription(@RequestBody SheetDTO sheetDTO) {
        try {
            // Your logic to update subscription here
            return ResponseEntity.ok("Subscription updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Checks if a sheet exists for a publisher.
     *
     * @param sheet     the sheet name.
     * @param publisher the publisher name.
     * @return true if the sheet exists, false otherwise.
     */
    private boolean hasSheet(String sheet, String publisher) {
        for (IAppUser user : availUsers) {
            if (user.getUsername().equals(publisher) && user.doesSheetExist(sheet)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Finds a user by username.
     *
     * @param username the username.
     * @return the user if found, null otherwise.
     */
    private IAppUser findUser(String username) {
        for (IAppUser user : this.availUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }



}
