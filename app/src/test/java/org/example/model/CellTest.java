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
}
