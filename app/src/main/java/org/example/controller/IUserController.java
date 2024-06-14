package org.example.controller;

import java.util.List;

import org.example.model.IAppUser;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
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
     *
     * @param sheetView the sheet view to set as current.
     * @author Vinay
     */
    void setCurrentSheet(ISheetView sheetView);
    public ISpreadsheet getSpreadsheetModel();
    /**
     * Opens the home view.
     *
     * @author Vinay
     */
    void openHomeView();

    /**
     * Creates a new sheet with the given name.
     *
     * @param name the name of the new sheet.
     * @author Tony
     */
    void createNewServerSheet(String name);

    /**
     * Saves the given sheet to the specified path.
     *
     * @param sheet the sheet to save.
     * @param path  the path to save the sheet to.
     * @author Theo
     */
    void saveSheetLocally(IReadOnlySpreadSheet sheet, String path);

    /**
     * Sets the selectedCells with the given array of rows and columns.
     *
     * @param selectedRows    the selected rows.
     * @param selectedColumns the selected columns.
     * @author Vinay
     */
    void setSelectedCells(int[] selectedRows, int[] selectedColumns);

    /**
     * Opens a sheet from the specified path locally.
     *
     * @param path the path to open the sheet from.
     * @author Theo
     */
    void openSheetLocally(String path);

    /**
     * Gets the list of saved sheets.
     *
     * @return the list of saved sheets.
     * @author Vinay
     */
    List<String> getSavedSheetsLocally();

    /**
     * Deletes a sheet locally at the specified path.
     *
     * @param path the path to delete the sheet from.
     * @author Vinay
     */
    void deleteSheetLocally(String path);

    /**
     * Gets the home view.
     *
     * @return the IHomeView.
     * @author Ben
     */
    IHomeView getHomeView();

    /**
     * Changes the value of a cell in the spreadsheet at the specified row and column.
     *
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     * @param val    the value to set.
     * @author Ben
     */
    void changeSpreadSheetValueAt(int selRow, int selCol, String val);

    /**
     * Cuts the content of a cell.
     *
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     * @author Vinay
     */
    void cutCell(int selRow, int selCol);

    /**
     * Copies the content of a cell.
     *
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     * @author Vinay
     */
    void copyCell(int selRow, int selCol);

    /**
     * Pastes the content into a cell.
     *
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     * @author Vinay
     */
    void pasteCell(int selRow, int selCol);

    /**
     * Calculates the percentile of a cell value.
     *
     * @param selRow the row of the cell.
     * @param selCol the column of the cell.
     * @author Theo
     */
    void getPercentile(int selRow, int selCol);

    /**
     * Saves the sheet to the server.
     *
     * @param sheet     the sheet to save.
     * @param sheetName the name of the sheet.
     * @author Tony
     */
    void saveSheetToServer(IReadOnlySpreadSheet sheet, String sheetName);

    /**
     * Deletes a sheet from the server.
     *
     * @param name the name of the sheet.
     * @author Tony
     */
    void deleteSheetFromServer(String name);

    /**
     * Handles reevaluating the formula within the cell.
     *
     * @param row  the row of the cell.
     * @param col  the column of the cell.
     * @param data the data in the cell.
     * @return the result of the formula.
     * @author Ben
     */
    String handleReevaluatingCellFormula(int row, int col, String data);

    /**
     * Gets the list of sheets from the server for the current user.
     *
     * @return the list of server sheets.
     * @author Tony
     */
    List<String> getAppUserSheets();

    /**
     * Opens a sheet from the server.
     *
     * @param selectedSheet the name of the sheet to open.
     * @author Tony
     */
    void openServerSheet(String selectedSheet);

    /**
     * Gets the zero-indexed selected start row.
     *
     * @return the zero-indexed selected row.
     * @author Vinay
     */
    int getSelectedStartRow();

    /**
     * Gets the zero-indexed selected start column.
     *
     * @return the zero-indexed selected column.
     * @author Vinay
     */
    int getSelectedStartCol();

    /**
     * Gets the zero-indexed selected end row.
     *
     * @return the zero-indexed selected row.
     * @author Ben
     */
    int getSelectedEndRow();

    /**
     * Gets the zero-indexed selected end column.
     *
     * @return the zero-indexed selected column.
     * @author Ben
     */
    int getSelectedEndCol();

    /**
     * Getter for the clipboard content.
     *
     * @return clipboard content
     */
    String getClipboardContent();

    /**
     * Registers a new user with the provided username and password.
     *
     * @param username the username of the new user.
     * @param password the password of the new user.
     * @author Ben
     */
    void registerUser(String username, String password) throws Exception;

    /**
     * Logs in a user with the provided username and password.
     *
     * @param username the username of the user.
     * @param password the password of the user.
     * @author Ben
     */
    void loginUser(String username, String password) throws Exception;

    /**
     * Gets the list of publishers.
     *
     * @return the list of publishers.
     * @author Tony
     */
    List<String> getPublishersFromServer();

    /**
     * Gets the list of sheets for the given publisher.
     *
     * @param publisher the publisher of the sheets.
     * @return the list of subscribed sheets.
     * @author Ben
     */
    List<String> accessSheetsFromUser(String publisher);

    /**
     * Applies conditional formatting to the spreadsheet.
     *
     * @author Vinay
     */
    void applyConditionalFormatting();

    /**
     * Opens a subscriber sheet from the specified publisher.
     *
     * @param selectedSheet the name of the sheet to open.
     * @param publisher     the publisher of the sheet.
     * @author Tony
     */
    void openSubscriberSheet(String selectedSheet, String publisher);

    /**
     * Updates the subscribed sheet on the server.
     *
     * @param publisher the publisher of the sheet.
     * @param sheet     the sheet to update.
     * @param name      the name of the sheet.
     * @author Tony
     */
    void updateSubscribedSheet(String publisher, IReadOnlySpreadSheet sheet, String name);

    /**
     * Updates the selected cells with the given value.
     *
     * @param value the value to update the selected cells with.
     * @author Vinay
     */
    void updateSelectedCells(String value);

    /**
     * Gets all subscriber updates since the specified id.
     *
     * @param sheet name of the sheet.
     * @param id    version of the sheet.
     * @throws Exception if an error occurs while fetching updates.
     * @author Ben
     */
    void getUpdatesForPublished(String sheet, int id) throws Exception;


    /**
     * Gets all publisher updates since the specified id.
     *
     * @param sheet name of the sheet.
     * @param id    version of the sheet.
     * @throws Exception if an error occurs while fetching updates.
     * @author Ben
     */
    void getUpdatesForSubscribed(String sheet, int id) throws Exception;

    /**
     * @return the current app user
     * @author Ben
     */
    IAppUser getAppUser();

    /**
     * Getter for isCutOperation.
     * @return boolean
     */
    boolean isCutOperation();
}
