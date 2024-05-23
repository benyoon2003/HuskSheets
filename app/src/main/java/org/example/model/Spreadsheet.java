package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Spreadsheet implements ISpreadsheet{
  //2d Array list to represent spreadsheet;
  private ArrayList<ArrayList<Cell>> grid;


  public Spreadsheet() {

    ArrayList<Cell> columns = new ArrayList<Cell>();
    ArrayList<ArrayList<Cell>> temp_grid = new ArrayList<>();

    for(int i = 0; i < 100; i++){
      columns.add(new Cell("test"));
    }
    for(int i = 0; i < 100; i++){

      temp_grid.add(columns);
    }

    this.grid = temp_grid;
  }

  public int getRows(){
    return this.grid.size();
  }

  public int getCols(){
    return this.grid.get(0).size();
  }

  public ArrayList<ArrayList<Cell>> getCells(){
    return this.grid;
  }

  public Cell[][] getCellsObject() {
    Cell[][] retObject = new Cell[this.getRows()][this.getCols()];
    for(int r = 0; r < this.getRows(); r++){
      ArrayList<Cell> row = this.grid.get(r);
      for(int c = 0; c < this.getCols(); c++){
        retObject[r][c] = row.get(c);
      }
    }
    return retObject;
  }


  public String[][] getCellStringsObject() {
    String[][] retObject = new String[this.getRows()][this.getCols()];
    for(int r = 0; r < this.getRows(); r++){
      ArrayList<Cell> row = this.grid.get(r);
      for(int c = 0; c < this.getCols(); c++){
        retObject[r][c] = row.get(c).getValue();
      }
    }
    return retObject;
  }

  //MAY NOT NEED TO BE IMPLEMENTED
//  public void performMedianCalc(ISelectedCells selectedCells) {
//    //System.out.println(grid.get(selectedCells.getStartRow()).get(selectedCells.getStartCol()).getValue());
//    List<Float> listOfSelectedCell = this.listOfSelectedCells(selectedCells);
//    Collections.sort(listOfSelectedCell);
//    float median;
//    int length = listOfSelectedCell.size();
//    System.out.println(length);
//    if (length % 2 == 0) {
//      median = (listOfSelectedCell.get(length / 2 - 1) + listOfSelectedCell.get(length / 2)) / 2.0f;
//    } else {
//      median = listOfSelectedCell.get(length / 2);
//    }
//    grid.get(selectedCells.getEndRow() + 1).set(
//            selectedCells.getEndCol(), new Cell("= " + median));
//    System.out.println(median);
//  }
//
//  public void performMeanCalc(ISelectedCells selectedCells) {
//
//
//  }
//
//  public void performModeCalc(ISelectedCells selectedCells) {
//
//
//  }

  private List<Float> listOfSelectedCells(ISelectedCells selectedCells) {
    ArrayList<Float> listOfSelectedFloat = new ArrayList<>();
    for (int row = selectedCells.getStartRow(); row <= selectedCells.getEndRow(); row++) {
      for (int col = selectedCells.getStartCol(); col <= selectedCells.getEndCol(); col++) {
        try {
          listOfSelectedFloat.add(Float.parseFloat(grid.get(row).get(col).getValue()));
        }
        catch (NullPointerException ignored) {
          listOfSelectedFloat.add((float) 0);
        }
        catch (NumberFormatException ignored) {
          try {
            listOfSelectedFloat.add((float) Integer.parseInt(grid.get(row).get(col).getValue()));
          }
          catch (NumberFormatException ign) {
            throw new IllegalArgumentException("Cannot perform statistical calculation on String");
          }
        }
      }
    }
    return listOfSelectedFloat;
  }


}
