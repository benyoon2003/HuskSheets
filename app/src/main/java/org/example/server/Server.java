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
    private List<IAppUser> availUsers = new ArrayList<>();

    /**
     * Decodes the basic authentication and returns a String array of the credentials.
     *
     * @param authHeader the authentication header
     * @return a 1D String array of credentials
     * @author Tony
     */
    private static String[] decodeBasicAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(authHeader.substring(6));
        String decodedString = new String(decodedBytes);
        return decodedString.split(":", 2);
    }

    /**
     * Makes sure that the given credentials are not empty.
     *
     * @param credentials the String array of credentials
     * @author Tony
     */
    private void validateCredentials(String[] credentials) {
        if (credentials == null || credentials.length != 2) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
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
        for (IAppUser user : availUsers) {
            if (user.getUsername().equals(publisher) && user.doesSheetExist(sheet)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds an IAppUser by username.
     *
     * @param username the username
     * @return the user if found, null otherwise
     * @author Ben
     */
    private IAppUser findUser(String username) {
        for (IAppUser user : this.availUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
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
        for (IAppUser user : availUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if there exists a user with the given username.
     *
     * @param username a String
     * @return true if there exists a user with the given username and false otherwise.
     * @author Tony
     */
    private boolean findByUsername(String username) {
        for (IAppUser user : availUsers) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
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
        try {
            String[] credentials = decodeBasicAuth(authHeader);
            validateCredentials(credentials);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(401).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        List<Argument> listOfArgument = new ArrayList<>();
        for (IAppUser user : availUsers) {
            listOfArgument.add(new Argument(user.getUsername(), null, null, null));
        }
        return ResponseEntity.ok(new Result(
                true, null, listOfArgument));
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
        String[] credentials = decodeBasicAuth(authHeader);
        try {
            validateCredentials(credentials);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.status(401).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        String username = credentials[0];
        String publisher = argument.getPublisher();
        String sheet = argument.getSheet();
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
    }

    /**
     * Deletes the specified sheet (from Argument) from the server's memory.
     *
     * @param authHeader basic authentication header
     * @param argument   an Argument containing the publisher and sheet name
     * @return a ResponseEntity containing the result of the sheet creation.
     * @author Ben
     */
    @PostMapping("/deleteSheet")
    public ResponseEntity<Result> deleteSheet(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody Argument argument) {
        String[] credentials = decodeBasicAuth(authHeader);
        try {
            validateCredentials(credentials);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        String username = credentials[0];
        String publisher = argument.getPublisher();
        String sheet = argument.getSheet();
        IAppUser user = findUser(username);
        if (!publisher.equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, "Unauthorized: sender is not owner of sheet", new ArrayList<>()));
        } else if (sheet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result(
                    false, "Sheet name cannot be blank", new ArrayList<>()));
        } else if (!user.doesSheetExist(sheet)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result(
                    false, "Sheet does not exist: " + sheet, new ArrayList<>()));
        } else {
            user.removeSheet(sheet);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Result(
                    true, "Sheet deleted successfully", new ArrayList<>()));
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
    @PostMapping("/getSheets")
    public ResponseEntity<Result> getSheets(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody Argument argument) {
        // Decode the Basic Auth header
        String[] credentials = decodeBasicAuth(authHeader);
        try {
            validateCredentials(credentials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        List<Argument> sheets = new ArrayList<>();
        ;
        String publisher = argument.getPublisher();
        IAppUser user = findUser(publisher);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                    false, "User not found", new ArrayList<>()));
        }
        for (ISpreadsheet sheet : user.getSheets()) {
            sheets.add(new Argument(publisher, sheet.getName(), null, null));
        }
        return ResponseEntity.ok(new Result(true, "Sheets retrieved successfully", sheets));
    }

    /**
     * Updates a published sheet for a specified publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @param argument   the argument containing the publisher, sheet, and payload
     * @return a ResponseEntity containing the result of the sheet update.
     * @author Tony
     */
    @PostMapping("/updatePublished")
    public ResponseEntity<Result> updatePublished(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody Argument argument) {
        String[] credentials = decodeBasicAuth(authHeader);
        try {
            validateCredentials(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        String publisher = argument.getPublisher();
        String sheet = argument.getSheet();
        String payload = argument.getPayload();
        IAppUser user = findUser(publisher);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                    false, "User not found", new ArrayList<>()));
        }
        for (ISpreadsheet existingSheet : user.getSheets()) {
            if (existingSheet.getName().equals(sheet)) {
                List<List<String>> data = Home.convertStringTo2DArray(payload);
                List<List<Cell>> updatedGrid = initializeEmptyGrid(existingSheet.getRows(), existingSheet.getCols());
                populateUpdatedGrid(updatedGrid, data);
                existingSheet.setGrid(updatedGrid);
                ISpreadsheet updatedVersion = createUpdatedVersion(existingSheet);
                existingSheet.addPublished(updatedVersion);
                return ResponseEntity.ok(new Result(true, "Sheet updated successfully", new ArrayList<>()));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                false, "Sheet not found", new ArrayList<>()));
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
        List<List<Cell>> grid = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add(new Cell(""));
            }
            grid.add(row);
        }
        return grid;
    }

    /**
     * Updates the copy grid with the original data.
     *
     * @param grid new grid
     * @param data previous grid
     * @author Ben
     */
    private void populateUpdatedGrid(List<List<Cell>> grid, List<List<String>> data) {
        for (List<String> ls : data) {
            int row = Integer.parseInt(ls.get(0));
            int col = Integer.parseInt(ls.get(1));
            String value = ls.get(2);
            grid.get(row).get(col).setValue(value);
            grid.get(row).get(col).setRawData(value);
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
        ISpreadsheet updatedVersion = new Spreadsheet(existingSheet.getName());
        List<List<Cell>> grid = existingSheet.getCells();
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                updatedVersion.setCellValue(i, j, grid.get(i).get(j).getValue());
                updatedVersion.setCellRawdata(i, j, grid.get(i).get(j).getRawdata());
            }
        }
        return updatedVersion;
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
        // Decode the Basic Auth header
        String[] credentials = decodeBasicAuth(authHeader);
        try {
            validateCredentials(credentials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        String publisher = argument.getPublisher();
        String sheet = argument.getSheet();
        String payload = argument.getPayload();
        IAppUser user = findUser(publisher);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                    false, "User not found", new ArrayList<>()));
        }
        for (ISpreadsheet existingSheet : user.getSheets()) {
            if (existingSheet.getName().equals(sheet)) {
                List<List<String>> data = Home.convertStringTo2DArray(payload);
                List<List<Cell>> updatedGrid = initializeEmptyGrid(existingSheet.getRows(), existingSheet.getCols());
                populateUpdatedGrid(updatedGrid, data);
                existingSheet.setGrid(updatedGrid);
                ISpreadsheet updatedVersion = createUpdatedVersion(existingSheet);
                existingSheet.addSubscribed(updatedVersion);
                return ResponseEntity.ok(new Result(true, "Sheet updated successfully", new ArrayList<>()));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                false, "Sheet not found", new ArrayList<>()));
    }

    /**
     * Registers a new publisher.
     *
     * @param authHeader the authorization header containing the credentials.
     * @return a ResponseEntity containing the result of the registration.
     * @author Tony
     */
    @GetMapping("/register")
    public ResponseEntity<Result> register(@RequestHeader("Authorization") String authHeader) {
        String[] credentials = decodeBasicAuth(authHeader);
        try {
            validateCredentials(credentials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        String username = credentials[0];
        String password = credentials[1];
        if (findByUsername(username)) {
            return ResponseEntity.status(401).body(new Result(
                    false, "User already exists", new ArrayList<>()));
        }
        AppUser newUser = new AppUser(username, password);
        availUsers.add(newUser);
        return ResponseEntity.ok(new Result(
                true, "Publisher registered successfully", new ArrayList<>()));
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
        String[] credentials = decodeBasicAuth(authHeader);
        try {
            validateCredentials(credentials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        String username = credentials[0];
        String password = credentials[1];
        if (existingUser(username, password)) {
            return ResponseEntity.ok(new Result(
                    true, "Publisher logged in successfully", new ArrayList<>()));
        } else {
            return ResponseEntity.status(401).body(new Result(
                    false, "Wrong username or password", new ArrayList<>()));
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
    @PostMapping("/getUpdatesForSubscription")
    public ResponseEntity<?> getUpdatesForSubscription(@RequestHeader("Authorization") String authHeader,
                                                       @RequestBody Argument argument) {
        String[] credentials = decodeBasicAuth(authHeader);
        try {
            validateCredentials(credentials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        String publisher = argument.getPublisher();
        String sheet = argument.getSheet();
        String id = argument.getId();
        IAppUser user = findUser(publisher);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                    false, "User not found", new ArrayList<>()));
        }
        List<Argument> arguments = new ArrayList<>();
        for (ISpreadsheet existingSheet : user.getSheets()) {
            if (existingSheet.getName().equals(sheet)) {
                List<ISpreadsheet> versions = existingSheet.getPublishedVersions();
                for (int i = Integer.parseInt(id); i < versions.size(); i++) {
                    String payload = Spreadsheet.convertSheetToPayload(versions.get(i));
                    Argument arg = new Argument(publisher, sheet, String.valueOf(i), payload);
                    arguments.add(arg);
                }
                return ResponseEntity.ok(new Result(true, "Updates received", arguments));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                false, "Sheet not found", new ArrayList<>()));
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
    @PostMapping("/getUpdatesForPublished")
    public ResponseEntity<?> getUpdatesForPublished(@RequestHeader("Authorization") String authHeader,
                                                    @RequestBody Argument argument) {
        // Decode the Basic Auth header
        String[] credentials = decodeBasicAuth(authHeader);
        try {
            validateCredentials(credentials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Result(
                    false, e.getMessage(), new ArrayList<>()));
        }
        String publisher = argument.getPublisher();
        String sheet = argument.getSheet();
        String id = argument.getId();
        IAppUser user = findUser(publisher);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                    false, "User not found", new ArrayList<>()));
        }
        List<Argument> arguments = new ArrayList<>();
        for (ISpreadsheet existingSheet : user.getSheets()) {
            if (existingSheet.getName().equals(sheet)) {
                List<ISpreadsheet> versions = existingSheet.getSubscribedVersions();
                for (int i = Integer.parseInt(id); i < versions.size(); i++) {
                    String payload = Spreadsheet.convertSheetToPayload(versions.get(i));
                    Argument arg = new Argument(publisher, sheet, String.valueOf(i), payload);
                    arguments.add(arg);
                }
                return ResponseEntity.ok(new Result(true, "Updates received", arguments));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result(
                false, "Sheet not found", new ArrayList<>()));
    }
}
