package org.example.controller;

import java.util.List;
import org.example.model.IReadOnlySpreadSheet;
import org.example.view.IHomeView;
import org.example.view.ISheetView;

/**
 * IUserController interface defines the contract for user-related operations
 * in the application, including user authentication, sheet management, and 
 * handling various user actions such as cell selection, toolbar commands, 
 * and statistical calculations. Implementing classes should provide the 
 * logic for these operations.
 */
public interface IUserController {

    /**
     * Sets the current sheet view.
     * @param sheetView the sheet view to set as current.
     */
    void setCurrentSheet(ISheetView sheetView);

    /**
     * Opens the home view.
     */
    void openHomeView();

    /**
     * Gets the current sheet view.
     * @return the current sheet view.
     */
    ISheetView getCurrentSheet();

    /**
     * Creates a new sheet.
     * @param name the name of the new sheet.
     */
    void createNewSheet(String name);

    /**
     * Saves the given sheet to the specified path.
     * @param sheet the sheet to save.
     * @param path the path to save the sheet to.
     */
    void saveSheetLocally(IReadOnlySpreadSheet sheet, String path);

    /**
     * Handles toolbar actions.
     * @param command the command to handle.
     */
    void handleToolbar(String command);

    /**
     * Handles cell selection.
     * @param selectedRows the selected rows.
     * @param selectedColumns the selected columns.
     */
    void selectedCells(int[] selectedRows, int[] selectedColumns);

    /**
     * Opens a sheet from the specified path.
     * @param path the path to open the sheet from.
     */
    void openSheet(String path);

    /**
     * Gets the list of saved sheets.
     * @return the list of saved sheets.
     */
    List<String> getSavedSheets();

    /**
     * Deletes a sheet at the specified path.
     * @param path the path to delete the sheet from.
     */
    void deleteSheet(String path);

    /**
     * Gets the home view.
     * @return the home view.
     */
    IHomeView getHomeView();

    /**
     * Changes the value of a cell in the spreadsheet at the specified row and column.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     * @param val the value to set.
     */
    void changeSpreadSheetValueAt(int selRow, int selCol, String val);

    /**
     * Evaluates a formula.
     * @param formula the formula to evaluate.
     * @return the result of the formula evaluation.
     */
    String evaluateFormula(String formula);

    /**
     * Cuts the content of a cell.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     */
    void cutCell(int selRow, int selCol);

    /**
     * Copies the content of a cell.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     */
    void copyCell(int selRow, int selCol);

    /**
     * Pastes the content into a cell.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     */
    void pasteCell(int selRow, int selCol);

    /**
     * Calculates the percentile of a cell value.
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     */
    void getPercentile(int selRow, int selCol);

    /**
     * Saves the sheet to the server.
     * @param sheet the sheet to save.
     * @param name the name of the sheet.
     */
    void saveSheetToServer(IReadOnlySpreadSheet sheet, String name);

    /**
     * Deletes a sheet from the server.
     * @param name the name of the sheet.
     */
    void deleteSheetFromServer(String name);

    /**
     * Handles referencing of a cell.
     * @param row the row of the cell.
     * @param col the column of the cell.
     * @param data the data in the cell.
     * @return the result of the referencing.
     */
    String handleReferencingCell(int row, int col, String data);

    /**
     * Gets the list of sheets from the server.
     * @return the list of server sheets.
     */
    List<String> getServerSheets();

    /**
     * Opens a sheet from the server.
     * @param selectedSheet the name of the sheet to open.
     */
    void openServerSheet(String selectedSheet);

    /**
     * Gets the zero-indexed selected row.
     * @return the zero-indexed selected row.
     */
    int getSelectedRowZeroIndex();

    /**
     * Gets the zero-indexed selected column.
     * @return the zero-indexed selected column.
     */
    int getSelectedColZeroIndex();

    /**
     * Gets the formula of a cell.
     * @param row the row of the cell.
     * @param col the column of the cell.
     * @return the formula of the cell.
     */
    String getFormula(int row, int col);

    /**
     * Registers a new user with the provided username and password.
     * @param username the username of the new user.
     * @param password the password of the new user.
     */
    void registerUser(String username, String password);

    /**
     * Logs in a user with the provided username and password.
     * @param username the username of the user.
     * @param password the password of the user.
     */
    void loginUser(String username, String password);

    /**
     * Gets the list of publishers.
     * @return the list of publishers.
     */
    List<String> getPublishers();

    /**
     * Gets the list of subscribed sheets for the given publisher.
     * @param publisher the publisher of the sheets.
     * @return the list of subscribed sheets.
     */
    List<String> getSubscribedSheets(String publisher);

    /**
     * Applies conditional formatting to the spreadsheet.
     */
    void applyConditionalFormatting();

    /**
     * Opens a subscriber sheet from the specified publisher.
     * @param selectedSheet the name of the sheet to open.
     * @param publisher the publisher of the sheet.
     */
    void openSubscriberSheet(String selectedSheet, String publisher);

    /**
     * Updates the subscribed sheet on the server.
     * @param publisher the publisher of the sheet.
     * @param sheet the sheet to update.
     * @param name the name of the sheet.
     */
    void updateSubscribedSheet(String publisher, IReadOnlySpreadSheet sheet, String name);

    /**
     * Updates the selected cells with the given value.
     * @param value the value to update the selected cells with.
     */
    void updateSelectedCells(String value);

    /**
     * Gets all subscriber updates since the specified id.
     * @param sheet name of the sheet.
     * @param id version of the sheet.
     * @throws Exception if an error occurs while fetching updates.
     */
    void getUpdatesForPublished(String sheet, int id) throws Exception;
}
