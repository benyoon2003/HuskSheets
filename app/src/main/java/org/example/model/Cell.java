package org.example.model;

public class Cell {

  private String value;
  private int height;
  private int width;
  private int posx;
  private int posy;

  public Cell(){
    this.value = "";
    this.height = 30;
    this.width = 50;
  }

  public String getValue(){
    return this.value;
  }

  public int getHeight(){
    return this.height;
  }

  public int getWidth(){
    return this.width;
  }

  public int getPosx(){
    return this.posx;
  }

  public int getPosy(){
    return this.posy;
  }

}
