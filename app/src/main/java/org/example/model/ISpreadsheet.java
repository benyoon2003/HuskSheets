package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the interface for a spreadsheet.
 */
public interface ISpreadsheet extends IReadOnlySpreadSheet {

    /**
     * Gets a 2D ArrayList of Cell objects representing the cells in the spreadsheet.
     *
     * @return a 2D ArrayList of Cell objects representing the cells in the spreadsheet.
     */
    ArrayList<ArrayList<Cell>> getCells();

    /**
     * Evaluates the given formula and returns the result.
     *
     * @param formula the formula to evaluate.
     * @return the result of evaluating the formula.
     */
    String evaluateFormula(String formula);

    /**
     * Sets the value of the cell at the specified row and column.
     *
     * @param row   the row index of the cell.
     * @param col   the column index of the cell.
     * @param value the value to set.
     */
    void setCellValue(int row, int col, String value);

    /**
     * Gets the value of the cell at the specified row and column.
     *
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @return the value of the cell.
     */
    String getCellValue(int row, int col);

    /**
     * Gets the formula of the cell at the specified row and column.
     *
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @return the formula of the cell.
     */
    String getCellFormula(int row, int col);

    /**
     * Sets the raw data of the cell at the specified row and column.
     *
     * @param selRow the row index of the cell.
     * @param selCol the column index of the cell.
     * @param val    the raw data to set.
     */
    void setCellRawdata(int selRow, int selCol, String val);

    /**
     * Gets the raw data of the cell at the specified row and column.
     *
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @return the raw data of the cell.
     */
    String getCellRawdata(int row, int col);

    /**
     * Gets the name of the spreadsheet.
     *
     * @return the name of the spreadsheet.
     */
    String getName();


    public void addPublished(ISpreadsheet sheet);

    public void addSubscribed(ISpreadsheet sheet);

    public List<ISpreadsheet> getPublishedVersions();
}
