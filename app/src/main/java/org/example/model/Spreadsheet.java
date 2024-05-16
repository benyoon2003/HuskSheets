package org.example.model;

import java.util.ArrayList;

public class Spreadsheet {
  //2d Array list to represent spreadsheet;
  private ArrayList<ArrayList<Cell>> grid;


  public Spreadsheet(){

    ArrayList<Cell> columns = new ArrayList<Cell>();
    ArrayList<ArrayList<Cell>> temp_grid = new ArrayList<>();

    for(int i = 0; i < 100; i++){
      columns.add(new Cell());
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

}
