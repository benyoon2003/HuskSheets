package org.example.model;

import java.util.ArrayList;

public interface ISpreadsheet extends ReadOnlySpreadSheet {
    ArrayList<ArrayList<Cell>> getCells();

    String evaluateFormula(String formula);
}
