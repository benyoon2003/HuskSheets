package org.example.model;

public class Cell {
    private String value;
    private int row;
    private int col;

    public Cell() {
        this.value = "";
    }

    public Cell(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    // checks whether user is typing in a formula in a cell
    public boolean isFormula() {
        return this.value.startsWith("=");
    }
}
