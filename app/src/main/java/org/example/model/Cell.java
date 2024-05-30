package org.example.model;

public class Cell {
    private String value;
    private String formula;

    private String rawdata;
    private int row;
    private int col;

    public Cell() {
        this.value = "";
        this.rawdata = "";
        this.formula = "";
    }

    public Cell(String value) {
        this();
        this.rawdata = "";
        this.value = value;
        if (value.startsWith("=")) {
            this.formula = value;
        }
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
        if (value.startsWith("=")) {
            this.formula = value;
        } else {
            this.formula = "";
        }
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

    public String getFormula() {
        return this.formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public boolean isFormula() {
        return this.value.startsWith("=");
    }
}
