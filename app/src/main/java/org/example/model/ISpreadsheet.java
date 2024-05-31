package org.example.model;

import java.util.ArrayList;

public interface ISpreadsheet extends IReadOnlySpreadSheet {
    ArrayList<ArrayList<Cell>> getCells();
    String evaluateFormula(String formula);
    void setCellValue(int row, int col, String value);
    String getCellValue(int row, int col);
    String getCellFormula(int row, int col); // Add this line
    void setCellRawdata(int selRow, int selCol, String val);
    String getCellRawdata(int row, int col);

    String getName();
}
