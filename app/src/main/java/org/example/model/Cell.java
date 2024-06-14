package org.example.model;

/**
 * Represents a cell in a spreadsheet.
 */
public class Cell {
    private String value; // Stores the cell's value
    private String formula; // Stores the cell's formula
    private String rawdata; // Stores the cell's raw data
    private int row; // Stores the cell's row index
    private int col; // Stores the cell's column index
 
    /**
     * Default constructor that initializes a cell with empty values.
     */
    public Cell() {
        this.value = ""; // Initialize value to an empty string
        this.rawdata = ""; // Initialize raw data to an empty string
        this.formula = ""; // Initialize formula to an empty string
    }

    /**
     * Constructor that initializes a cell with a specified value.
     *
     * @param value the value to set for the cell.
     * @author Vinay
     */
    public Cell(String value) {
        this(); // Call the default constructor to initialize empty values
        this.value = value; // Set the cell's value to the specified value
        if (value.startsWith("=")) { // Check if the value starts with "=" indicating a formula
            this.formula = value; // Set the formula if the value starts with "="
        }
    }

    /**
     * Sets the raw data of the cell.
     *
     * @param rawdata the raw data to set for the cell.
     * @author Ben
     */
    public void setRawData(String rawdata) {
        this.rawdata = rawdata; // Set the raw data for the cell
    }

    /**
     * Returns the value of the cell.
     *
     * @return the value of the cell.
     * @author Tony
     */
    public String getValue() {
        return this.value; // Return the cell's value
    }

    /**
     * Returns the raw data of the cell.
     *
     * @return the raw data of the cell.
     * @author Ben
     */
    public String getRawdata() {
        return this.rawdata; // Return the cell's raw data
    }

    /**
     * Sets the value of the cell.
     *
     * @param value the value to set for the cell.
     * @author Vinay
     */
    public void setValue(String value) {
        this.value = value; // Set the cell's value to the specified value
        if (value.startsWith("=")) { // Check if the value starts with "=" indicating a formula
            this.formula = value; // Set the formula if the value starts with "="
        } else {
            this.formula = ""; // Clear the formula if the value does not start with "="
        }
    }

    /**
     * Returns the row index of the cell.
     *
     * @return the row index of the cell.
     * @author Tony
     */
    public int getRow() {
        return this.row; // Return the cell's row index
    }

    /**
     * Sets the row index of the cell.
     *
     * @param row the row index to set for the cell.
     * @author Theo
     */
    public void setRow(int row) {
        this.row = row; // Set the cell's row index to the specified value
    }

    /**
     * Returns the column index of the cell.
     *
     * @return the column index of the cell.
     * @author Tony
     */
    public int getCol() {
        return this.col; // Return the cell's column index
    }

    /**
     * Sets the column index of the cell.
     *
     * @param col the column index to set for the cell.
     * @author Theo
     */
    public void setCol(int col) {
        this.col = col; // Set the cell's column index to the specified value
    }

    /**
     * Returns the formula of the cell.
     *
     * @return the formula of the cell.
     * @author Vinay
     */
    public String getFormula() {
        return this.formula; // Return the cell's formula
    }

    /**
     * Sets the formula of the cell.
     *
     * @param formula the formula to set for the cell.
     * @author Vinay
     */
    public void setFormula(String formula) {
        this.formula = formula; // Set the cell's formula to the specified value
    }

    /**
     * Checks if the cell contains a formula.
     *
     * @return true if the cell contains a formula, false otherwise.
     * @author Vinay
     */
    public boolean isFormula() {
        return this.value.startsWith("="); // Return true if the cell's value starts with "="
    }
}
