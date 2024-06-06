package org.example.view;

import org.example.controller.IUserController;

/**
 * Interface representing the sheet view in the application.
 * Provides methods for interaction between the view and the user controller.
 */
public interface ISheetView {

    public void setup();

    /**
     * Adds a controller to the sheet view.
     *
     * @param controller the IUserController instance to add.
     */
    void addController(IUserController controller);

    /**
     * Makes the sheet view visible.
     */
    void makeVisible();

    /**
     * Displays a message in the sheet view.
     *
     * @param s the message to display.
     */
    void displayMessage(String s);
    
    /**
     * Updates the table in the sheet view.
     */
    void updateTable();

    /**
     * Changes the formula text field to display the given raw data.
     *
     * @param rawdata the raw data to display in the formula text field.
     */
    void changeFormulaTextField(String rawdata);
    
    /**
     * Gets the Excel-style column name for the given column number.
     *
     * @param columnNumber the column number to convert to an Excel-style column name.
     * @return the Excel-style column name.
     */
    String getExcelColumnName(int columnNumber);

    public void loadChanges();
}
