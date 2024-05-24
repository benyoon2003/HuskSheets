package org.example.model;

public class Cell {
    private String value;

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

    public boolean isFormula() {
        return this.value.startsWith("=");
    }
}
