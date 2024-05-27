package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class SpreadsheetTest {

    private Spreadsheet spreadsheet;

    @BeforeEach
    public void setUp() {
        spreadsheet = new Spreadsheet();
    }

    @Test
    public void testConstructor() {
        assertEquals(100, spreadsheet.getRows(), "Constructor should initialize 100 rows");
        assertEquals(100, spreadsheet.getCols(), "Constructor should initialize 100 columns");

        ArrayList<ArrayList<ICell>> cells = spreadsheet.getCells();
        for (ArrayList<ICell> row : cells) {
            for (ICell cell : row) {
                assertEquals("test", cell.getValue(), "Constructor should initialize all cells with value 'test'");
            }
        }
    }

    @Test
    public void testGetRows() {
        assertEquals(100, spreadsheet.getRows(), "getRows should return the correct number of rows");
    }

    @Test
    public void testGetCols() {
        assertEquals(100, spreadsheet.getCols(), "getCols should return the correct number of columns");
    }

    @Test
    public void testGetCells() {
        ArrayList<ArrayList<ICell>> cells = spreadsheet.getCells();
        assertEquals(100, cells.size(), "getCells should return 100 rows");

        for (ArrayList<ICell> row : cells) {
            assertEquals(100, row.size(), "Each row should have 100 columns");
        }
    }

    @Test
    public void testGetCellsObject() {
        ICell[][] cellsObject = spreadsheet.getCellsObject();
        assertEquals(100, cellsObject.length, "getCellsObject should return a 2D array with 100 rows");
        assertEquals(100, cellsObject[0].length, "Each row in the 2D array should have 100 columns");

        for (int r = 0; r < 100; r++) {
            for (int c = 0; c < 100; c++) {
                assertEquals("test", cellsObject[r][c].getValue(), "Each cell in the 2D array should have value 'test'");
            }
        }
    }

    @Test
    public void testGetCellStringsObject() {
        String[][] cellStringsObject = spreadsheet.getCellStringsObject();
        assertEquals(100, cellStringsObject.length, "getCellStringsObject should return a 2D array with 100 rows");
        assertEquals(100, cellStringsObject[0].length, "Each row in the 2D array should have 100 columns");

        for (int r = 0; r < 100; r++) {
            for (int c = 0; c < 100; c++) {
                assertEquals("test", cellStringsObject[r][c], "Each cell in the 2D array should have value 'test'");
            }
        }
    }
}
