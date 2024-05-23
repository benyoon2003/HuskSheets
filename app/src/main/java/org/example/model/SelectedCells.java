package org.example.model;

public class SelectedCells {
  int startRowCoordinate;
  int endRowCoordinate;
  int startColCoordinate;
  int getEndColCoordinate;

  public SelectedCells(int startRowCoordinate, int endRowCoordinate,
                       int startColCoordinate, int endColCoordinate) {
    this.startRowCoordinate = startRowCoordinate;
    this.endRowCoordinate = endRowCoordinate;
    this.startColCoordinate = startColCoordinate;
    this.getEndColCoordinate = endColCoordinate;
  }
}
