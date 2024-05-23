package org.example.model;

public class SelectedCells implements ISelectedCells {
  int startRow;
  int endRow;
  int startCol;
  int endCol;

  public SelectedCells(int startRow, int endRow,
                       int startCol, int endCol) {
    this.startRow = startRow;
    this.endRow = endRow;
    this.startCol = startCol;
    this.endCol = endCol;
  }

  public int getStartRow() {
    return startRow;
  }

  public int getEndRow() {
    return endRow;
  }

  public int getStartCol() {
    return startCol;
  }

  public int getEndCol() {
    return endCol;
  }
}
