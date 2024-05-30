package org.example.model;

import java.util.ArrayList;

public interface ISpreadsheet {
    int getRows();
    int getCols();
    ArrayList<ArrayList<Cell>> getCells();
    Cell[][] getCellsObject();
    String[][] getCellStringsObject();
    String evaluateFormula(String formula);
    void setCellValue(int row, int col, String value);
    String getCellValue(int row, int col);
    String getCellFormula(int row, int col); // Add this line

    void setCellRawdata(int selRow, int selCol, String val);

    String getCellRawdata(int row, int col);
}
