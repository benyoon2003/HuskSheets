package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    }

    @Test
    public void testSetValue() {
        Cell cell = new Cell();
        String newValue = "NewValue";
        cell.setValue(newValue);
        assertEquals(newValue, cell.getValue(), "setValue should update the value field");
    }

    @Test
    public void testGetValue() {
        Cell cell = new Cell("InitialValue");
        assertEquals("InitialValue", cell.getValue(), "getValue should return the current value of the cell");
    }

    @Test
    public void testTokenize(){
        Cell cell = new Cell("4 + 7");
        String[] tokens = {"4", "+", "7"};
        List<String> tokenArrayList = Arrays.asList(tokens);
        assertEquals(tokenArrayList, cell.tokenize());

    }
}
