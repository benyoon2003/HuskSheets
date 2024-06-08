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
    private String clipboardContent;
    private boolean isCutOperation;
    private final ServerEndpoint serverEndpoint;

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
    }

    public void registerUser(String username, String password) {
        try {
            if (validateInput(username, password)) {
                IAppUser newUser = new AppUser(username, password);
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
        catch (Exception e) {
            this.loginPage.displayErrorBox(e.getMessage());
        }
    }

    @Override
    public void loginUser(String username, String password) {
        try {
            if (validateInput(username, password)) {
                IAppUser newUser = new AppUser(username, password);
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
        catch (Exception e) {
            this.loginPage.displayErrorBox(e.getMessage());
        }
    }

    @Override
    public List<String> getPublishersFromServer() {
        try {
            Result getPublisherResult = serverEndpoint.getPublishers();
            List<String> listOfUsernames = new ArrayList<>();
            for (Argument argument : getPublisherResult.getValue()) {
                if (!argument.getPublisher().equals(this.appUser.getUsername())) {
                    listOfUsernames.add(argument.getPublisher());
                }
            }
            // The list should exclude the current user
            listOfUsernames.remove(appUser.getUsername());
            return listOfUsernames;
        }
        catch (Exception e) {
            this.homeView.displayErrorBox(e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public void setCurrentSheet(ISheetView sheetView) {
        this.sheetView = sheetView;
        this.sheetView.addController(this);
        this.sheetView.makeVisible();
    }

    @Override
    public void createNewServerSheet(String name) {
        try {
            Result createSheetResult = serverEndpoint.createSheet(name);
            if (createSheetResult.getSuccess()) {
                this.homeView.disposeHomePage();
                this.spreadsheetModel = new Spreadsheet(name);
                setCurrentSheet(new SheetView(this.spreadsheetModel));
            } else {
                this.homeView.displayErrorBox(createSheetResult.getMessage());
            }
        } catch (Exception e) {
            this.homeView.displayErrorBox(e.getMessage());
        }
    }

    @Override
    public void saveSheetLocally(IReadOnlySpreadSheet sheet, String path) {
        try {
            this.home.writeXML(sheet, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveSheetToServer(IReadOnlySpreadSheet sheet, String sheetName) {
        try {
            String payload = convertSheetToPayload(sheet);
            Result result = serverEndpoint.updatePublished(appUser.getUsername(), sheetName, payload);
            if (!result.getSuccess()) {
                sheetView.displayMessage(result.getMessage());
            }
        } catch (Exception e) {
            this.sheetView.displayMessage(e.getMessage());
        }
    }

    public void updateSelectedCells(String value) {
        int startRow = selectedCells.getStartRow();
        int endRow = selectedCells.getEndRow();
        int startCol = selectedCells.getStartCol();
        int endCol = selectedCells.getEndCol();

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (value.isEmpty()) {
                    changeSpreadSheetValueAt(row, col, "");
                } else {
                    changeSpreadSheetValueAt(row, col, value);
                }
            }
        }
    }

    public void updateSubscribedSheet(String publisher, IReadOnlySpreadSheet sheet, String name) {
        try {
            String payload = convertSheetToPayload(sheet);
            Result result = serverEndpoint.updateSubscription(publisher, name, payload);
            if (!result.getSuccess()) {
                sheetView.displayMessage(result.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertSheetToPayload(IReadOnlySpreadSheet sheet) {
        StringBuilder payload = new StringBuilder();
        Cell[][] values = sheet.getCellsObject();
        for (int i = 0; i < sheet.getRows(); i++) {
            for (int j = 0; j < sheet.getCols(); j++) {
                if (values[i][j] != null && !values[i][j].getRawdata().isEmpty()) {
                    String cellValue = values[i][j].isFormula() ? values[i][j].getFormula() : values[i][j].getRawdata();
                    payload.append(String.format("$%s%s %s\\n", getColumnName(j + 1), i + 1, cellValue));
                }
            }
        }
        return payload.toString();
    }

    public static String getColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder();
        while (columnNumber > 0) {
            int remainder = (columnNumber - 1) % 26;
            columnName.insert(0, (char) (remainder + 'A'));
            columnNumber = (columnNumber - 1) / 26;
        }
        return columnName.toString();
    }

    @Override
    public void setSelectedCells(int[] selectedRows, int[] selectedColumns) {
        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            int startRow = selectedRows[0];
            int endRow = selectedRows[selectedRows.length - 1];
            int startColumn = selectedColumns[0];
            int endColumn = selectedColumns[selectedColumns.length - 1];

            this.selectedCells = new SelectedCells(startRow + 1, endRow + 1, startColumn, endColumn);

            if (this.singleCellSelected(this.selectedCells)) {
                this.sheetView.changeFormulaTextField(this.spreadsheetModel.getCellRawdata(
                        this.selectedCells.getStartRow(), this.selectedCells.getStartCol()));
            }
        } else {
            this.selectedCells = new SelectedCells(-1, -1, -1, -1);
        }
    }

    public int getSelectedRow() {
        return selectedCells.getStartRow();
    }

    public int getSelectedCol() {
        return selectedCells.getStartCol();
    }

    private boolean singleCellSelected(ISelectedCells selectedCells) {
        return selectedCells.getStartRow() == selectedCells.getEndRow() &&
                selectedCells.getStartCol() == selectedCells.getEndCol();
    }

    @Override
    public void openSheetLocally(String path) {
        try {
            this.homeView.disposeHomePage();
            this.spreadsheetModel = this.home.readXML(path);
            setCurrentSheet(new SheetView(spreadsheetModel));
        } catch (Exception e) {
            homeView.displayErrorBox(e.getMessage());
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
            this.spreadsheetModel = this.home.readPayload(fullPayload, selectedSheet);
            setCurrentSheet(new SheetView(spreadsheetModel));
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
            fullPayload = getUpdatesForPublishedResult.getValue().getLast().getPayload();
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
    public String handleReevaluatingCellFormula(int row, int col, String data) {
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