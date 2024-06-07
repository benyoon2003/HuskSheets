package org.example.controller;

import org.example.model.*;
import org.example.view.*;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The UserController class is responsible for managing user interactions and the flow of data
 * between the view and the model. It handles user authentication, sheet operations, and 
 * server communication.
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
     * Constructs a UserController with the given login view.
     *
     * @param loginView the login view to be used for user authentication.
     */
    public UserController(ILoginView loginView) {
        this.loginPage = loginView;
        loginView.addController(this);
        this.home = new Home();
        this.serverEndpoint = new ServerEndpoint();
    }

    /**
     * Registers a new user with the given username and password.
     *
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
                } else {
                    this.loginPage.displayErrorBox(registerResult.getMessage());
                }
            } else {
                this.loginPage.displayErrorBox("Empty credentials");
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Logs in a user with the given username and password.
     *
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
                } else {
                    this.loginPage.displayErrorBox(loginResult.getMessage());
                }
            } else {
                this.loginPage.displayErrorBox("Empty credentials");
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Retrieves a list of publishers.
     *
     * @return a list of publisher usernames.
     */
    @Override
    public List<String> getPublishers() {
        if (appUser == null) {
            System.out.println("user is null");
        }
        try {
            Result getPublisherResult = serverEndpoint.getPublishers();
            List<String> listOfUsernames = new ArrayList<>();
            for (Argument argument : getPublisherResult.getValue()) {
                System.out.println(argument.getPublisher());
                System.out.println("User: " + this.appUser.getUsername());
                if (!argument.getPublisher().equals(this.appUser.getUsername())) {
                    listOfUsernames.add(argument.getPublisher());
                }
            }
            listOfUsernames.remove(appUser.getUsername());
            return listOfUsernames;
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    /**
     * Sets the current sheet view.
     *
     * @param sheetView the sheet view to be set as current.
     */
    @Override
    public void setCurrentSheet(ISheetView sheetView) {
        this.sheetView = sheetView;
        this.sheetView.addController(this);
    }

    /**
     * Gets the current sheet view.
     *
     * @return the current sheet view.
     */
    public ISheetView getCurrentSheet() {
        return this.sheetView;
    }

    /**
     * Creates a new sheet with the given name.
     *
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
            } else {
                this.homeView.displayErrorBox(createSheetResult.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the given sheet to a local path.
     *
     * @param sheet the sheet to be saved.
     * @param path the path to save the sheet to.
     */
    @Override
    public void saveSheet(IReadOnlySpreadSheet sheet, String path) {
        try {
            this.home.writeXML(sheet, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the given sheet to the server with the specified name.
     *
     * @param sheet the sheet to be saved.
     * @param name the name to save the sheet as on the server.
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

    /**
     * Updates the selected cells with the given value.
     *
     * @param value the value to update the selected cells with.
     */
    public void updateSelectedCells(String value) {
        if (selectedCells == null || selectedCells.getStartRow() == -1) return;
    
        int startRow = selectedCells.getStartRow() - 1;
        int endRow = selectedCells.getEndRow() - 1;
        int startCol = selectedCells.getStartCol() - 1;
        int endCol = selectedCells.getEndCol() - 1;
    
        System.out.println("Updating cells from row " + startRow + " to " + endRow +
                " and columns from " + startCol + " to " + endCol);
    
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                System.out.println("Changing value at (" + row + ", " + col + ") to " + value);
                if (value.equals("")) {
                    changeSpreadSheetValueAt(row, col, "");
                } else {
                    changeSpreadSheetValueAt(row, col, value);
                }
            }
        }
    }

    /**
     * Updates a subscribed sheet on the server.
     *
     * @param publisher the publisher of the sheet.
     * @param sheet the sheet to be updated.
     * @param name the name of the sheet.
     */
    public void updateSubscribedSheet(String publisher, IReadOnlySpreadSheet sheet, String name) {
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

    /**
     * Converts a sheet to a payload string.
     *
     * @param sheet the sheet to be converted.
     * @return the payload string representing the sheet.
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

        if (payload.isEmpty()) {
            return "";
        }
        else {
            System.out.println("convertSheetToPayload is called here!");
            return payload.toString();
        }
    }

    /**
     * Gets the Excel column name for a given column number.
     *
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
     *
     * @param command the toolbar command.
     */
    @Override
    public void handleToolbar(String command) {
        this.sheetView.displayMessage(command + " button clicked");
    }

    /**
     * Updates the selected cells based on the given selected rows and columns.
     *
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
                    selectedCells.getStartCol() + ") to (" + selectedCells.getEndRow() + ", "
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
     * Gets the zero-based index of the selected row.
     *
     * @return the zero-based index of the selected row.
     */
    public int getSelectedRowZeroIndex() {
        return selectedCells.getStartRow() - 1;
    }

    /**
     * Gets the zero-based index of the selected column.
     *
     * @return the zero-based index of the selected column.
     */
    public int getSelectedColZeroIndex() {
        return selectedCells.getStartCol() - 1;
    }

    private boolean singleCellSelected(ISelectedCells selectedCells) {
        return selectedCells.getStartRow() == selectedCells.getEndRow() &&
                selectedCells.getStartCol() == selectedCells.getEndCol();
    }

    /**
     * Opens a sheet from the specified path.
     *
     * @param path the path to the sheet.
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
     * Gets a list of saved sheets.
     *
     * @return a list of saved sheets.
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
     * Gets a list of sheets from the server.
     *
     * @return a list of server sheets.
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
     * Opens a server sheet with the specified name.
     *
     * @param selectedSheet the name of the sheet to open.
     */
    @Override
    public void openServerSheet(String selectedSheet) {
        try {
            Result getUpdatesForSubscriptionResult = this.serverEndpoint.getUpdatesForSubscription(this.appUser.getUsername(), selectedSheet, "0");
            System.out.println("Response from server: " + getUpdatesForSubscriptionResult.getMessage());
            String fullPayload = getUpdatesForSubscriptionResult.getValue().getLast().getPayload();
//            String fullPayload = "";
//            List<Argument> payloads = getUpdatesForSubscriptionResult.getValue();
//            for (Argument payload : payloads) {
//                String payload_string = payload.getPayload();
//                System.out.println("Payload received: " + payload);
//                fullPayload += payload_string;
//            }
            this.spreadsheetModel = this.home.readPayload(fullPayload, selectedSheet);
            this.sheetView = new SheetView(spreadsheetModel);
            this.setCurrentSheet(sheetView);
            this.sheetView.makeVisible();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a list of subscribed sheets from the specified publisher.
     *
     * @param publisher the publisher of the sheets.
     * @return a list of subscribed sheets.
     */
    public List<String> getSubscribedSheets(String publisher) {
        try {
            List<String> sheets = new ArrayList<>();
            String response = this.serverEndpoint.getSheets(publisher);
            sheets = Result.getSheets(response);
            return sheets;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Opens a subscriber sheet with the specified name from the given publisher.
     *
     * @param selectedSheet the name of the sheet to open.
     * @param publisher the publisher of the sheet.
     */
    @Override
    public void openSubscriberSheet(String selectedSheet, String publisher) {
        try {
            Result getUpdatesForSubscriptionResult = this.serverEndpoint.getUpdatesForSubscription(publisher, selectedSheet, "0");
            System.out.println("Response from server: " + getUpdatesForSubscriptionResult.getMessage());
            String fullPayload = getUpdatesForSubscriptionResult.getValue().getLast().getPayload();
            System.out.println("Payload received: " + fullPayload);
            this.spreadsheetModel = this.home.readPayload(fullPayload, selectedSheet);
            this.sheetView = new SubscriberSheetView(publisher, spreadsheetModel);
            this.sheetView.addController(this);
            this.setCurrentSheet(sheetView);
            this.sheetView.makeVisible();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets updates for the published sheet with the specified ID.
     *
     * @param sheet the name of the sheet.
     * @param id the ID of the sheet.
     * @throws Exception if an error occurs while getting updates.
     */
    public void getUpdatesForPublished(String sheet, int id) throws Exception {
        try {
            Result getUpdatesForPublishedResult = this.serverEndpoint.getUpdatesForPublished(this.appUser.getUsername(), sheet, String.valueOf(id));
            String fullPayload = "";
            List<Argument> payloads = getUpdatesForPublishedResult.getValue();
            for (Argument payload : payloads) {
                String payload_string = payload.getPayload();
                System.out.println("Payload received: " + payload);
                fullPayload += payload_string;
            }
            ISpreadsheet changes = this.home.readPayload(fullPayload, sheet);
            System.out.println("Changes payload received: " + fullPayload);
            this.sheetView = new ReviewChangesSheetView(changes, this.spreadsheetModel);
            this.sheetView.addController(this);
            this.setCurrentSheet(sheetView);
            this.sheetView.makeVisible();
            this.sheetView.loadChanges();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Deletes a sheet from the local storage.
     *
     * @param path the path to the sheet to be deleted.
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
     *
     * @param name the name of the sheet to be deleted.
     */
    @Override
    public void deleteSheetFromServer(String name) {
        try {
            serverEndpoint.deleteSheet(appUser.getUsername(), name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles cell referencing.
     *
     * @param row the row of the cell.
     * @param col the column of the cell.
     * @param data the data in the cell.
     * @return the referenced data.
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
     *
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
     * Changes the value of a cell in the spreadsheet.
     *
     * @param selRow the selected row.
     * @param selCol the selected column.
     * @param val the value to set.
     */
    @Override
    public void changeSpreadSheetValueAt(int selRow, int selCol, String val) {
        System.out.println("Changing value at (" + selRow + ", " + selCol + ") to " + val);
        this.spreadsheetModel.setCellRawdata(selRow, selCol, val);
        if (val.startsWith("=")) {
            this.spreadsheetModel.setCellValue(selRow, selCol, val);
            val = this.spreadsheetModel.evaluateFormula(val);
        } else if (val.isEmpty()) {
            this.spreadsheetModel.setCellValue(selRow, selCol, "");
        } else {
            this.spreadsheetModel.setCellValue(selRow, selCol, val);
        }
        System.out.println("Cell updated to: " + val);
        this.sheetView.updateTable();
    }

    /**
     * Evaluates a formula in the spreadsheet.
     *
     * @param formula the formula to evaluate.
     * @return the evaluated result.
     */
    @Override
    public String evaluateFormula(String formula) {
        return this.spreadsheetModel.evaluateFormula(formula);
    }

    /**
     * Cuts the content of a cell.
     *
     * @param selRow the selected row.
     * @param selCol the selected column.
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
     *
     * @param selRow the selected row.
     * @param selCol the selected column.
     */
    @Override
    public void copyCell(int selRow, int selCol) {
        this.clipboardContent = this.spreadsheetModel.getCellRawdata(selRow, selCol);
        this.isCutOperation = false;
    }

    /**
     * Pastes the content of the clipboard into a cell.
     *
     * @param selRow the selected row.
     * @param selCol the selected column.
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
     * Calculates and sets the percentile of the value in the selected cell.
     *
     * @param selRow the selected row.
     * @param selCol the selected column.
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
     *
     * @param row the row of the cell.
     * @param col the column of the cell.
     * @return the formula of the cell.
     */
    @Override
    public String getFormula(int row, int col) {
        return this.spreadsheetModel.getCellFormula(row, col);
    }

    /**
     * Applies conditional formatting to the spreadsheet.
     */
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

    /**
     * Highlights a cell with the specified color.
     *
     * @param row the row of the cell.
     * @param col the column of the cell.
     * @param color the color to highlight the cell with.
     */
    public void highlightCell(int row, int col, Color color) {
        if (color.equals(SheetView.GREEN) || color.equals(SheetView.PINK)) {
            System.out.println("Calling highlightCell with row: " + row + ", col: " + col + ", color: " + color);
        }
        if (sheetView instanceof SheetView) {
            ((SheetView) sheetView).highlightCell(row, col, color);
        }
    }

    /**
     * Validates the input username and password.
     *
     * @param username the username to validate.
     * @param password the password to validate.
     * @return true if both the username and password are non-empty, false otherwise.
     */
    private boolean validateInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }
}