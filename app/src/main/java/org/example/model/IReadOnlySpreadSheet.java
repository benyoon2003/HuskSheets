package org.example.model;

/**
 * Represents the interface for a read-only spreadsheet.
 */
public interface IReadOnlySpreadSheet {

    /**
     * Gets the number of rows in the spreadsheet.
     *
     * @return the number of rows in the spreadsheet.
     */
    int getRows();

    /**
     * Gets the number of columns in the spreadsheet.
     *
     * @return the number of columns in the spreadsheet.
     */
    int getCols();

    /**
     * Gets a 2D array of Cell objects representing the cells in the spreadsheet.
     *
     * @return a 2D array of Cell objects representing the cells in the spreadsheet.
     */
    Cell[][] getCellsObject();

    /**
     * Gets a 2D array of strings representing the values of cells in the spreadsheet.
     *
     * @return a 2D array of strings representing the values of cells in the spreadsheet.
     */
    String[][] getCellStringsObject();
}
