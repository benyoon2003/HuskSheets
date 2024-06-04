package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class SpreadsheetTest {

    private Spreadsheet spreadsheet;

    @BeforeEach
    public void setUp() {
        spreadsheet = new Spreadsheet("Test");
    }

    @Test
    public void testConstructor() {
        assertEquals(100, spreadsheet.getRows(), "Constructor should initialize 100 rows");
        assertEquals(100, spreadsheet.getCols(), "Constructor should initialize 100 columns");
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
    public void testGetCellsObject() {
        Cell[][] cellsObject = spreadsheet.getCellsObject();
        assertEquals(100, cellsObject.length, "getCellsObject should return a 2D array with 100 rows");
        assertEquals(100, cellsObject[0].length, "Each row in the 2D array should have 100 columns");
    }

    @Test
    public void testGetCellStringsObject() {
        String[][] cellStringsObject = spreadsheet.getCellStringsObject();
        assertEquals(100, cellStringsObject.length, "getCellStringsObject should return a 2D array with 100 rows");
        assertEquals(100, cellStringsObject[0].length, "Each row in the 2D array should have 100 columns");
    }

    @Test
    public void testGetCells() {
        ArrayList<ArrayList<Cell>> cells = spreadsheet.getCells();
        assertEquals(100, cells.size(), "getCells should return 100 rows");

        for (ArrayList<Cell> row : cells) {
            assertEquals(100, row.size(), "Each row should have 100 columns");
        }
    }

    @Test
    public void testEvaluateFormulaNotAFormula() {
        assertEquals("not a formula", spreadsheet.evaluateFormula("not a formula"));
        assertEquals("not a formula", spreadsheet.evaluateFormula("= not a formula"));
    }

    @Test
    public void testEvaluateFormulaArithmetic() {
        assertEquals("4", spreadsheet.evaluateFormula("= 2 + 2"));
        assertEquals("4", spreadsheet.evaluateFormula("= 6 - 2"));
        assertEquals("4", spreadsheet.evaluateFormula("= 2 * 2"));
        assertEquals("4", spreadsheet.evaluateFormula("= 8 / 2"));

        assertEquals("Error", spreadsheet.evaluateFormula("= e + 2"));
        assertEquals("Error", spreadsheet.evaluateFormula("= e - 2"));
        assertEquals("Error", spreadsheet.evaluateFormula("= e * 2"));
        assertEquals("Error", spreadsheet.evaluateFormula("= e / 2"));
    }

    @Test
    public void testEvaluateFormulaComparisons() {
        // less than
        assertEquals("1", spreadsheet.evaluateFormula("= 1 < 2"));
        assertEquals("0", spreadsheet.evaluateFormula("= 2 < 1"));
        assertEquals("Error", spreadsheet.evaluateFormula("= e < 2"));

        // greater than
        assertEquals("1", spreadsheet.evaluateFormula("= 2 > 1"));
        assertEquals("0", spreadsheet.evaluateFormula("= 1 > 2"));
        assertEquals("Error", spreadsheet.evaluateFormula("= e > 2"));

        // equal
        assertEquals("1", spreadsheet.evaluateFormula("= 1 = 1"));
        assertEquals("0", spreadsheet.evaluateFormula("= 1 = 2"));
        assertEquals("1", spreadsheet.evaluateFormula("= e = e"));
        assertEquals("0", spreadsheet.evaluateFormula("= e = f"));
        // assertEquals("Error", spreadsheet.evaluateFormula("= e = 1"));

        // not equal
        assertEquals("1", spreadsheet.evaluateFormula("= 1 <> 2"));
        assertEquals("0", spreadsheet.evaluateFormula("= 1 <> 1"));
        assertEquals("1", spreadsheet.evaluateFormula("= e <> f"));
        assertEquals("0", spreadsheet.evaluateFormula("= e <> e"));
        // assertEquals("Error", spreadsheet.evaluateFormula("= e <> 1"));
    }

    @Test
    public void testEvaluateFormulaBoolean() {
        // and
        assertEquals("1", spreadsheet.evaluateFormula("= 1 & 1"));
        assertEquals("0", spreadsheet.evaluateFormula("= 1 & 0"));
        assertEquals("0", spreadsheet.evaluateFormula("= 0 & 1"));
        assertEquals("0", spreadsheet.evaluateFormula("= 0 & 0"));
        assertEquals("Error", spreadsheet.evaluateFormula("= e & 1"));

        // or
        assertEquals("1", spreadsheet.evaluateFormula("= 1 | 1"));
        assertEquals("1", spreadsheet.evaluateFormula("= 1 | 0"));
        assertEquals("1", spreadsheet.evaluateFormula("= 0 | 1"));
        assertEquals("0", spreadsheet.evaluateFormula("= 0 | 0"));
        assertEquals("Error", spreadsheet.evaluateFormula("= e | 1"));
    }

    @Test
    public void testEvaluateFormulaRange() {
        spreadsheet.setCellValue(0, 0, "1");
        spreadsheet.setCellValue(1, 0, "2");
        spreadsheet.setCellValue(2, 0, "3");
        spreadsheet.setCellValue(3, 0, "4");

        assertEquals("1,2,3,4", spreadsheet.evaluateFormula("= $A1:$A4"));
        assertEquals("Error", spreadsheet.evaluateFormula("= $A2:$A1"));
    }

    @Test
    public void testEvaluateFormulaIF() {
        assertEquals("1", spreadsheet.evaluateFormula("=IF(1,1,0)"));
        assertEquals("0", spreadsheet.evaluateFormula("=IF(0,1,0)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=IF(e,1,0)"));
    }

    @Test
    public void testEvaluateFormulaSUM() {
        assertEquals("10.0", spreadsheet.evaluateFormula("=SUM(1,2,3,4)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=SUM(e,2,3,4)"));
    }

    @Test
    public void testEvaluateFormulaMIN() {
        assertEquals("1.0", spreadsheet.evaluateFormula("=MIN(1,2,3,4)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=MIN(e,2,3,4)"));
    }

    @Test
    public void testEvaluateFormulaMAX() {
        assertEquals("4.0", spreadsheet.evaluateFormula("=MAX(1,2,3,4)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=MAX(e,2,3,4)"));
    }

    @Test
    public void testEvaluateFormulaAVG() {
        assertEquals("2.5", spreadsheet.evaluateFormula("=AVG(1,2,3,4)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=AVG(e,2,3,4)"));
    }

    @Test
    public void testEvaluateFormulaCONCATAndDEBUG() {
        assertEquals("1234", spreadsheet.evaluateFormula("=CONCAT(1,2,3,4)"));
        assertEquals("1,2,3,4", spreadsheet.evaluateFormula("=DEBUG(1,2,3,4)"));
    }

    @Test
    public void testEvaluateFormulaSTDDEV() {
        assertEquals("4.899", spreadsheet.evaluateFormula("=STDDEV(10, 12, 23, 23, 16, 23, 21, 16)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=STDDEV(e, 10, 12, 23, 23, 16, 23, 21, 16)"));
    }

    @Test
    public void testEvaluateFormulaSORT() {
        spreadsheet.setCellValue(0, 0, "2");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(2, 0, "13");
        spreadsheet.setCellValue(3, 0, "-1");
        spreadsheet.setCellValue(4, 0, "5");

        spreadsheet.evaluateFormula("=SORT($A1:$A5)");

        assertEquals("-1.0", spreadsheet.getCellValue(5, 0));
        assertEquals("2.0", spreadsheet.getCellValue(6, 0));
        assertEquals("3.0", spreadsheet.getCellValue(7, 0));
        assertEquals("5.0", spreadsheet.getCellValue(8, 0));
        assertEquals("13.0", spreadsheet.getCellValue(9, 0));
    }

    @Test
    public void testEvaluateFormulaSORTError() {
        spreadsheet.setCellValue(0, 0, "e");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(2, 0, "13");
        spreadsheet.setCellValue(3, 0, "-1");
        spreadsheet.setCellValue(4, 0, "5");

        spreadsheet.evaluateFormula("=SORT($A1:$A5)");

        assertEquals("", spreadsheet.getCellValue(5, 0));
    }
}
