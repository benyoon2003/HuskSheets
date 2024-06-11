package org.example.model;

/**
 * Represents a cell in a spreadsheet.
 */
public class Cell {
    private String value;
    private String formula;
    private String rawdata;
    private int row;
    private int col;

    /**
     * Default constructor that initializes a cell with empty values.
     */
    public Cell() {
        this.value = "";
        this.rawdata = "";
        this.formula = "";
    }

    /**
     * Constructor that initializes a cell with a specified value.
     *
     * @param value the value to set for the cell.
     * @author Vinay
     */
    public Cell(String value) {
        this();
        this.value = value;
        if (value.startsWith("=")) {
            this.formula = value;
        }
    }

    /**
     * Sets the raw data of the cell.
     *
     * @param rawdata the raw data to set for the cell.
     * @author Ben
     */
    public void setRawData(String rawdata) {
        this.rawdata = rawdata;
    }

    /**
     * Returns the value of the cell.
     *
     * @return the value of the cell.
     * @author Tony
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Returns the raw data of the cell.
     *
     * @return the raw data of the cell.
     * @author Ben
     */
    public String getRawdata() {
        return this.rawdata;
    }

    /**
     * Sets the value of the cell.
     *
     * @param value the value to set for the cell.
     * @author Vinay
     */
    public void setValue(String value) {
        this.value = value;
        if (value.startsWith("=")) {
            this.formula = value;
        } else {
            this.formula = "";
        }
    }

    /**
     * Returns the row index of the cell.
     *
     * @return the row index of the cell.
     * @author Tony
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Sets the row index of the cell.
     *
     * @param row the row index to set for the cell.
     * @author Theo
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Returns the column index of the cell.
     *
     * @return the column index of the cell.
     * @author Tony
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Sets the column index of the cell.
     *
     * @param col the column index to set for the cell.
     * @author Theo
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Returns the formula of the cell.
     *
     * @return the formula of the cell.
     * @author Vinay
     */
    public String getFormula() {
        return this.formula;
    }

    /**
     * Sets the formula of the cell.
     *
     * @param formula the formula to set for the cell.
     * @author Vinay
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     * Checks if the cell contains a formula.
     *
     * @return true if the cell contains a formula, false otherwise.
     * @author Vinay
     */
    public boolean isFormula() {
        return this.value.startsWith("=");
    }
}
