package org.example.view;

import java.awt.Color;

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
     * @author Vinay
     */
    void addController(IUserController controller);

    /**
     * Makes the sheet view visible.
     * 
     * @author Theo
     */
    void makeVisible();

    /**
     * Displays a message in the sheet view.
     *
     * @param s the message to display.
     * @author Vinay
     */
    void displayMessage(String s);
    
    /**
     * Updates the table in the sheet view.
     * 
     * @author Vinay
     */
    void updateTable();

    /**
     * Changes the formula text field to display the given raw data.
     *
     * @param rawdata the raw data to display in the formula text field.
     * 
     * @author Ben
     */
    void changeFormulaTextField(String rawdata);
    
    /**
     * Gets the Excel-style column name for the given column number.
     *
     * @param columnNumber the column number to convert to an Excel-style column name.
     * @return the Excel-style column name.
     * @author Vinay
     */
    String getExcelColumnName(int columnNumber);

   /**
     * Loads changes into the SheetView.
     *
     * @throws Exception if an error occurs while loading changes.
     * @author Theo
     */
    public void loadChanges() throws Exception;

    /**
     * Highlights a cell with the specified color.
     *
     * @param row the row of the cell.
     * @param col the column of the cell.
     * @param color the color to highlight.
     * @author Vinay
     */
    public void highlightCell(int row, int col, Color color);
    
    /**
     * Handles the selection of cells in the table.
     *
     * @param selectedRows the selected rows.
     * @param selectedColumns the selected columns.
     * @author Vinay
     */
    void selectedCells(int[] selectedRows, int[] selectedColumns);

}
