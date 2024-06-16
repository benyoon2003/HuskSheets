package org.example.server;

import org.example.model.*;

import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistent REST API Server for handling requests from HuskSheet application
 * and storing all user data.
 */
@RestController
@RequestMapping("/api/v1")
public class Server {

    //List of all available users
    List<IAppUser> availUsers = new ArrayList<>();

    /**
     * Decodes the basic authentication and returns a String array of the credentials.
     *
     * @param authHeader the authentication header
     * @return a 1D String array of credentials
     * @author Tony
     */
    private static String[] decodeBasicAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null; // Return null if the authHeader is null or does not start with "Basic "
        }
        byte[] decodedBytes = Base64.getDecoder().decode(authHeader.substring(6)); // Decode the Base64 encoded credentials
        String decodedString = new String(decodedBytes); // Convert the decoded bytes to a string
        return decodedString.split(":", 2); // Split the decoded string into username and password
    }

    /**
     * Makes sure that the given credentials are not empty.
     *
     * @param credentials the String array of credentials
     * @author Tony
     */
    private void validateCredentials(String[] credentials) {
        if (credentials == null || credentials.length != 2
                || credentials[0].isEmpty() || credentials[1].isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");  // Throw an exception if credentials are invalid
        }
    }

    /**
     * Checks if a sheet exists for a publisher.
     *
     * @param sheet     the sheet name.
     * @param publisher the publisher name.
     * @return true if the sheet exists, false otherwise.
     * @author Tony
     */
    private boolean hasSheet(String sheet, String publisher) {
        for (IAppUser user : availUsers) { // Iterate through all available users
            if (user.getUsername().equals(publisher) && user.doesSheetExist(sheet)) {
                return true; // Return true if the user exists and the sheet exists for the publisher
            }
        }
        return false; // Return false if the sheet does not exist for the publisher
    }

    /**
     * Finds an IAppUser by username.
     *
     * @param username the username
     * @return the user if found, null otherwise
     * @author Ben
     */
    private IAppUser findUser(String username) {
        for (IAppUser user : this.availUsers) { // Iterate through all available users
            if (user.getUsername().equals(username)) {
                return user; // Return the user if the username matches
            }
        }
        return null; // Return null if no user is found with the given username
    }

    /**
     * Checks if the given username and password belong to an existing user.
     *
     * @param username the username
     * @param password the password
     * @return true if the user exists already and false otherwise
     * @author Ben
     */
    public boolean existingUser(String username, String password) {
        for (IAppUser user : availUsers) { // Iterate through all available users
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true; // Return true if the username and password match an existing user
            }
        }
        return false; // Return false if no matching user is found
    }

    /**
     * Determines if there exists a user with the given username.
     *
     * @param username a String
     * @return true if there exists a user with the given username and false otherwise.
     * @author Tony
     */
    private boolean findByUsername(String username) {
        for (IAppUser user : availUsers) { // Iterate through all available users
            if (user.getUsername().equals(username)) {
                return true; // Return true if the username matches an existing user
            }
        }
        return false; // Return false if no matching user is found
    }


    /**
     * Gets a list of publishers currently stored in the server.
     *
     * @param authHeader basic authentication header
     * @return a ResponseEntity containing a Result
     * @author Ben
     */
    @GetMapping("/getPublishers")
    public ResponseEntity<?> getPublishers(@RequestHeader("Authorization") String authHeader) {
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(401).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        String username = credentials[0]; // Get the username from credentials
        IAppUser user = findUser(username); // Find the user by username
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "User not found", new ArrayList<>())); // Return 404 status if user is not found
        }
        List<Argument> listOfArgument = new ArrayList<>();
        for (IAppUser appUser : availUsers) { // Iterate through all available users
            listOfArgument.add(new Argument(appUser.getUsername(), null, null, null)); // Add each user to the list of arguments
        }
        return ResponseEntity.ok(new Result(
                true, null, listOfArgument)); // Return 200 status with the list of publishers
    }

    /**
     * Creates a new sheet for a specified publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the publisher and sheet name
     * @return a ResponseEntity containing the result of the sheet creation.
     * @author Ben
     */
    @PostMapping("/createSheet")
    public ResponseEntity<Result> createSheet(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody Argument argument) {
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.status(401).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        String username = credentials[0]; // Get the username from credentials
        String publisher = argument.getPublisher(); // Get the publisher from the argument
        String sheet = argument.getSheet(); // Get the sheet name from the argument
        if (!publisher.equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "Unauthorized: sender is not owner of sheet", new ArrayList<>())); // Return 401 status if sender is not the owner of the sheet
        } else if (sheet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "Sheet name cannot be blank", new ArrayList<>())); // Return 400 status if sheet name is blank
        } else if (hasSheet(sheet, publisher)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "Sheet already exists: " + sheet, new ArrayList<>())); // Return 409 status if sheet already exists
        } else {
            IAppUser user = findUser(username); // Find the user by username
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                        false, "User not found", new ArrayList<>())); // Return 404 status if user is not found
            }
            user.addSheet(sheet); // Add the sheet to the user
            return ResponseEntity.ok(new Result(
                    true, "Sheet created successfully", new ArrayList<>())); // Return 201 status if sheet is created successfully
        }
    }

    /**
     * Deletes the specified sheet (from Argument) from the server's memory.
     *
     * @param authHeader basic authentication header
     * @param argument   an Argument containing the publisher and sheet name
     * @return a ResponseEntity containing the result of the sheet creation.
     * @author Ben
     */
    @PostMapping("/deleteSheet") // Mapping for POST requests to /deleteSheet
    public ResponseEntity<Result> deleteSheet(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody Argument argument) {
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        String username = credentials[0]; // Get the username from credentials
        String publisher = argument.getPublisher(); // Get the publisher from the argument
        String sheet = argument.getSheet(); // Get the sheet name from the argument
        IAppUser user = findUser(username); // Find the user by username
        if (!publisher.equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "Unauthorized: sender is not owner of sheet", new ArrayList<>())); // Return 401 status if sender is not the owner of the sheet
        } else if (sheet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "Sheet name cannot be blank", new ArrayList<>())); // Return 400 status if sheet name is blank
        } else if (!user.doesSheetExist(sheet)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "Sheet does not exist: " + sheet, new ArrayList<>())); // Return 400 status if sheet does not exist
        } else {
            user.removeSheet(sheet);
            return ResponseEntity.ok(new Result(
                    true, "Sheet deleted successfully", new ArrayList<>())); // Return 202 status if sheet is deleted successfully
        }
    }

    /**
     * Retrieves all sheets for a specified publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the publisher
     * @return a ResponseEntity containing the result of the sheets retrieval.
     * @author Tony
     */
    @PostMapping("/getSheets") // Mapping for POST requests to /getSheets
    public ResponseEntity<Result> getSheets(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody Argument argument) {
        // Decode the Basic Auth header
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        List<Argument> sheets = new ArrayList<>(); // Initialize the list of sheets
        String publisher = argument.getPublisher(); // Get the publisher from the argument
        IAppUser user = findUser(publisher); // Find the user by publisher name
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "User not found", new ArrayList<>())); // Return 404 status if user is not found
        }
        for (ISpreadsheet sheet : user.getSheets()) { // Iterate through the user's sheets
            sheets.add(new Argument(publisher, sheet.getName(), null, null)); // Add each sheet to the list of arguments
        }
        return ResponseEntity.ok(new Result(true, "Sheets retrieved successfully", sheets)); // Return 200 status with the list of sheets
    }

    /**
     * Updates a published sheet for a specified publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the publisher, sheet, and payload
     * @return a ResponseEntity containing the result of the sheet update.
     * @author Tony
     */
    @PostMapping("/updatePublished") // Mapping for POST requests to /updatePublished
    public ResponseEntity<Result> updatePublished(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody Argument argument) {
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        String publisher = argument.getPublisher(); // Get the publisher from the argument
        String sheet = argument.getSheet(); // Get the sheet name from the argument
        String payload = argument.getPayload(); // Get the payload from the argument
        IAppUser user = findUser(publisher); // Find the user by publisher name
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "User not found", new ArrayList<>())); // Return 404 status if user is not found
        }
        // New updated sheet must not mutate old version and must be initialized with empty grid
        // to account for empty spaces or deletions in the update
        for (ISpreadsheet existingSheet : user.getSheets()) { // Iterate through the user's sheets
            if (existingSheet.getName().equals(sheet)) {
                List<List<String>> data = Home.convertStringTo2DArray(payload); // Convert the payload to a 2D array
                List<List<Cell>> updatedGrid = initializeEmptyGrid(existingSheet.getRows(), existingSheet.getCols()); // Initialize an empty grid
                populateUpdatedGrid(updatedGrid, data); // Populate the updated grid with the data
                existingSheet.setGrid(updatedGrid); // Set the updated grid in the existing sheet
                ISpreadsheet updatedVersion = createUpdatedVersion(existingSheet); // Create an updated version of the sheet
                existingSheet.addPublished(updatedVersion);  // Add the updated version to the list of published versions
                return ResponseEntity.ok(new Result(true, "Sheet updated successfully", new ArrayList<>())); // Return 200 status if sheet is updated successfully
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                false, "Sheet not found", new ArrayList<>())); // Return 404 status if sheet is not found
    }


    /**
     * Initializes an empty grid of cells.
     *
     * @param rows number of rows
     * @param cols number of columns
     * @return a 2D list of Cell
     * @author Ben
     */
    private List<List<Cell>> initializeEmptyGrid(int rows, int cols) {
        List<List<Cell>> grid = new ArrayList<>(); // Initialize the grid
        for (int i = 0; i < rows; i++) {
            ArrayList<Cell> row = new ArrayList<>(); // Initialize a new row
            for (int j = 0; j < cols; j++) {
                row.add(new Cell("")); // Add a new empty cell to the row
            }
            grid.add(row); // Add the row to the grid
        }
        return grid; // Return the grid
    }

    /**
     * Updates the copy grid with the original data.
     *
     * @param grid new grid
     * @param data previous grid
     * @author Ben
     */
    private void populateUpdatedGrid(List<List<Cell>> grid, List<List<String>> data) {
        for (List<String> ls : data) { // Iterate through the data
            int row = Integer.parseInt(ls.get(0)); // Get the row index from the data
            int col = Integer.parseInt(ls.get(1)); // Get the column index from the data
            String value = ls.get(2); // Get the cell value from the data
            grid.get(row).get(col).setValue(value); // Set the cell value in the grid
            grid.get(row).get(col).setRawData(value); // Set the raw data in the grid
        }
    }

    /**
     * Creates a new copy of the updated version to be saved in the list of versions.
     *
     * @param existingSheet the existing ISpreadsheet
     * @return a new ISpreadSheet
     * @author Ben
     */
    private ISpreadsheet createUpdatedVersion(ISpreadsheet existingSheet) {
        ISpreadsheet updatedVersion = new Spreadsheet(existingSheet.getName()); // Create a new spreadsheet with the same name
        List<List<Cell>> grid = existingSheet.getCells(); // Get the grid from the existing sheet
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                updatedVersion.setCellValue(i, j, grid.get(i).get(j).getValue()); // Set the cell value in the updated version
                updatedVersion.setCellRawdata(i, j, grid.get(i).get(j).getRawdata()); // Set the raw data in the updated version
            }
        }
        return updatedVersion; // Return the updated version
    }

    /**
     * Updates the published sheet with changes made by the subscriber.
     *
     * @param authHeader a basic authentication header
     * @param argument   an Argument containing publisher, sheet name, and payload
     * @return a ResponseEntity containing the Result of the update
     * @author Ben
     */
    @PostMapping("/updateSubscription")
    public ResponseEntity<Result> updateSubscription(@RequestHeader("Authorization") String authHeader,
                                                     @RequestBody Argument argument) {
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        String publisher = argument.getPublisher(); // Get the publisher from the argument
        String sheet = argument.getSheet(); // Get the sheet name from the argument
        String payload = argument.getPayload(); // Get the payload from the argument
        IAppUser user = findUser(publisher); // Find the user by publisher name
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "User not found", new ArrayList<>())); // Return 404 status if user is not found
        }
        // New updated sheet must not mutate old version and must be initialized with empty grid
        // to account for empty spaces or deletions in the update
        for (ISpreadsheet existingSheet : user.getSheets()) { // Iterate through the user's sheets
            if (existingSheet.getName().equals(sheet)) {
                List<List<String>> data = Home.convertStringTo2DArray(payload); // Convert the payload to a 2D array
                List<List<Cell>> updatedGrid = initializeEmptyGrid(existingSheet.getRows(), existingSheet.getCols()); // Initialize an empty grid
                populateUpdatedGrid(updatedGrid, data); // Populate the updated grid with the data
                existingSheet.setGrid(updatedGrid); // Set the updated grid in the existing sheet
                ISpreadsheet updatedVersion = createUpdatedVersion(existingSheet); // Create an updated version of the sheet
                existingSheet.addSubscribed(updatedVersion); // Add the updated version to the list of subscribed versions
                return ResponseEntity.ok(new Result(true, "Sheet updated successfully", new ArrayList<>())); // Return 200 status if sheet is updated successfully
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                false, "Sheet not found", new ArrayList<>())); // Return 404 status if sheet is not found
    }

    /**
     * Registers a new publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @return a ResponseEntity containing the result of the registration.
     * @author Tony
     */
    @GetMapping("/register") // Mapping for GET requests to /register
    public ResponseEntity<Result> register(@RequestHeader("Authorization") String authHeader) {
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        String username = credentials[0]; // Get the username from credentials
        String password = credentials[1]; // Get the password from credentials
        if (findByUsername(username)) {
            return ResponseEntity.ok(new Result(
                    true, "Publisher registered successfully", new ArrayList<>())); // Return 200 status if user is registered successfully
        }
        AppUser newUser = new AppUser(username, password); // Create a new user
        availUsers.add(newUser); // Add the new user to the list of available users
        return ResponseEntity.ok(new Result(
                true, "Publisher registered successfully", new ArrayList<>())); // Return 200 status if user is registered successfully
    }

    /**
     * Logs in a user.
     *
     * @param authHeader the authorization header containing the credentials.
     * @return a ResponseEntity containing the result of the login.
     * @author Ben
     */
    @GetMapping("/login")
    public ResponseEntity<Result> login(@RequestHeader("Authorization") String authHeader) {
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        String username = credentials[0]; // Get the username from credentials
        String password = credentials[1]; // Get the password from credentials
        if (existingUser(username, password)) {
            return ResponseEntity.ok(new Result(
                    true, "Publisher logged in successfully", new ArrayList<>())); // Return 200 status if user is logged in successfully
        } else {
            return ResponseEntity.status(401).body(new Result(
                    false, "Wrong username or password", new ArrayList<>())); // Return 401 status if username or password is incorrect
        }
    }

    /**
     * Retrieves all updates after the given id for the specific publisher and sheet
     * for a subscriber.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the publisher, sheet name and id
     * @return a ResponseEntity containing the result of the updates retrieval.
     * @author Tony
     */
    @PostMapping("/getUpdatesForSubscription") // Mapping for POST requests to /getUpdatesForSubscription
    public ResponseEntity<?> getUpdatesForSubscription(@RequestHeader("Authorization") String authHeader,
                                                       @RequestBody Argument argument) {
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        String publisher = argument.getPublisher(); // Get the publisher from the argument
        String sheet = argument.getSheet(); // Get the sheet name from the argument
        String id = argument.getId(); // Get the id from the argument
        IAppUser user = findUser(publisher); // Find the user by publisher name
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "User not found", new ArrayList<>()));  // Return 404 status if user is not found
        }
        List<Argument> arguments = new ArrayList<>(); // Initialize the list of arguments
        for (ISpreadsheet existingSheet : user.getSheets()) { // Iterate through the user's sheets
            if (existingSheet.getName().equals(sheet)) {
                List<ISpreadsheet> versions = existingSheet.getPublishedVersions(); // Get the list of published versions
                for (int i = Integer.parseInt(id); i < versions.size(); i++) { // Iterate through the versions starting from the given id
                    String payload = Spreadsheet.convertSheetToPayload(versions.get(i)); // Convert the sheet to a payload
                    Argument arg = new Argument(publisher, sheet, String.valueOf(i), payload); // Create a new argument with the payload
                    arguments.add(arg); // Add the argument to the list
                }
                return ResponseEntity.ok(new Result(true, "Updates received", arguments)); // Return 200 status with the list of updates
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                false, "Sheet not found", new ArrayList<>()));  // Return 404 status if sheet is not found
    }

    /**
     * Retrieves all updates after the given id for the specific publisher and sheet
     * for a publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the subscription details.
     * @return a ResponseEntity containing the result of the updates retrieval.
     * @author Tony
     */
    @PostMapping("/getUpdatesForPublished") // Mapping for POST requests to /getUpdatesForPublished
    public ResponseEntity<?> getUpdatesForPublished(@RequestHeader("Authorization") String authHeader,
                                                    @RequestBody Argument argument) {
        String[] credentials = decodeBasicAuth(authHeader); // Decode the Basic Auth header
        try {
            validateCredentials(credentials); // Validate the credentials
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>())); // Return 401 status if credentials are invalid
        }
        String publisher = argument.getPublisher(); // Get the publisher from the argument
        String sheet = argument.getSheet(); // Get the sheet name from the argument
        String id = argument.getId();  // Get the id from the argument
        IAppUser user = findUser(publisher); // Find the user by publisher name
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "User not found", new ArrayList<>())); // Return 404 status if user is not found
        }
        List<Argument> arguments = new ArrayList<>(); // Initialize the list of arguments
        for (ISpreadsheet existingSheet : user.getSheets()) { // Iterate through the user's sheets
            if (existingSheet.getName().equals(sheet)) {
                List<ISpreadsheet> versions = existingSheet.getSubscribedVersions(); // Get the list of subscribed versions
                for (int i = Integer.parseInt(id); i < versions.size(); i++) { // Iterate through the versions starting from the given id
                    String payload = Spreadsheet.convertSheetToPayload(versions.get(i)); // Convert the sheet to a payload
                    Argument arg = new Argument(publisher, sheet, String.valueOf(i), payload); // Create a new argument with the payload
                    arguments.add(arg); // Add the argument to the list
                }
                return ResponseEntity.ok(new Result(true, "Updates received", arguments)); // Return 200 status with the list of updates

            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                false, "Sheet not found", new ArrayList<>())); // Return 404 status if sheet is not found
    }
}
