package org.example.model;

/**
 * Represents the interface for selected cells in a spreadsheet.
 */
public interface ISelectedCells {

    /**
     * Gets the starting row index of the selected cells.
     *
     * @return the starting row index of the selected cells.
     * @author Ben
     */
    int getStartRow();

    /**
     * Gets the ending row index of the selected cells.
     *
     * @return the ending row index of the selected cells.
     * @author Ben
     */
    int getEndRow();

    /**
     * Gets the starting column index of the selected cells.
     *
     * @return the starting column index of the selected cells.
     * @author Ben
     */
    int getStartCol();

    /**
     * Gets the ending column index of the selected cells.
     *
     * @return the ending column index of the selected cells.
     * @author Ben
     */
    int getEndCol();
}
