package org.example.model;

public class Cell {
    private String value;

    private String rawdata;
    private int row;
    private int col;

    public Cell() {
        this.value = "";
        this.rawdata = "";
    }

    public Cell(String value) {
        super();
        this.rawdata = "";
        this.value = value;
    }

    public void setRawData(String rawdata) {
        this.rawdata = rawdata;
    }

    public String getValue() {
        return this.value;
    }

    public String getRawdata() {
        return this.rawdata;
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
