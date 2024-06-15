package org.example.controller;

import org.example.model.*;
import org.example.view.*;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The UserController class is responsible for managing user interactions and the flow of data
 * between the view and the model. It handles user authentication, sheet operations, and
 * server communication.
 */
public class UserController implements IUserController {
    private final ILoginView loginPage;
    private ISheetView sheetView;
    private IHomeView homeView;
    private IAppUser appUser;
    private final IHome home;
    private ISpreadsheet spreadsheetModel;
    private ISelectedCells selectedCells;
    String clipboardContent;
    boolean isCutOperation;
    protected final ServerEndpoint serverEndpoint;
    private String currentSubscribedPublisher;

    /**
     * Constructs a UserController with the given login view.
     *
     * @param loginView the login view to be used for user authentication.
     */
    public UserController(ILoginView loginView) {
        this.loginPage = loginView;
        loginView.addController(this);
        this.home = new Home();
        this.serverEndpoint = new ServerEndpoint();
        this.clipboardContent = "";
        this.isCutOperation = false;
        this.currentSubscribedPublisher = "";
    }

    /**
     * Constructs a UserController with the given login view and url.
     *
     * @param loginView the login view to be used for user authentication.
     * @param url the server url
     */
    public UserController(ILoginView loginView, String url) {
        this.loginPage = loginView;
        loginView.addController(this);
        this.home = new Home();
        this.serverEndpoint = new ServerEndpoint(url);
        this.clipboardContent = "";
        this.isCutOperation = false;
        this.currentSubscribedPublisher = "";
    }

    /**
     * Constructs a UserController and starts the program through the given inputs. This is
     * mainly utilized to handle command line args for testing.
     *
     * @param url       a URL
     * @param username  a username
     * @param password  a password
     * @param publisher a publisher
     * @param sheetname a sheet name
     */
    public UserController(String url, String username, String password, String publisher, String sheetname) {
        this.serverEndpoint = new ServerEndpoint(url); // Initialize the server endpoint with the provided URL.
        this.loginPage = new LoginView(); // Initialize the login page.
        this.loginPage.disposeLoginPage(); // Dispose of the login page.
        this.home = new Home(); // Initialize the home model.
        this.homeView = new HomeView(); // Initialize the home view.
        handleCommandLine(username, password, sheetname, publisher); // Handle command line arguments.
    }

    /**
     * Takes the given inputs and opens the publisher's sheet.
     *
     * @param username  a username
     * @param password  a pasword
     * @param sheetname a sheet name
     * @param publisher a publisher
     * @author Ben
     */
    private void handleCommandLine(String username, String password,
                                   String sheetname, String publisher) {
        try {
            registerUser(username, password); // Attempt to register the user.
        } catch (Exception registerError) {
            if (registerError.getMessage().equals("User already exists")) { // If the user already exists.
                try {
                    loginUser(username, password); // Attempt to log in the user.
                } catch (Exception loginError) {
                    this.homeView.displayErrorBox(loginError.getMessage()); // Display login error.
                }
            }
            this.homeView.displayErrorBox(registerError.getMessage()); // Display registration error.
            System.out.println(registerError.getMessage()); // Print registration error.
        }
        openSubscriberSheet(sheetname, publisher); // Open the subscriber sheet.
    }

    @Override
    public IAppUser getAppUser() {
        return appUser; // Return the current app user.
    }

    @Override
    public boolean isCutOperation() {
        return isCutOperation; // Return the cut operation flag.
    }

    @Override
    public String getClipboardContent() {
        return clipboardContent; // Return the clipboard content.
    }

    @Override
    public void registerUser(String username, String password) throws Exception {
        try {
            if (validateInput(username, password)) { // Validate the input.
                IAppUser newUser = new AppUser(username, password); // Create a new app user.
                Result result = serverEndpoint.register(newUser); // Register the new user.
                if (result.getSuccess()) { // If registration is successful.
                    this.loginPage.disposeLoginPage(); // Dispose of the login page.
                    this.appUser = newUser; // Set the app user
                    openHomeView(); // Open the home view
                } else {
                    throw new Exception(result.getMessage()); // Throw an exception with the result message.
                }
            } else {
                this.loginPage.displayErrorBox("Empty credentials"); // Display error for empty credentials.
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage()); // Throw an exception with the caught exception message.
        }
    }

    @Override
    public void loginUser(String username, String password) throws Exception {
        try {
            if (validateInput(username, password)) { // Validate the input.
                IAppUser newUser = new AppUser(username, password); // Create a new app user.
                Result result = serverEndpoint.login(newUser); // Log in the new user.
                if (result.getSuccess()) { // If login is successful.
                    this.loginPage.disposeLoginPage(); // Dispose of the login page.
                    this.appUser = newUser; // Set the app user.
                    openHomeView(); // Open the home view.
                } else {
                    throw new Exception(result.getMessage()); // Throw an exception with the result message.
                }
            } else {
                this.loginPage.displayErrorBox("Empty credentials"); // Display error for empty credentials.
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage()); // Throw an exception with the caught exception message.
        }
    }

    @Override
    public List<String> getPublishersFromServer() {
        try {
            Result result = serverEndpoint.getPublishers(); // Get the list of publishers from the server.
            List<String> listOfUsernames = new ArrayList<>();
            if (result.getSuccess()) { // If the request is successful.
                for (Argument argument : result.getValue()) {
                    if (!argument.getPublisher().equals(this.appUser.getUsername())) { // Exclude the current user.
                        listOfUsernames.add(argument.getPublisher()); // Add the publisher to the list.
                    }
                }
                // The list should exclude the current user
                listOfUsernames.remove(appUser.getUsername());
            } else {
                homeView.displayErrorBox(result.getMessage()); // Display the error message.
            }
            return listOfUsernames; // Return the list of publishers.
        } catch (Exception e) {
            this.homeView.displayErrorBox(e.getMessage()); // Display the error message.
        }
        return new ArrayList<>(); // Return an empty list if an exception occurs.
    }

    @Override
    public void setCurrentSheet(ISheetView sheetView) {
        this.sheetView = sheetView; // Set the current sheet view.
        this.sheetView.addController(this); // Add this controller to the sheet view.
        this.sheetView.makeVisible(); // Make the sheet view visible.
    }

    @Override
    public ISpreadsheet getSpreadsheetModel() {
        return spreadsheetModel; // Return the current spreadsheet model.
    }

    @Override
    public void createNewServerSheet(String name) {
        try {
            Result result = serverEndpoint.createSheet(name); // Create a new sheet on the server.
            if (result.getSuccess()) { // If the sheet is created successfully.
                this.homeView.disposeHomePage(); // Dispose of the home page.
                this.spreadsheetModel = new Spreadsheet(name); // Create a new spreadsheet model.
                setCurrentSheet(new SheetView(this.spreadsheetModel)); // Set the current sheet view with the new model.
            } else {
                this.homeView.displayErrorBox(result.getMessage()); // Display the error message.
            }
        } catch (Exception e) {
            this.homeView.displayErrorBox(e.getMessage()); // Display the error message.
        }
    }

    @Override
    public void saveSheetLocally(IReadOnlySpreadSheet sheet, String path) {
        try {
            this.home.writeXML(sheet, path); // Save the sheet locally as an XML file.
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace if an exception occurs.
        }
    }

    @Override
    public void saveSheetToServer(IReadOnlySpreadSheet sheet, String sheetName) {
        try {
            String payload = Spreadsheet.convertSheetToPayload(sheet); // Convert the sheet to a payload.
            Result result = serverEndpoint.updatePublished(appUser.getUsername(), sheetName, payload); // Update the sheet on the server.
            if (!result.getSuccess()) {
                sheetView.displayMessage(result.getMessage()); // Display the error message if the update is unsuccessful.
            }
        } catch (Exception e) {
            this.sheetView.displayMessage(e.getMessage()); // Display the error message if an exception occurs.
        }
    }

    @Override
    public void updateSelectedCells(String value) {
        int startRow = selectedCells.getStartRow(); // Get the start row of the selected cells.
        int endRow = selectedCells.getEndRow(); // Get the end row of the selected cells.
        int startCol = selectedCells.getStartCol(); // Get the start column of the selected cells.
        int endCol = selectedCells.getEndCol(); // Get the end column of the selected cells.

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (value.isEmpty()) {
                    changeSpreadSheetValueAt(row, col, ""); // Clear the cell if the value is empty.
                } else {
                    changeSpreadSheetValueAt(row, col, value); // Update the cell with the provided value.
                }
            }
        }
    }

    @Override
    public void updateSubscribedSheet(String publisher, IReadOnlySpreadSheet sheet, String name) {
        try {
            String payload = Spreadsheet.convertSheetToPayload(sheet); // Convert the sheet to a payload.
            Result result = serverEndpoint.updateSubscription(publisher, name, payload); // Update the subscription on the server.
            if (!result.getSuccess()) {
                sheetView.displayMessage(result.getMessage()); // Display the error message if the update is unsuccessful.
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace if an exception occurs.
        }
    }

    @Override
    public void setSelectedCells(int[] selectedRows, int[] selectedColumns) {
        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            int startRow = selectedRows[0]; // Get the start row of the selected cells.
            int endRow = selectedRows[selectedRows.length - 1]; // Get the end row of the selected cells.
            int startColumn = selectedColumns[0]; // Get the start column of the selected cells.
            int endColumn = selectedColumns[selectedColumns.length - 1]; // Get the end column of the selected cells.

            this.selectedCells = new SelectedCells(startRow + 1, endRow + 1, startColumn, endColumn); // Create a new SelectedCells object.

            if (this.singleCellSelected(this.selectedCells)) {
                this.sheetView.changeFormulaTextField(this.spreadsheetModel.getCellRawdata(
                        this.selectedCells.getStartRow(), this.selectedCells.getStartCol())); // Change the formula text field if a single cell is selected.
            }
        } else {
            // Default selection when no cells have been selected
            this.selectedCells = new SelectedCells(-1, -1, -1, -1); // Create a new SelectedCells object with default values.
        }
    }

    @Override
    public int getSelectedStartRow() {
        return selectedCells.getStartRow(); // Return the start row of the selected cells.
    }

    @Override
    public int getSelectedStartCol() {
        return selectedCells.getStartCol(); // Return the start column of the selected cells.
    }

    @Override
    public int getSelectedEndRow() {
        return selectedCells.getEndRow(); // Return the end row of the selected cells.
    }

    @Override
    public int getSelectedEndCol() {
        return selectedCells.getEndCol(); // Return the end column of the selected cells.
    }

    /**
     * Determines if a single cell has been selected.
     *
     * @param selectedCells an ISelectedCells object
     * @return boolean
     * @author Vinay
     */
    private boolean singleCellSelected(ISelectedCells selectedCells) {
        return selectedCells.getStartRow() == selectedCells.getEndRow() &&
                selectedCells.getStartCol() == selectedCells.getEndCol(); // Return true if a single cell is selected. 
    }

    @Override
    public void openSheetLocally(String path) {
        try {
            this.homeView.disposeHomePage(); // Dispose of the home page.
            this.spreadsheetModel = this.home.readXML(path); // Read the spreadsheet from the local XML file.
            setCurrentSheet(new SheetView(spreadsheetModel)); // Set the current sheet view with the loaded model.
        } catch (Exception e) {
            homeView.displayErrorBox(e.getMessage()); // Display the error message if an exception occurs.
        }
    }

    @Override
    public List<String> getSavedSheetsLocally() {
        List<String> sheets = new ArrayList<>(); 
        File folder = new File("..\\sheets"); // Define the directory for saved sheets.
        if (!folder.exists()) {
            folder.mkdirs(); // Create the directory if it does not exist.
        }
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    sheets.add(file.getName()); // Add XML files to the list of saved sheets.
                }
            }
        }
        return sheets; // Return the list of saved sheets.
    }

    @Override
    public List<String> getAppUserSheets() {
        return accessSheetsFromUser(appUser.getUsername()); // Return the list of sheets for the current user.
    }

    @Override
    public void openServerSheet(String selectedSheet) {
        try {
            Result result = this.serverEndpoint.getUpdatesForSubscription(this.appUser.getUsername(), selectedSheet, "0"); // Get updates for the selected sheet.
            System.out.println("test result: " + result);
            String fullPayload = "";
            if (result.getSuccess()) {
                try {
                    fullPayload = result.getValue().getLast().getPayload(); // Get the payload of the last update.
                } catch (Exception e) {
                    //payload is empty
                }
                this.spreadsheetModel = this.home.readPayload(fullPayload, selectedSheet); // Read the payload into the spreadsheet model.
                setCurrentSheet(new SheetView(spreadsheetModel)); // Set the current sheet view with the loaded model.
            } else {
                homeView.displayErrorBox(result.getMessage()); // Display the error message if the update is unsuccessful.
            }
        } catch (Exception e) {
            homeView.displayErrorBox(e.getMessage()); // Display the error message if an exception occurs.
        }
    }

    @Override
    public List<String> accessSheetsFromUser(String publisher) {
        List<String> sheets = new ArrayList<>();
        try {
            Result result = serverEndpoint.getSheets(publisher); // Get the list of sheets for the given publisher.
            if (result.getSuccess()) {
                for (Argument argument : result.getValue()) {
                    sheets.add(argument.getSheet()); // Add the sheets to the list.
                }
                return sheets; // Return the list of sheets.
            } else {
                homeView.displayErrorBox(result.getMessage()); // Display the error message if the request is unsuccessful.
            }
        } catch (Exception e) {
            homeView.displayErrorBox(e.getMessage()); // Display the error message if an exception occurs.
        }
        return sheets; // Return an empty list if an exception occurs.
    }

    @Override
    public void openSubscriberSheet(String selectedSheet, String publisher) {
        try {
            Result result = this.serverEndpoint.getUpdatesForSubscription(publisher, selectedSheet, "0"); // Get updates for the selected sheet.
            if (result.getSuccess()) {
                this.currentSubscribedPublisher = publisher; // Set the current subscribed publisher.
                String fullPayload = result.getValue().getLast().getPayload(); // Get the payload of the last update.
                this.spreadsheetModel = this.home.readPayload(fullPayload, selectedSheet); // Read the payload into the spreadsheet model.
                this.setCurrentSheet(new SubscriberSheetView(publisher, spreadsheetModel)); // Set the current sheet view with the loaded model.
            } else {
                homeView.displayErrorBox(result.getMessage()); // Display the error message if the update is unsuccessful.
            }
        } catch (Exception e) {
            homeView.displayErrorBox(e.getMessage()); // Display the error message if an exception occurs.
        }
    }

    @Override
    public void getUpdatesForPublished(String sheet, int id) throws Exception {
        try {
            Result result = this.serverEndpoint.getUpdatesForPublished(this.appUser.getUsername(), sheet, String.valueOf(id)); // Get updates for the published sheet.
            if (result.getSuccess()) {
                String fullPayload = result.getValue().getLast().getPayload(); // Get the payload of the last update.
                ISpreadsheet changes = this.home.readPayload(fullPayload, sheet); // Read the payload into the changes spreadsheet.
                this.setCurrentSheet(new ReviewChangesSheetView(changes, this.spreadsheetModel)); // Set the current sheet view with the changes.
                this.sheetView.loadChanges(); // Load the changes into the view.
            } else {
                sheetView.displayMessage(result.getMessage()); // Display the error message if the update is unsuccessful.
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage()); // Throw an exception with the caught exception message.
        }
    }

    @Override
    public void getUpdatesForSubscribed(String sheet, int id) throws Exception {
        try {
            if (!this.currentSubscribedPublisher.isEmpty()) { // Check if there is a current subscribed publisher.
                Result result = this.serverEndpoint.getUpdatesForSubscription(
                        currentSubscribedPublisher, sheet, String.valueOf(id)); // Get updates for the subscribed sheet.
                if (result.getSuccess()) {
                    String fullpayload = result.getValue().getLast().getPayload(); // Get the payload of the last update.
                    this.spreadsheetModel = this.home.readPayload(fullpayload, sheet); // Read the payload into the spreadsheet model.
                    this.setCurrentSheet(new SubscriberSheetView(currentSubscribedPublisher,
                            this.spreadsheetModel)); // Set the current sheet view with the loaded model.
                } else {
                    sheetView.displayMessage(result.getMessage()); // Display the error message if the update is unsuccessful.
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage()); // Throw an exception with the caught exception message.
        }
    }

    @Override
    public void deleteSheetLocally(String path) {
        File file = new File("HuskSheets/sheets/" + path); // Define the file path for the sheet to be deleted.
        if (file.exists()) {
            file.delete(); // Delete the file if it exists.
            this.homeView.updateSavedSheets(); // Update the saved sheets in the home view.
        }
    }

    @Override
    public void deleteSheetFromServer(String name) {
        try {
            Result result = serverEndpoint.deleteSheet(appUser.getUsername(), name); // Delete the sheet from the server.
            if (!result.getSuccess()) {
                homeView.displayErrorBox(result.getMessage()); // Display the error message if the deletion is unsuccessful.
            }
        } catch (Exception e) {
            homeView.displayErrorBox(e.getMessage()); // Display the error message if an exception occurs.
        }
    }

    @Override
    public String handleReevaluatingCellFormula(int row, int col, String data) {
        String rawdata = this.spreadsheetModel.getCellRawdata(row, col); // Get the raw data of the cell.
        if (rawdata.startsWith("=")) {
            return this.spreadsheetModel.evaluateFormula(rawdata); // Evaluate the formula if the raw data starts with "=".
        } else {
            return data; // Return the data if it is not a formula.
        }
    }

    @Override
    public IHomeView getHomeView() {
        return this.homeView; // Return the current home view.
    }

    @Override
    public void openHomeView() {
        this.homeView = new HomeView(); // Initialize a new home view.
        homeView.addController(this); // Add this controller to the home view.
        this.homeView.makeVisible(); // Make the home view visible.
    }

    @Override
    public void changeSpreadSheetValueAt(int selRow, int selCol, String val) {
        this.spreadsheetModel.setCellRawdata(selRow, selCol, val); // Set the raw data of the cell.
        if (val.startsWith("=")) {
            this.spreadsheetModel.setCellValue(selRow, selCol, val); // Set the cell value.
            val = this.spreadsheetModel.evaluateFormula(val); // Evaluate the formula.
        } else if (val.isEmpty()) {
            this.spreadsheetModel.setCellValue(selRow, selCol, ""); // Clear the cell value if it is empty.
        } else {
            this.spreadsheetModel.setCellValue(selRow, selCol, val); // Set the cell value.
        }
        this.sheetView.updateTable(); // Update the table view.
    }

    @Override
    public void cutCell(int selRow, int selCol) {
        this.clipboardContent = this.spreadsheetModel.getCellRawdata(selRow, selCol); // Get the raw data of the cell and store it in the clipboard.
        this.spreadsheetModel.setCellValue(selRow, selCol, ""); // Clear the cell value.
        this.sheetView.updateTable(); // Update the table view.
        this.isCutOperation = true; // Set the cut operation flag to true.
    }

    @Override
    public void copyCell(int selRow, int selCol) {
        this.clipboardContent = this.spreadsheetModel.getCellRawdata(selRow, selCol); // Get the raw data of the cell and store it in the clipboard.
        this.isCutOperation = false; // Set the cut operation flag to false.
    }

    @Override
    public void pasteCell(int selRow, int selCol) {
        if (!clipboardContent.isEmpty()) {
            this.spreadsheetModel.setCellValue(selRow, selCol, clipboardContent); // Set the cell value to the clipboard content.
            if (isCutOperation) {
                clipboardContent = ""; // Clear the clipboard content if it was a cut operation.
                isCutOperation = false; // Reset the cut operation flag.
            }
            this.sheetView.updateTable(); // Update the table view.
        }
    }

    @Override
    public void getPercentile(int selRow, int selCol) {
        String value = this.spreadsheetModel.getCellValue(selRow, selCol); // Get the cell value.
        if (value.isEmpty() || value.contains("%")) return; // Return if the value is empty or already contains a percentage.

        try {
            double num = Double.parseDouble(value); // Parse the value to a double.
            this.spreadsheetModel.setCellValue(selRow, selCol, "" + (num * 100) + "%"); // Set the cell value to the percentage.
        } catch (NumberFormatException e) {
            this.spreadsheetModel.setCellValue(selRow, selCol, "Error"); // Set the cell value to "Error" if the value is not a number.
        }
    }

    @Override
    public void applyConditionalFormatting() {
        Cell[][] cells = this.spreadsheetModel.getCellsObject(); // Get the cells of the spreadsheet model.
        for (int i = 0; i < this.spreadsheetModel.getRows(); i++) {
            for (int j = 0; j < this.spreadsheetModel.getCols(); j++) {
                String value = cells[i][j].getValue(); // Get the value of the cell.
                if (value != null && !value.isEmpty()) {
                    try {
                        double numericValue = Double.parseDouble(value); // Parse the value to a double.
                        if (numericValue < 0) {
                            sheetView.highlightCell(i, j, SheetView.PINK); // Highlight the cell in pink if the value is negative.
                        } else if (numericValue > 0) {
                            sheetView.highlightCell(i, j, SheetView.GREEN); // Highlight the cell in green if the value is positive.
                        }
                    } catch (NumberFormatException e) {
                        sheetView.highlightCell(i, j, Color.WHITE); // Highlight the cell in white if the value is not a number.
                    }
                } else {
                    sheetView.highlightCell(i, j, Color.WHITE); // Highlight the cell in white if the value is empty.
                }
            }
        }
        this.sheetView.updateTable(); // Update the table view.
    }

    /**
     * Validates the input username and password.
     *
     * @param username the username to validate.
     * @param password the password to validate.
     * @return true if both the username and password are non-empty, false otherwise.
     * @author Ben
     */
    private boolean validateInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty(); // Return true if both username and password are non-empty.
    }

}