package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the methods within the Cell class.
 */
public class CellTest {

    /**
     * Tests the default constructor of the Cell class.
     */
    @Test
    public void testDefaultConstructor() {
        Cell cell = new Cell(); // Creates a new Cell instance using the default constructor.
        assertEquals("", cell.getValue(), "Default constructor should initialize value to an empty string");
    }

    /**
     * Tests the constructor that takes a value as a parameter.
     */
    @Test
    public void testConstructorWithValue() {
        String testValue = "TestValue"; // Declares a test value string.
        Cell cell = new Cell(testValue); // Creates a new Cell instance with the test value.
        assertEquals(testValue, cell.getValue(), "Constructor should initialize value to the provided string");

        String formula = "= 2 + 2"; // Declares a test formula string.
        Cell cell2 = new Cell(formula); // Creates a new Cell instance with the test formula.
        assertEquals(formula, cell2.getValue(), "Constructor should initialize value to the provided formula"); // Verifies that the value is set correctly for a formula.
        assertEquals(formula, cell2.getFormula(), "Constructor should initialize formula to the provided formula"); // Verifies that the formula is set correctly.
    }

    /**
     * Tests the constructor that takes a formula as a parameter.
     */
    @Test
    public void testConstructorWithFormula() {
        String formula = "=SUM(A1:A10)"; // Declares a test formula string.
        Cell cell = new Cell(formula); // Creates a new Cell instance with the test formula.
        assertEquals(formula, cell.getValue(), "Constructor should initialize value to the provided formula"); // Verifies that the value is set correctly for the formula.
        assertEquals(formula, cell.getFormula(), "Constructor should initialize formula to the provided formula"); // Verifies that the formula is set correctly.
    }

    /**
     * Tests the setValue method of the Cell class.
     */
    @Test
    public void testSetValue() {
        Cell cell = new Cell(); // Creates a new Cell instance using the default constructor.
        
        assertEquals("", cell.getValue(), "Cell value should be empty"); // Verifies that the value is initially empty.
        assertEquals("", cell.getFormula(), "Cell formula should be empty"); // Verifies that the formula is initially empty.

        String newValue = "= 2 + 2"; // Declares a new value string.
        cell.setValue(newValue); // Sets the value of the cell.

        assertEquals(newValue, cell.getValue(), "setValue should update the value field"); // Verifies that the value is updated correctly.
        assertEquals(newValue, cell.getFormula(), "Cell formula should update the formula field"); // Verifies that the formula is updated correctly.
    }

    /**
     * Tests the getValue method of the Cell class.
     */
    @Test
    public void testGetValue() {
        Cell cell = new Cell("InitialValue"); // Creates a new Cell instance with an initial value.
        assertEquals("InitialValue", cell.getValue(), "getValue should return the current value of the cell"); // Verifies that the value is returned correctly.
        assertEquals("", cell.getFormula(), "Cell formula should be empty"); // Verifies that the formula is initially empty.
    }

    /**
     * Tests the setRow and getRow methods of the Cell class.
     */
    @Test
    public void testSetAndGetRow() {
        Cell cell = new Cell(); // Creates a new Cell instance using the default constructor.
        cell.setRow(5); // Sets the row index of the cell.
        assertEquals(5, cell.getRow(), "getRow should return the row index that was set"); // Verifies that the row index is returned correctly.
    }

    /**
     * Tests the setCol and getCol methods of the Cell class.
     */
    @Test
    public void testSetAndGetCol() {
        Cell cell = new Cell(); // Creates a new Cell instance using the default constructor.
        cell.setCol(3); // Sets the column index of the cell.
        assertEquals(3, cell.getCol(), "getCol should return the column index that was set"); // Verifies that the column index is returned correctly.
    }

    /**
     * Tests the setRawData and getRawdata methods of the Cell class.
     */
    @Test
    public void testSetAndGetRawData() {
        Cell cell = new Cell(); // Creates a new Cell instance using the default constructor.
        String rawData = "Raw data"; // Declares a raw data string.
        cell.setRawData(rawData); // Sets the raw data of the cell.
        assertEquals(rawData, cell.getRawdata(), "getRawdata should return the raw data that was set"); // Verifies that the raw data is returned correctly.
    }

    /**
     * Tests the setRow, setCol, getRow, and getCol methods of the Cell class.
     */
    @Test
    public void testGetAndSetPositions() {
        Cell cell = new Cell(""); // Creates a new Cell instance with an empty value.
        cell.setRow(0); // Sets the row index of the cell.
        cell.setCol(1); // Sets the column index of the cell.

        assertEquals(0, cell.getRow()); // Verifies that the row index is returned correctly.
        assertEquals(1, cell.getCol()); // Verifies that the column index is returned correctly.
    }

    /**
     * Tests the isFormula method of the Cell class.
     */
    @Test
    public void testIsFormula() {
        Cell cell = new Cell("not a formula"); // Creates a new Cell instance with a non-formula value.
        assertFalse(cell.isFormula(), "Cell is not a formula, should return false"); // Verifies that the cell is not identified as a formula.

        cell.setValue("= 2 + 2"); // Sets the value of the cell to a formula.
        assertTrue(cell.isFormula(), "Cell is now a formula, should return true"); // Verifies that the cell is identified as a formula.
    }
    
    /**
     * Tests the setFormula and getFormula methods of the Cell class.
     */
    @Test
    public void testSetFormula() {
        Cell cell = new Cell(); // Creates a new Cell instance using the default constructor.
        assertEquals("", cell.getFormula(), "Cell formula should be empty"); // Verifies that the formula is initially empty.

        String formula = "=SUM(A1:A10)"; // Declares a formula string.
        cell.setFormula(formula); // Sets the formula of the cell.

        assertEquals(formula, cell.getFormula(), "setFormula should update the formula field"); // Verifies that the formula is updated correctly.
    }
}
