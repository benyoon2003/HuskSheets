package org.example.model;

import java.util.ArrayList;

public class Spreadsheet {
  //2d Array list to represent spreadsheet;
  private ArrayList<ArrayList<Cell>> grid;


  public Spreadsheet(){

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
}
