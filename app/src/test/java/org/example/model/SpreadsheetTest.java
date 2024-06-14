package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    public void testGetIdVersion() {
    Spreadsheet spreadsheet = new Spreadsheet("Test");
    assertEquals(0, spreadsheet.getId_version(), "Initial id_version should be 0");

    // Simulate changes that would increment id_version
    spreadsheet.addPublished(new Spreadsheet("NewTest"));
    assertEquals(1, spreadsheet.getId_version(), "id_version should be incremented after adding a published version");
}


    @Test
    public void testSetGrid() {
        List<List<Cell>> newGrid = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < 100; j++) {
                row.add(new Cell(""));
            }
            newGrid.add(row);
        }
        newGrid.get(0).get(0).setValue("New Value");

        spreadsheet.setGrid(newGrid);

        assertEquals("New Value", spreadsheet.getCellValue(0, 0));
        assertEquals(newGrid, spreadsheet.getGrid());
    }

    @Test
    public void testConstructorExistingGrid() {
        List<List<Cell>> grid = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < 100; j++) {
                row.add(new Cell(""));
            }
            grid.add(row);
        }
        grid.get(0).get(0).setValue("00");
        grid.get(0).get(1).setValue("01");
        grid.get(1).get(0).setValue("10");
        grid.get(1).get(1).setValue("11");

        spreadsheet = new Spreadsheet(grid, "Test");

        assertNotNull(spreadsheet);
        assertEquals("00", spreadsheet.getCellValue(0, 0));
        assertEquals("01", spreadsheet.getCellValue(0, 1));
        assertEquals("10", spreadsheet.getCellValue(1, 0));
        assertEquals("11", spreadsheet.getCellValue(1, 1));
    }

    @Test
    public void testConvertSheetToPayload() {
        spreadsheet.setCellRawdata(0, 0, "00");
        spreadsheet.setCellRawdata(0, 1, "01");
        spreadsheet.setCellRawdata(1, 0, "10");
        spreadsheet.setCellRawdata(1, 1, "11");

        String payload = Spreadsheet.convertSheetToPayload(spreadsheet);

        assertEquals("$A1 00\\n$B1 01\\n$A2 10\\n$B2 11\\n", payload);
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
        List<List<Cell>> cells = spreadsheet.getCells();
        assertEquals(100, cells.size(), "getCells should return 100 rows");

        for (List<Cell> row : cells) {
            assertEquals(100, row.size(), "Each row should have 100 columns");
        }
    }

    @Test
    public void testGetRow() throws Exception {
        Method getRowMethod = Spreadsheet.class.getDeclaredMethod("getRow", String.class);
        getRowMethod.setAccessible(true);
        assertEquals(0, getRowMethod.invoke(spreadsheet, "$A1"));
        assertEquals(9, getRowMethod.invoke(spreadsheet, "$A10"));
        assertEquals(-1, getRowMethod.invoke(spreadsheet, "$AX"));
    }

    @Test
    public void testGetColumn() throws Exception {
        Method getColumnMethod = Spreadsheet.class.getDeclaredMethod("getColumn", String.class);
        getColumnMethod.setAccessible(true);
        assertEquals(0, getColumnMethod.invoke(spreadsheet, "$A1"));
        assertEquals(25, getColumnMethod.invoke(spreadsheet, "$Z1"));
        assertEquals(26, getColumnMethod.invoke(spreadsheet, "$AA1"));
    }

    @Test
    public void testGetColumnName() {
        assertEquals("A", Spreadsheet.getColumnName(1));
        assertEquals("Z", Spreadsheet.getColumnName(26));
        assertEquals("AA", Spreadsheet.getColumnName(27));
    }

    @Test
    public void testAddPublishedAndSubscribed() {
        Spreadsheet newSheet = new Spreadsheet("NewTest");
        spreadsheet.addPublished(newSheet);
        spreadsheet.addSubscribed(newSheet);

        assertEquals(1, spreadsheet.getPublishedVersions().size());
        assertEquals(1, spreadsheet.getSubscribedVersions().size());
        assertEquals(newSheet, spreadsheet.getPublishedVersions().get(0));
        assertEquals(newSheet, spreadsheet.getSubscribedVersions().get(0));
    }

    @Test
    public void testSetAndGetCellRawdata() {
        spreadsheet.setCellRawdata(0, 0, "Test Raw Data");
        assertEquals("Test Raw Data", spreadsheet.getCellRawdata(0, 0));
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
    public void testRangeOperation() throws Exception {
        // Create a new Spreadsheet and set some values
        spreadsheet.setCellValue(0, 0, "1");
        spreadsheet.setCellValue(0, 1, "2");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(1, 1, "4");
    
        Method rangeOperationMethod = Spreadsheet.class.getDeclaredMethod("rangeOperation", String.class, String.class);
        rangeOperationMethod.setAccessible(true);
    
        // Test valid range
        String result = (String) rangeOperationMethod.invoke(spreadsheet, "$A1", "$B2");
        assertEquals("1,2,3,4", result);
    
        // Test invalid range (end row less than start row)
        result = (String) rangeOperationMethod.invoke(spreadsheet, "$A2", "$A1");
        assertEquals("Error", result);
    
        // Test invalid range (end column less than start column)
        result = (String) rangeOperationMethod.invoke(spreadsheet, "$B1", "$A1");
        assertEquals("Error", result);
    
        // Test range with non-existent cells
        result = (String) rangeOperationMethod.invoke(spreadsheet, "$Z1", "$AA1");
        assertEquals("", result);
    
        // Test empty range
        result = (String) rangeOperationMethod.invoke(spreadsheet, "$A3", "$A3");
        assertEquals("", result);
    }
    

    @Test
    public void testEvaluateIF() throws Exception {
        Method evaluateIFMethod = Spreadsheet.class.getDeclaredMethod("evaluateIF", String.class);
        evaluateIFMethod.setAccessible(true);
    
        // Test valid IF condition true
        String result = (String) evaluateIFMethod.invoke(spreadsheet, "1,1,0");
        assertEquals("1", result);
    
        // Test valid IF condition false
        result = (String) evaluateIFMethod.invoke(spreadsheet, "0,1,0");
        assertEquals("0", result);
    
        // Test valid IF with cell references
        spreadsheet.setCellValue(0, 0, "5");
        spreadsheet.setCellValue(0, 1, "3");
        result = (String) evaluateIFMethod.invoke(spreadsheet, "$A1>$A2,1,0");
        assertEquals("Error", result);
    
        // Test invalid IF condition
        result = (String) evaluateIFMethod.invoke(spreadsheet, "e,1,0");
        assertEquals("Error", result);
    
        // Test IF with missing parameters
        result = (String) evaluateIFMethod.invoke(spreadsheet, "1,1");
        assertEquals("Error", result);
    
        // Test IF with extra parameters
        result = (String) evaluateIFMethod.invoke(spreadsheet, "1,1,0,1");
        assertEquals("Error", result);
    }
    

    @Test
    public void testEvaluateFormulaSUM() {
        assertEquals("10.0", spreadsheet.evaluateFormula("=SUM(1,2,3,4)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=SUM(e,2,3,4)"));

        assertEquals("5.0", spreadsheet.evaluateFormula("=SUM(SUM(1,1),1,2)"));
        assertEquals("5.0", spreadsheet.evaluateFormula("=SUM(SUM(1,1),SUM(1,2))"));
        assertEquals("7.0", spreadsheet.evaluateFormula("=SUM(SUM(1,1),SUM(1,2),2)"));
    }

    @Test
    public void testEvaluateMAX() {
        // Test with simple numerical values
        assertEquals("4.0", spreadsheet.evaluateFormula("=MAX(1,2,3,4)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=MAX(e,2,3,4)"));

        // Test with cell references
        spreadsheet.setCellValue(0, 0, "5");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(2, 0, "8");
        spreadsheet.setCellValue(3, 0, "1");

        assertEquals("8.0", spreadsheet.evaluateFormula("=MAX($A1,$A2,$A3,$A4)"));
        assertEquals("8.0", spreadsheet.evaluateFormula("=MAX($A1:$A4)"));

        // Test with nested function calls
        assertEquals("8.0", spreadsheet.evaluateFormula("=MAX(MAX(2,4),3,8)"));
    }

    @Test
    public void testEvaluateAVG() {
        // Test with simple numerical values
        assertEquals("2.5", spreadsheet.evaluateFormula("=AVG(1,2,3,4)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=AVG(e,2,3,4)"));

        // Test with cell references
        spreadsheet.setCellValue(0, 0, "5");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(2, 0, "8");
        spreadsheet.setCellValue(3, 0, "1");

        assertEquals("4.25", spreadsheet.evaluateFormula("=AVG($A1,$A2,$A3,$A4)"));
        assertEquals("4.25", spreadsheet.evaluateFormula("=AVG($A1:$A4)"));

        // Test with nested function calls
        assertEquals("2.0", spreadsheet.evaluateFormula("=AVG(AVG(2,4),3,2)"));
    }

    @Test
    public void testEvaluateFormulaCONCATAndDEBUG() {
        assertEquals("1234", spreadsheet.evaluateFormula("=CONCAT(1,2,3,4)"));
        assertEquals("1,2,3,4", spreadsheet.evaluateFormula("=DEBUG(1,2,3,4)"));
    }

    @Test
    public void testEvaluateCONCAT() {
        // Test with simple string values
        assertEquals("1234", spreadsheet.evaluateFormula("=CONCAT(1,2,3,4)"));
        assertEquals("hello world", spreadsheet.evaluateFormula("=CONCAT(\"hello \",\"world\")"));

        // Test with mixed types
        assertEquals("1hello2", spreadsheet.evaluateFormula("=CONCAT(1,\"hello\",2)"));

        // Test with cell references
        spreadsheet.setCellValue(0, 0, "foo");
        spreadsheet.setCellValue(1, 0, "bar");
        assertEquals("foobar", spreadsheet.evaluateFormula("=CONCAT($A1,$A2)"));
    }

    @Test
    public void testEvaluateSTDDEV() {
        // Test with simple numerical values
        assertEquals("4.899", spreadsheet.evaluateFormula("=STDDEV(10, 12, 23, 23, 16, 23, 21, 16)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=STDDEV(e, 10, 12, 23, 23, 16, 23, 21, 16)"));

        // Test with cell references
        spreadsheet.setCellValue(0, 0, "10");
        spreadsheet.setCellValue(1, 0, "12");
        spreadsheet.setCellValue(2, 0, "23");
        spreadsheet.setCellValue(3, 0, "23");
        spreadsheet.setCellValue(4, 0, "16");
        spreadsheet.setCellValue(5, 0, "23");
        spreadsheet.setCellValue(6, 0, "21");
        spreadsheet.setCellValue(7, 0, "16");

        assertEquals("4.899", spreadsheet.evaluateFormula("=STDDEV($A1:$A8)"));

        // Test with nested function calls
        assertEquals("2.136", spreadsheet.evaluateFormula("=STDDEV(STDDEV(1,2,3,4),STDDEV(5,6,7,8))"));
    }

    @Test
    public void testEvaluateFormulaSORT() {
        spreadsheet.setCellValue(0, 0, "2");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(2, 0, "13");
        spreadsheet.setCellValue(3, 0, "-1");
        spreadsheet.setCellValue(4, 0, "5");

        assertEquals("-1.0,2.0,3.0,5.0,13.0", spreadsheet.evaluateFormula("=SORT($A1:$A5)"));
    }

    @Test
    public void testEvaluateFormulaSORTError() {
        spreadsheet.setCellValue(0, 0, "e");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(2, 0, "13");
        spreadsheet.setCellValue(3, 0, "-1");
        spreadsheet.setCellValue(4, 0, "5");

        assertEquals("Error", spreadsheet.evaluateFormula("=SORT($A1:$A5)"));
    }

    @Test
    public void testEvaluateMIN() {
        // Test with simple numerical values
        assertEquals("1.0", spreadsheet.evaluateFormula("=MIN(1,2,3,4)"));
        assertEquals("Error", spreadsheet.evaluateFormula("=MIN(e,2,3,4)"));

        // Test with cell references
        spreadsheet.setCellValue(0, 0, "5");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(2, 0, "8");
        spreadsheet.setCellValue(3, 0, "1");

        assertEquals("1.0", spreadsheet.evaluateFormula("=MIN($A1,$A2,$A3,$A4)"));
        assertEquals("1.0", spreadsheet.evaluateFormula("=MIN($A1:$A4)"));

        // Test with nested function calls
        assertEquals("1.0", spreadsheet.evaluateFormula("=MIN(MIN(2,4),3,1)"));
    }
}
