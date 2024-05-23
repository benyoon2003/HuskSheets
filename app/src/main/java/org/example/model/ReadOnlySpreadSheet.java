package org.example.model;

public interface ReadOnlySpreadSheet {

  int getRows();

  int getCols();

  String[][] getCellStringsObject();
  Cell[][] getCellsObject();
}
