package org.example.model;

import java.util.ArrayList;

public interface ISpreadsheet extends ReadOnlySpreadSheet{

  ArrayList<ArrayList<Cell>> getCells();


//  void performMedianCalc(ISelectedCells selectedCells);
//
//  void performModeCalc(ISelectedCells selectedCells);
//
//  void performMeanCalc(ISelectedCells selectedCells);

}
