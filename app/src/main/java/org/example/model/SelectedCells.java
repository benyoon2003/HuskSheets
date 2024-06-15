package org.example.model;

/**
 * Represents a range of selected cells in a spreadsheet.
 */
public class SelectedCells implements ISelectedCells {
    // Starting row of the selection
    private int startRow;
    // Ending row of the selection
    private int endRow;
    // Starting column of the selection
    private int startCol;
    // Ending column of the selection
    private int endCol;

    /**
     * Constructs a SelectedCells object with the specified range.
     *
     * @param startRow the starting row of the selection.
     * @param endRow   the ending row of the selection.
     * @param startCol the starting column of the selection.
     * @param endCol   the ending column of the selection.
     */
    public SelectedCells(int startRow, int endRow, int startCol, int endCol) {
        this.startRow = startRow; 
        this.endRow = endRow;
        this.startCol = startCol;
        this.endCol = endCol;
    }

    /**
     * Gets the starting row of the selection.
     *
     * @return the starting row.
     * @author Ben
     */
    @Override
    public int getStartRow() {
        return startRow - 1; // Return the starting row index adjusted for 0-based indexing
    }

    /**
     * Gets the ending row of the selection.
     *
     * @return the ending row.
     * @author Ben
     */
    @Override
    public int getEndRow() {
        return endRow - 1; // Return the ending row index adjusted for 0-based indexing
    }

    /**
     * Gets the starting column of the selection.
     *
     * @return the starting column.
     * @author Ben
     */
    @Override
    public int getStartCol() {
        return startCol - 1; // Return the starting column index adjusted for 0-based indexing
    }

    /**
     * Gets the ending column of the selection.
     *
     * @return the ending column.
     * @author Ben
     */
    @Override
    public int getEndCol() {
        return endCol - 1; // Return the ending column index adjusted for 0-based indexing
    }
}
