package org.example.model;

public interface IReadOnlySpreadSheet {

    int getRows();

    int getCols();

    Cell[][] getCellsObject();

    String[][] getCellStringsObject();
}
