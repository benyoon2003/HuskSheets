package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CellTest {

    @Test
    public void testDefaultConstructor() {
        ICell cell = new Cell();
        assertEquals("", cell.getValue(), "Default constructor should initialize value to an empty string");
    }

    @Test
    public void testConstructorWithValue() {
        String testValue = "TestValue";
        ICell cell = new Cell(testValue);
        assertEquals(testValue, cell.getValue(), "Constructor should initialize value to the provided string");
    }

    @Test
    public void testSetValue() {
        ICell cell = new Cell();
        String newValue = "NewValue";
        cell.setValue(newValue);
        assertEquals(newValue, cell.getValue(), "setValue should update the value field");
    }

    @Test
    public void testGetValue() {
        ICell cell = new Cell("InitialValue");
        assertEquals("InitialValue", cell.getValue(), "getValue should return the current value of the cell");
    }
}
