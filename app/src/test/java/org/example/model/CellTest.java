package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CellTest {

    @Test
    public void testDefaultConstructor() {
        Cell cell = new Cell();
        assertEquals("", cell.getValue(), "Default constructor should initialize value to an empty string");
    }

    @Test
    public void testConstructorWithValue() {
        String testValue = "TestValue";
        Cell cell = new Cell(testValue);
        assertEquals(testValue, cell.getValue(), "Constructor should initialize value to the provided string");

        String formula = "= 2 + 2";
        Cell cell2 = new Cell(formula);
        assertEquals(formula, cell2.getValue(), "Constructor should initialize value to the provided formula");
        assertEquals(formula, cell2.getFormula(), "Constructor should initialize formula to the provided formula");
    }

    @Test
    public void testConstructorWithFormula() {
        String formula = "=SUM(A1:A10)";
        Cell cell = new Cell(formula);
        assertEquals(formula, cell.getValue(), "Constructor should initialize value to the provided formula");
        assertEquals(formula, cell.getFormula(), "Constructor should initialize formula to the provided formula");
    }

    @Test
    public void testSetValue() {
        Cell cell = new Cell();
        
        assertEquals("", cell.getValue(), "Cell value should be empty");
        assertEquals("", cell.getFormula(), "Cell formula should be empty");

        String newValue = "= 2 + 2";
        cell.setValue(newValue);

        assertEquals(newValue, cell.getValue(), "setValue should update the value field");
        assertEquals(newValue, cell.getFormula(), "Cell formula should update the formula field");
    }

    @Test
    public void testGetValue() {
        Cell cell = new Cell("InitialValue");
        assertEquals("InitialValue", cell.getValue(), "getValue should return the current value of the cell");
        assertEquals("", cell.getFormula(), "Cell formula should be empty");
    }

    @Test
    public void testSetAndGetRow() {
        Cell cell = new Cell();
        cell.setRow(5);
        assertEquals(5, cell.getRow(), "getRow should return the row index that was set");
    }

    @Test
    public void testSetAndGetCol() {
        Cell cell = new Cell();
        cell.setCol(3);
        assertEquals(3, cell.getCol(), "getCol should return the column index that was set");
    }

    @Test
    public void testSetAndGetRawData() {
        Cell cell = new Cell();
        String rawData = "Raw data";
        cell.setRawData(rawData);
        assertEquals(rawData, cell.getRawdata(), "getRawdata should return the raw data that was set");
    }

    @Test
    public void testGetAndSetPositions() {
        Cell cell = new Cell("");
        cell.setRow(0);
        cell.setCol(1);

        assertEquals(0, cell.getRow());
        assertEquals(1, cell.getCol());
    }

    @Test
    public void testIsFormula() {
        Cell cell = new Cell("not a formula");
        assertFalse(cell.isFormula(), "Cell is not a formula, should return false");

        cell.setValue("= 2 + 2");
        assertTrue(cell.isFormula(), "Cell is now a formula, should return true");
    }

    @Test
    public void testSetFormula() {
        Cell cell = new Cell();
        assertEquals("", cell.getFormula(), "Cell formula should be empty");

        String formula = "=SUM(A1:A10)";
        cell.setFormula(formula);

        assertEquals(formula, cell.getFormula(), "setFormula should update the formula field");
    }
}
