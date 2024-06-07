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

    /**
     * Get the id of the sheet
     * @return the id as an int
     */
    public int getId_version();

    /**
     * Gets the name of the spreadsheet.
     *
     * @return the name of the spreadsheet.
     */
    String getName();

    /**
     * Gets the raw data of a cell in the spreadsheet.
     *
     * @return the raw data of a particular cell at a row and column specified value.
     */
    String getCellRawdata(int row, int col);

}
