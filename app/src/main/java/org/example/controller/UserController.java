package org.example.controller;

import org.example.model.AppUser;
import org.example.model.Argument;
import org.example.model.Cell;
import org.example.model.Home;
import org.example.model.IAppUser;
import org.example.model.IHome;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISelectedCells;
import org.example.model.ISpreadsheet;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.Result;
import org.example.model.SelectedCells;
import org.example.model.ServerEndpoint;
import org.example.model.Spreadsheet;
import org.example.view.*;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

/**
 * UserController class implements the IUserController interface and 
 * manages user-related operations such as authentication, sheet management, 
 * and handling user interactions within the application. This class interacts 
 * with the model and view components to perform actions like creating, 
 * saving, and deleting sheets, handling cell operations, and managing 
 * user sessions. It serves as the main controller for user functionalities 
 * and ensures the smooth flow of data and actions between the application's 
 * UI and backend.
 */
public class UserController implements IUserController {

    private ILoginView loginPage;
    private ISheetView sheetView;
    private IHomeView homeView;
    private IAppUser appUser;
    private IHome home;

    private ISpreadsheet spreadsheetModel;

    private ISelectedCells selectedCells;

    private String clipboardContent = "";
    private boolean isCutOperation = false;
    private ServerEndpoint serverEndpoint;

    /**
     * Constructor to initialize the UserController.
     * @param loginView the login view.
     */
    public UserController(ILoginView loginView) {
        this.loginPage = loginView;
        loginView.addController(this);
        this.home = new Home();
        this.serverEndpoint = new ServerEndpoint();
    }

    /**
     * Registers a new user with the provided username and password.
     * @param username the username of the new user.
     * @param password the password of the new user.
     */
    public void registerUser(String username, String password) {
        try {
            if (validateInput(username, password)) {
                IAppUser newUser = new AppUser();
                newUser.setUsername(username);
                newUser.setPassword(password);
                Result registerResult = serverEndpoint.register(newUser);
                if (registerResult.getSuccess()) {
                    this.loginPage.disposeLoginPage();
                    this.appUser = newUser;
                    openHomeView();
                }
                else {
                    this.loginPage.displayErrorBox(registerResult.getMessage());
                }
            }
            else {
                this.loginPage.displayErrorBox("Empty credentials");
            }
        }
        catch (Exception ignored){
        }
    }

    /**
     * Logs in a user with the provided username and password.
     * @param username the username of the user.
     * @param password the password of the user.
     */
    @Override
    public void loginUser(String username, String password) {
        try {
            if (validateInput(username, password)) {
                IAppUser newUser = new AppUser();
                newUser.setUsername(username);
                newUser.setPassword(password);
                Result loginResult = serverEndpoint.login(newUser);
                if (loginResult.getSuccess()) {
                    this.loginPage.disposeLoginPage();
                    this.appUser = newUser;
                    openHomeView();
                }
                else {
                    this.loginPage.displayErrorBox(loginResult.getMessage());
                }
            }
            else {
                this.loginPage.displayErrorBox("Empty credentials");
            }
        }
        catch (Exception ignored){
        }
    }

    @Override
    public List<String> getPublishers() {
        if(appUser == null){
            System.out.println("user is null");
        }
        try {

            Result getPublisherResult = serverEndpoint.getPublishers();

            List<String> listOfUsernames = new ArrayList<>();
            for (Argument argument : getPublisherResult.getValue()) {
                System.out.println(argument.getPublisher());
                System.out.println("User: " + this.appUser.getUsername());
                if(!argument.getPublisher().equals(this.appUser.getUsername())) {
                    listOfUsernames.add(argument.getPublisher());
                }
            }
            listOfUsernames.remove(appUser.getUsername());
            return listOfUsernames;
        }
        catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

//    @Override
//    public boolean isUserAuthenticationComplete(String username, String password) {
//        if (validateInput(username, password)) {
//            String message = this.appUser.authenticateUser(username, password);
//            this.loginPage.displayErrorBox(message);
//            if (message.equals("Login successful!")) {
//                this.homeView.makeVisible();
//                this.loginPage.disposeLoginPage();
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean isUserCreatedSuccessfully(String username, String password) {
//        if (validateInput(username, password)) {
//            String message = this.appUser.createAccount(username, password);
//            this.loginPage.displayErrorBox(message);
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * Sets the current sheet view.
     * @param sheetView the sheet view to set as current.
     */
    @Override
    public void setCurrentSheet(ISheetView sheetView) {
        this.sheetView = sheetView;
        this.sheetView.addController(this);
    }

    /**
     * Gets the current sheet view.
     * @return the current sheet view.
     */
    public ISheetView getCurrentSheet() {
        return this.sheetView;
    }

    /**
     * Creates a new sheet.
     * @param name the name of the new sheet.
     */
    @Override
    public void createNewSheet(String name) {
        try {
            Result createSheetResult = serverEndpoint.createSheet(name);
            if (createSheetResult.getSuccess()) {
                this.homeView.disposeHomePage();
                this.spreadsheetModel = new Spreadsheet(name);
                this.sheetView = new SheetView(this.spreadsheetModel);
                this.setCurrentSheet(sheetView);
                this.sheetView.makeVisible();
            }
            else {
                this.homeView.displayErrorBox(createSheetResult.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the given sheet to the specified path.
     * @param sheet the sheet to save.
     * @param path the path to save the sheet to.
     */
    @Override
    public void saveSheetLocally(IReadOnlySpreadSheet sheet, String path) {
        try {
            this.home.writeXML(sheet, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the sheet to the server.
     * @param sheet the sheet to save.
     * @param name the name of the sheet.
     */
    @Override
    public void saveSheetToServer(IReadOnlySpreadSheet sheet, String name) {
        try {
            String payload = convertSheetToPayload(sheet);

            System.out.println("Converted Payload:\n" + payload);
            Result result = serverEndpoint.updatePublished(appUser.getUsername(), name, payload);
            if (result.getSuccess()) {
                System.out.println("Sheet updated successfully on the server.");
            } else {
                System.out.println("Failed to update sheet on the server: " + result.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSubscribedSheet(String publisher, IReadOnlySpreadSheet sheet, String name){
        try {
            String payload = convertSheetToPayload(sheet);

            System.out.println("Converted Payload:\n" + payload);
            Result result = serverEndpoint.updateSubscription(publisher, name, payload);
            if (result.getSuccess()) {
                System.out.println("Sheet updated successfully on the server.");
            } else {
                System.out.println("Failed to update sheet on the server: " + result.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

//     @Override
//     public void saveSheetToServer(IReadOnlySpreadSheet sheet, String name) {
//         try {
//             String payload = convertSheetToPayload(sheet);
//             serverEndpoint.updatePublished(appUser.getUsername(), name, payload);

// //            HttpClient client = HttpClient.newHttpClient();
// //            String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"payload\":\"%s\"}", "team2", name, payload);
// //
// //            HttpRequest request = HttpRequest.newBuilder()
// //                    .uri(new URI("https://husksheets.fly.dev/api/v1/updatePublished"))
// //                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(("team2:Ltf3r008'fYrV405").getBytes(StandardCharsets.UTF_8)))
// //                    .header("Content-Type", "application/json")
// //                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
// //                    .build();
// //
// //            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
// //            if (response.statusCode() == 200) {
// //                System.out.println("Sheet updated successfully!");
// //            } else {
// //                System.out.println("Failed to update sheet: " + response.body());
// //            }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

    /**
     * Converts the sheet to a payload string.
     * @param sheet the sheet to convert.
     * @return the payload string.
     */
    public static String convertSheetToPayload(IReadOnlySpreadSheet sheet) {
        StringBuilder payload = new StringBuilder();
        Cell[][] values = sheet.getCellsObject();
        for (int i = 0; i < sheet.getRows(); i++) {
            for (int j = 0; j < sheet.getCols(); j++) {
                if (values[i][j] != null && !values[i][j].getRawdata().isEmpty()) {
                    String cellValue = values[i][j].isFormula() ? values[i][j].getFormula() : values[i][j].getRawdata();
                    payload.append(String.format("$%s%s %s\\n", getExcelColumnName(j + 1), i + 1, cellValue));
                }
            }
        }
        System.out.println("convertSheetToPayload is called here!");
        return payload.toString();
    }
    
    /**
     * Gets the Excel column name for a given column number.
     * @param columnNumber the column number.
     * @return the Excel column name.
     */
    public static String getExcelColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder();
        while (columnNumber > 0) {
            int remainder = (columnNumber - 1) % 26;
            columnName.insert(0, (char) (remainder + 'A'));
            columnNumber = (columnNumber - 1) / 26;
        }
        return columnName.toString();
    }

    /**
     * Handles toolbar actions.
     * @param command the command to handle.
     */
    @Override
    public void handleToolbar(String command) {
        this.sheetView.displayMessage(command + " button clicked");
    }

    // /**
    //  * Handles statistics dropdown actions.
    //  * @param selectedStat the selected statistic to handle.
    //  */
    // @Override
    // public void handleStatsDropdown(String selectedStat) {
    //     // TODO: Implement statistical calculations if needed
    // }    


    /**
     * Handles cell selection.
     * @param selectedRows the selected rows.
     * @param selectedColumns the selected columns.
     */
    @Override
    public void selectedCells(int[] selectedRows, int[] selectedColumns) {
        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            int startRow = selectedRows[0];
            int endRow = selectedRows[selectedRows.length - 1];
            int startColumn = selectedColumns[0];
            int endColumn = selectedColumns[selectedColumns.length - 1];

            this.selectedCells = new SelectedCells(startRow + 1, endRow + 1, startColumn, endColumn);

            System.out.println("Selected range: (" + (selectedCells.getStartRow()) + ", " +
                    selectedCells.getStartCol() + ") to (" + (selectedCells.getEndRow()) + ", "
                    + selectedCells.getEndCol() + ")");

            if (this.singleCellSelected(this.selectedCells)) {
                this.sheetView.changeFormulaTextField(this.spreadsheetModel.getCellRawdata(
                        this.selectedCells.getStartRow() - 1, this.selectedCells.getStartCol() - 1));
            }
        } else {
            this.selectedCells = new SelectedCells(-1, -1, -1, -1);
        }
    }

    /**
     * Gets the zero-indexed selected row.
     * @return the zero-indexed selected row.
     */
    public int getSelectedRowZeroIndex() {
        return selectedCells.getStartRow() - 1;
    }

    /**
     * Gets the zero-indexed selected column.
     * @return the zero-indexed selected column.
     */
    public int getSelectedColZeroIndex() {
        return selectedCells.getStartCol() - 1;
    }

    /**
     * Checks if a single cell is selected.
     * @param selectedCells the selected cells.
     * @return true if a single cell is selected, false otherwise.
     */
    private boolean singleCellSelected(ISelectedCells selectedCells) {
        return selectedCells.getStartRow() == selectedCells.getEndRow() &&
                selectedCells.getStartCol() == selectedCells.getEndCol();
    }

    /**
     * Opens a sheet from the specified path.
     * @param path the path to open the sheet from.
     */
    @Override
    public void openSheet(String path) {
        try {
            this.homeView.disposeHomePage();
            this.spreadsheetModel = this.home.readXML(path);
            this.sheetView = new SheetView(spreadsheetModel);
            this.sheetView.makeVisible();
            this.setCurrentSheet(sheetView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the list of saved sheets.
     * @return the list of saved sheets.
     */
    @Override
    public List<String> getSavedSheets() {
        List<String> sheets = new ArrayList<>();
        File folder = new File("HuskSheets/sheets");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    sheets.add(file.getName());
                }
            }
        }
        return sheets;
    }

    /**
     * Gets the list of sheets from the server.
     * @return the list of server sheets.
     */
    @Override
    public List<String> getServerSheets() {
        List<String> sheets = new ArrayList<>();
        try {
            String response = serverEndpoint.getSheets(appUser.getUsername());
            sheets = Result.getSheets(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sheets;
    }

    /**
     * Opens a sheet from the server.
     * @param selectedSheet the name of the sheet to open.
     */
    @Override
    public void openServerSheet(String selectedSheet) {
        try {
            Result getUpdatesForSubscriptionResult = this.serverEndpoint.getUpdatesForSubscription(this.appUser.getUsername(), selectedSheet, "0");
            System.out.println("Response from server: " + getUpdatesForSubscriptionResult.getMessage());

            String fullPayload = "";
            List<Argument> payloads = getUpdatesForSubscriptionResult.getValue();
            for(Argument payload : payloads) {
                String payload_string = payload.getPayload();
                System.out.println("Payload received: " + payload);
                fullPayload += payload_string;
            }
            this.spreadsheetModel = this.home.readPayload(fullPayload, selectedSheet);
            this.sheetView = new SheetView(spreadsheetModel);
            this.setCurrentSheet(sheetView);
            this.sheetView.makeVisible();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getSubscribedSheets(String publisher){
        try{
            List<String> sheets = new ArrayList<>();
            String response = this.serverEndpoint.getSheets(publisher);
            sheets = Result.getSheets(response);
            return sheets;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void openSubscriberSheet(String selectedSheet, String publisher) {
        try {

            Result getUpdatesForSubscriptionResult = this.serverEndpoint.getUpdatesForSubscription(publisher, selectedSheet, "0");
            System.out.println("Response from server: " + getUpdatesForSubscriptionResult.getMessage());

            String fullPayload = "";
            List<Argument> payloads = getUpdatesForSubscriptionResult.getValue();
            for(Argument payload : payloads) {
                String payload_string = payload.getPayload();
                System.out.println("Payload received: " + payload);
                fullPayload += payload_string;
            }
            System.out.println("Payload received: " + fullPayload);
            this.spreadsheetModel = this.home.readPayload(fullPayload, selectedSheet);
            this.sheetView = new SubscriberSheetView(publisher, spreadsheetModel);
            this.sheetView.addController(this);
            this.setCurrentSheet(sheetView);
            this.sheetView.makeVisible();
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }

    /**
     * Gets all subscriber updates since the specified id
     * @param sheet name of the sheet
     * @param id version of sheet
     */
    public void getUpdatesForPublished(String sheet, int id) throws Exception {
        try{
            Result getUpdatesForPublishedResult = this.serverEndpoint.getUpdatesForPublished(this.appUser.getUsername(), sheet, String.valueOf(id));
            //String payload = getUpdatesForPublishedResult.getValue().get(0).getPayload();

            String fullPayload = "";
            List<Argument> payloads = getUpdatesForPublishedResult.getValue();
            for(Argument payload : payloads) {
                String payload_string = payload.getPayload();
                System.out.println("Payload received: " + payload);
                fullPayload += payload_string;
            }


            ISpreadsheet changes = this.home.readPayload(fullPayload, sheet);
            System.out.println("Changes payload received: " + fullPayload);
            //Open new sheetview to review changes
            this.sheetView = new ReviewChangesSheetView(changes, this.spreadsheetModel);
            this.sheetView.addController(this);
            this.setCurrentSheet(sheetView);
            this.sheetView.makeVisible();
            this.sheetView.loadChanges();
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Deletes a sheet at the specified path.
     * @param path the path to delete the sheet from.
     */
    @Override
    public void deleteSheet(String path) {
        File file = new File("sheets/" + path);
        if (file.exists()) {
            file.delete();
            this.homeView.updateSavedSheets();
        }
    }

    /**
     * Deletes a sheet from the server.
     * @param name the name of the sheet.
     */
    @Override
    public void deleteSheetFromServer(String name) {
        try{
            serverEndpoint.deleteSheet(appUser.getUsername(), name);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Handles referencing of a cell.
     * @param row the row of the cell.
     * @param col the column of the cell.
     * @param data the data in the cell.
     * @return the result of the referencing.
     */
    @Override
    public String handleReferencingCell(int row, int col, String data) {
        String rawdata = this.spreadsheetModel.getCellRawdata(row, col);
        if (rawdata.startsWith("=")) {
            return this.spreadsheetModel.evaluateFormula(rawdata);
        } else {
            return data;
        }
    }

    /**
     * Gets the home view.
     * @return the home view.
     */
    @Override
    public IHomeView getHomeView() {
        return this.homeView;
    }

    /**
     * Opens the home view.
     */
    public void openHomeView() {
        this.homeView = new HomeView();
        homeView.addController(this);
        this.homeView.makeVisible();
    }

    /**
     * Changes the value of a cell in the spreadsheet at the specified row and column.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     * @param val the value to set.
     */
    @Override
    public void changeSpreadSheetValueAt(int selRow, int selCol, String val) {
        this.spreadsheetModel.setCellRawdata(selRow, selCol, val);
        if (val.startsWith("=")) {
            this.spreadsheetModel.setCellValue(selRow, selCol, val);
            val = this.spreadsheetModel.evaluateFormula(val);
        }
        this.spreadsheetModel.setCellValue(selRow, selCol, val);
        this.sheetView.updateTable();
    }

   /**
     * Evaluates a formula.
     * @param formula the formula to evaluate.
     * @return the result of the formula evaluation.
     */
    @Override
    public String evaluateFormula(String formula) {
        return this.spreadsheetModel.evaluateFormula(formula);
    }

    /**
     * Cuts the content of a cell.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     */
    @Override
    public void cutCell(int selRow, int selCol) {
        this.clipboardContent = this.spreadsheetModel.getCellRawdata(selRow, selCol);
        this.spreadsheetModel.setCellValue(selRow, selCol, "");
        this.sheetView.updateTable();
        this.isCutOperation = true;
    }

    /**
     * Copies the content of a cell.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     */
    @Override
    public void copyCell(int selRow, int selCol) {
        this.clipboardContent = this.spreadsheetModel.getCellRawdata(selRow, selCol);
        this.isCutOperation = false;
    }

    /**
     * Pastes the content into a cell.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     */
    @Override
    public void pasteCell(int selRow, int selCol) {
        if (!clipboardContent.isEmpty()) {
            this.spreadsheetModel.setCellValue(selRow, selCol, clipboardContent);
            if (isCutOperation) {
                clipboardContent = "";
                isCutOperation = false;
            }
            this.sheetView.updateTable();
        }
    }

    /**
     * Calculates the percentile of a cell value.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     */
    @Override
    public void getPercentile(int selRow, int selCol) {
        String value = this.spreadsheetModel.getCellValue(selRow, selCol);
        if (value.isEmpty() || value.contains("%")) return;

        try {
            double num = Double.parseDouble(value);
            this.spreadsheetModel.setCellValue(selRow, selCol, "" + (num * 100) + "%");
        } catch (NumberFormatException e) {
            this.spreadsheetModel.setCellValue(selRow, selCol, "Error");
        }
    }

    /**
     * Gets the formula of a cell.
     * @param row the row of the cell.
     * @param col the column of the cell.
     * @return the formula of the cell.
     */
    @Override
    public String getFormula(int row, int col) {
        return this.spreadsheetModel.getCellFormula(row, col);
    }

    @Override
    public void applyConditionalFormatting() {
        System.out.println("Applying Conditional Formatting...");
        Cell[][] cells = this.spreadsheetModel.getCellsObject();
        for (int i = 0; i < this.spreadsheetModel.getRows(); i++) {
            for (int j = 0; j < this.spreadsheetModel.getCols(); j++) {
                String value = cells[i][j].getValue();
                if (value != null && !value.isEmpty()) {
                    try {
                        double numericValue = Double.parseDouble(value);
                        if (numericValue < 0) {
                            System.out.println("Highlighting cell (" + i + ", " + j + ") with PINK");
                            this.highlightCell(i, j, SheetView.PINK);
                        } else if (numericValue > 0) {
                            System.out.println("Highlighting cell (" + i + ", " + j + ") with GREEN");
                            this.highlightCell(i, j, SheetView.GREEN);
                        }
                    } catch (NumberFormatException e) {
                        this.highlightCell(i, j, Color.WHITE);
                    }
                } else {
                    this.highlightCell(i, j, Color.WHITE);
                }
            }
        }
        this.sheetView.updateTable();
        System.out.println("Conditional Formatting Applied.");
    }

    public void highlightCell(int row, int col, Color color) {
        if (color.equals(SheetView.GREEN) || color.equals(SheetView.PINK)) {
            System.out.println("Calling highlightCell with row: " + row + ", col: " + col + ", color: " + color);
        }
        if (sheetView instanceof SheetView) {
            ((SheetView) sheetView).highlightCell(row, col, color);
        }
    }
    
    
   
    
    /**
     * Validates the input for username and password.
     * @param username the username to validate.
     * @param password the password to validate.
     * @return true if input is valid, false otherwise.
     */
    private boolean validateInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

}