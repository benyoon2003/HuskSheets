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
   */
  @Override
  public int getStartRow() {
    return startRow;
  }

  /**
   * Gets the ending row of the selection.
   *
   * @return the ending row.
   */
  @Override
  public int getEndRow() {
    return endRow;
  }

  /**
   * Gets the starting column of the selection.
   *
   * @return the starting column.
   */
  @Override
  public int getStartCol() {
    return startCol;
  }

  /**
   * Gets the ending column of the selection.
   *
   * @return the ending column.
   */
  @Override
  public int getEndCol() {
    return endCol;
  }
}
