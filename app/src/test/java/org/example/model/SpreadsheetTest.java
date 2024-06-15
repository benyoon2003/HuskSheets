package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests the methods within the Spreadsheet class.
 */
public class SpreadsheetTest {

    private Spreadsheet spreadsheet;

    /**
     * Sets up the test environment by initializing a Spreadsheet instance.
     * @author Vinay
     */
    @BeforeEach
    public void setUp() {
        spreadsheet = new Spreadsheet("Test"); // Setting up a new spreadsheet named test
    }

    /**
     * Tests the constructor of the Spreadsheet class.
     * @author Vinay
     */
    @Test
    public void testConstructor() {
        // 100 rows and 100 columns were initialized
        assertEquals(100, spreadsheet.getRows(), "Constructor should initialize 100 rows"); // Assert that the constructor initializes 100 rows
        assertEquals(100, spreadsheet.getCols(), "Constructor should initialize 100 columns"); // Assert that the constructor initializes 100 columns
    }

    /**
     * Tests the getIdVersion method of the Spreadsheet class.
     * @author Ben
     */
    @Test
    public void testGetIdVersion() {
    Spreadsheet spreadsheet = new Spreadsheet("Test");
    assertEquals(0, spreadsheet.getId_version(), "Initial id_version should be 0");

    // Simulate changes that would increment id_version
    spreadsheet.addPublished(new Spreadsheet("NewTest"));
    assertEquals(1, spreadsheet.getId_version(), "id_version should be incremented after adding a published version");
}

    /**
     * Tests the setGrid method of the Spreadsheet class.
     * @author Vinay
     */
    @Test
    public void testSetGrid() {
        List<List<Cell>> newGrid = new ArrayList<>(); // Create a new 2D list to represent the grid
        for (int i = 0; i < 100; i++) { // Iterate 100 times to create 100 rows
            List<Cell> row = new ArrayList<>(); // Create a new list to represent a row
            for (int j = 0; j < 100; j++) { // Iterate 100 times to create 100 columns
                row.add(new Cell("")); // Add a new Cell with an empty string to the row
            }
            newGrid.add(row); // Add the row to the grid
        }
        newGrid.get(0).get(0).setValue("New Value"); // Set the value of the first cell to "New Value"

        spreadsheet.setGrid(newGrid); // Set the new grid in the spreadsheet

        // grid was correctly set
        assertEquals("New Value", spreadsheet.getCellValue(0, 0)); // Assert that the value of the first cell is "New Value"
        assertEquals(newGrid, spreadsheet.getGrid()); // Assert that the grid was correctly set
    }

    /**
     * Tests the constructor of the Spreadsheet class with an existing grid.
     * @author Theo
     */
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

        assertNotNull(spreadsheet); // spreadsheet was created successfully
        // initial spreadsheet values were set
        assertEquals("00", spreadsheet.getCellValue(0, 0));
        assertEquals("01", spreadsheet.getCellValue(0, 1));
        assertEquals("10", spreadsheet.getCellValue(1, 0));
        assertEquals("11", spreadsheet.getCellValue(1, 1));

        // no other spreadsheet values were set
        assertEquals("", spreadsheet.getCellValue(2, 0));
        assertEquals("", spreadsheet.getCellValue(99, 99));
    }

    /**
     * Tests the convertSheetToPayload method of the Spreadsheet class.
     * @author Tony
     */
    @Test
    public void testConvertSheetToPayload() {
        spreadsheet.setCellRawdata(0, 0, "00");
        spreadsheet.setCellRawdata(0, 1, "01");
        spreadsheet.setCellRawdata(1, 0, "10");
        spreadsheet.setCellRawdata(1, 1, "11");

        String payload = Spreadsheet.convertSheetToPayload(spreadsheet);

        // payload was converted as expected
        assertEquals("$A1 00\\n$B1 01\\n$A2 10\\n$B2 11\\n", payload);
    }

    /**
     * Tests the getRows method of the Spreadsheet class.
     * @author Ben
     */
    @Test
    public void testGetRows() {
        assertEquals(100, spreadsheet.getRows(), "getRows should return the correct number of rows");
    }

    /**
     * Tests the getCols method of the Spreadsheet class.
     * @author Ben
     */
    @Test
    public void testGetCols() {
        assertEquals(100, spreadsheet.getCols(), "getCols should return the correct number of columns");
    }

    /**
     * Tests the getCellsObject method of the Spreadsheet class.
     * @author Tony
     */
    @Test
    public void testGetCellsObject() {
        Cell[][] cellsObject = spreadsheet.getCellsObject();
        assertEquals(100, cellsObject.length, "getCellsObject should return a 2D array with 100 rows");
        assertEquals(100, cellsObject[0].length, "Each row in the 2D array should have 100 columns");
    }

    /**
     * Tests the getCellStringsObject method of the Spreadsheet class.
     * @author Tony
     */
    @Test
    public void testGetCellStringsObject() {
        String[][] cellStringsObject = spreadsheet.getCellStringsObject();
        assertEquals(100, cellStringsObject.length, "getCellStringsObject should return a 2D array with 100 rows");
        assertEquals(100, cellStringsObject[0].length, "Each row in the 2D array should have 100 columns");
    }

    /**
     * Tests the getCells method of the Spreadsheet class.
     * @author Ben
     */
    @Test
    public void testGetCells() {
        List<List<Cell>> cells = spreadsheet.getCells();
        assertEquals(100, cells.size(), "getCells should return 100 rows");

        for (List<Cell> row : cells) {
            assertEquals(100, row.size(), "Each row should have 100 columns");
        }
    }

    /**
     * Tests the private getRow method of the Spreadsheet class using reflection.
     * @throws Exception if there is an error during reflection
     * @author Ben
     */
    @Test
    public void testGetRow() throws Exception {
        Method getRowMethod = Spreadsheet.class.getDeclaredMethod("getRow", String.class);
        getRowMethod.setAccessible(true);
        assertEquals(0, getRowMethod.invoke(spreadsheet, "$A1"));
        assertEquals(9, getRowMethod.invoke(spreadsheet, "$A10"));
        assertEquals(-1, getRowMethod.invoke(spreadsheet, "$AX"));
    }

    /**
     * Tests the private getColumn method of the Spreadsheet class using reflection.
     * @throws Exception if there is an error during reflection
     * @author Ben
     */
    @Test
    public void testGetColumn() throws Exception {
        Method getColumnMethod = Spreadsheet.class.getDeclaredMethod("getColumn", String.class);
        getColumnMethod.setAccessible(true);
        assertEquals(0, getColumnMethod.invoke(spreadsheet, "$A1"));
        assertEquals(25, getColumnMethod.invoke(spreadsheet, "$Z1"));
        assertEquals(26, getColumnMethod.invoke(spreadsheet, "$AA1"));
    }

    /**
     * Tests the getColumnName method of the Spreadsheet class.
     * @author Vinay
     */
    @Test
    public void testGetColumnName() {
        assertEquals("A", Spreadsheet.getColumnName(1)); // Test getColumnName with column index 1, expecting "A"
        assertEquals("Z", Spreadsheet.getColumnName(26)); // Test getColumnName with column index 26, expecting "Z"
        assertEquals("AA", Spreadsheet.getColumnName(27)); // Test getColumnName with column index 27, expecting "AA"
    }

    /**
     * Tests the addPublished and addSubscribed methods of the Spreadsheet class.
     * @author Ben
     */
    @Test
    public void testAddPublishedAndSubscribed() {
        Spreadsheet newSheet = new Spreadsheet("NewTest");
        spreadsheet.addPublished(newSheet);
        spreadsheet.addSubscribed(newSheet);

        // ensure the new spreadsheet was added
        assertEquals(1, spreadsheet.getPublishedVersions().size());
        assertEquals(1, spreadsheet.getSubscribedVersions().size());
        assertEquals(newSheet, spreadsheet.getPublishedVersions().get(0));
        assertEquals(newSheet, spreadsheet.getSubscribedVersions().get(0));
    }

    /**
     * Tests the setCellRawdata and getCellRawdata methods of the Spreadsheet class.
     * @author Ben
     */
    @Test
    public void testSetAndGetCellRawdata() {
        spreadsheet.setCellRawdata(0, 0, "Test Raw Data");
        assertEquals("Test Raw Data", spreadsheet.getCellRawdata(0, 0));
    }

    /**
     * Tests the evaluateFormula method of the Spreadsheet class when the input is not a formula.
     * @author Theo
     */
    @Test
    public void testEvaluateFormulaNotAFormula() {
        assertEquals("not a formula", spreadsheet.evaluateFormula("not a formula"));
        assertEquals("not a formula", spreadsheet.evaluateFormula("= not a formula"));
    }

    /**
     * Tests the evaluateFormula method of the Spreadsheet class with arithmetic operations.
     * @author Vinay
     */
    @Test
    public void testEvaluateFormulaArithmetic() {
        // arithmetic formulas return correct answers
        assertEquals("4", spreadsheet.evaluateFormula("= 2 + 2")); // Test addition, expecting "4"
        assertEquals("4", spreadsheet.evaluateFormula("= 6 - 2")); // Test subtraction, expecting "4"
        assertEquals("4", spreadsheet.evaluateFormula("= 2 * 2")); // Test multiplication, expecting "4"
        assertEquals("4", spreadsheet.evaluateFormula("= 8 / 2")); // Test division, expecting "4"

        // arithmetic formulas return error messages when strings are included
        assertEquals("Error", spreadsheet.evaluateFormula("= e + 2")); // Test addition with an invalid string, expecting "Error"
        assertEquals("Error", spreadsheet.evaluateFormula("= e - 2")); // Test subtraction with an invalid string, expecting "Error"
        assertEquals("Error", spreadsheet.evaluateFormula("= e * 2")); // Test multiplication with an invalid string, expecting "Error"
        assertEquals("Error", spreadsheet.evaluateFormula("= e / 2")); // Test division with an invalid string, expecting "Error"
    }

    /**
     * Tests the evaluateFormula method of the Spreadsheet class with comparison operations.
     * @author Vinay
     */
    @Test
    public void testEvaluateFormulaComparisons() {
        // less than
        assertEquals("1", spreadsheet.evaluateFormula("= 1 < 2")); // true
        assertEquals("0", spreadsheet.evaluateFormula("= 2 < 1")); // false
        assertEquals("Error", spreadsheet.evaluateFormula("= e < 2")); // string error

        // greater than
        assertEquals("1", spreadsheet.evaluateFormula("= 2 > 1")); // true
        assertEquals("0", spreadsheet.evaluateFormula("= 1 > 2")); // false
        assertEquals("Error", spreadsheet.evaluateFormula("= e > 2")); // string error

        // equal
        assertEquals("1", spreadsheet.evaluateFormula("= 1 = 1")); // true (numbers)
        assertEquals("0", spreadsheet.evaluateFormula("= 1 = 2")); // false (numbers)
        assertEquals("1", spreadsheet.evaluateFormula("= e = e")); // true (strings)
        assertEquals("0", spreadsheet.evaluateFormula("= e = f")); // false (strings)

        // not equal
        assertEquals("1", spreadsheet.evaluateFormula("= 1 <> 2")); // true (strings)
        assertEquals("0", spreadsheet.evaluateFormula("= 1 <> 1")); // false (strings)
        assertEquals("1", spreadsheet.evaluateFormula("= e <> f")); // true (strings)
        assertEquals("0", spreadsheet.evaluateFormula("= e <> e")); // false (strings)
    }

    /**
     * Tests the evaluateFormula method of the Spreadsheet class with boolean operations.
     * @author Theo
     */
    @Test
    public void testEvaluateFormulaBoolean() {
        // and
        assertEquals("1", spreadsheet.evaluateFormula("= 1 & 1")); // true
        // false
        assertEquals("0", spreadsheet.evaluateFormula("= 1 & 0"));
        assertEquals("0", spreadsheet.evaluateFormula("= 0 & 1"));
        assertEquals("0", spreadsheet.evaluateFormula("= 0 & 0"));
        assertEquals("Error", spreadsheet.evaluateFormula("= e & 1")); // string error

        // or
        // true
        assertEquals("1", spreadsheet.evaluateFormula("= 1 | 1"));
        assertEquals("1", spreadsheet.evaluateFormula("= 1 | 0"));
        assertEquals("1", spreadsheet.evaluateFormula("= 0 | 1"));
        assertEquals("0", spreadsheet.evaluateFormula("= 0 | 0")); // false
        assertEquals("Error", spreadsheet.evaluateFormula("= e | 1")); // string error
    }

    /**
     * Tests the private rangeOperation method of the Spreadsheet class using reflection.
     * @throws Exception if there is an error during reflection
     * @author Theo
     */
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
    
    /**
     * Tests the private evaluateIF method of the Spreadsheet class using reflection.
     * @throws Exception if there is an error during reflection
     * @author Vinay
     */
    @Test
    public void testEvaluateIF() throws Exception {
        Method evaluateIFMethod = Spreadsheet.class.getDeclaredMethod("evaluateIF", String.class); // Get the private evaluateIF method from the Spreadsheet class
        evaluateIFMethod.setAccessible(true); // Make the evaluateIF method accessible
    
        // Test valid IF condition true
        String result = (String) evaluateIFMethod.invoke(spreadsheet, "1,1,0"); // Invoke evaluateIF method with condition true
        assertEquals("1", result); // Assert that the result is "1"
    
        // Test valid IF condition false
        result = (String) evaluateIFMethod.invoke(spreadsheet, "0,1,0"); // Invoke evaluateIF method with condition false
        assertEquals("0", result); // Assert that the result is "0"
    
        // Test valid IF with cell references
        spreadsheet.setCellValue(0, 0, "5"); // Set cell (0,0) value to 5
        spreadsheet.setCellValue(0, 1, "3"); // Set cell (0,1) value to 3
        result = (String) evaluateIFMethod.invoke(spreadsheet, "$A1>$A2,1,0"); // Invoke evaluateIF method with cell references
        assertEquals("Error", result); // Assert that the result is "Error"
    
        // Test invalid IF condition
        result = (String) evaluateIFMethod.invoke(spreadsheet, "e,1,0"); // Invoke evaluateIF method with an invalid condition
        assertEquals("Error", result); // Assert that the result is "Error"
    
        // Test IF with missing parameters
        result = (String) evaluateIFMethod.invoke(spreadsheet, "1,1"); // Invoke evaluateIF method with missing parameters
        assertEquals("Error", result); // Assert that the result is "Error"
    
        // Test IF with extra parameters
        result = (String) evaluateIFMethod.invoke(spreadsheet, "1,1,0,1"); // Invoke evaluateIF method with extra parameters
        assertEquals("Error", result); // Assert that the result is "Error"
    }

    /**
     * Tests the evaluateDEBUG method of the Spreadsheet class using reflection.
     * @throws Exception if there is an error during reflection
     * @author Theo
     */
    @Test
    public void testEvaluateDEBUG() throws Exception {
        Method evaluateDEBUGMethod = Spreadsheet.class.getDeclaredMethod("evaluateDEBUG", String.class);
        evaluateDEBUGMethod.setAccessible(true);

        // Test with leading and trailing spaces
        String result = (String) evaluateDEBUGMethod.invoke(spreadsheet, "  test  ");
        assertEquals("test", result, "evaluateDEBUG should trim leading and trailing spaces");

        // Test with no spaces
        result = (String) evaluateDEBUGMethod.invoke(spreadsheet, "test");
        assertEquals("test", result, "evaluateDEBUG should return the same string when there are no leading or trailing spaces");

        // Test with only spaces
        result = (String) evaluateDEBUGMethod.invoke(spreadsheet, "  ");
        assertEquals("", result, "evaluateDEBUG should return an empty string when the input is only spaces");
    }
    
    /**
     * Tests the evaluateFormula method of the Spreadsheet class with the SUM function.
     * @author Theo
     */
    @Test
    public void testEvaluateFormulaSUM() {
        assertEquals("10.0", spreadsheet.evaluateFormula("=SUM(1,2,3,4)")); // correct answer
        assertEquals("Error", spreadsheet.evaluateFormula("=SUM(e,2,3,4)")); // string error

        // testing nested functions
        assertEquals("5.0", spreadsheet.evaluateFormula("=SUM(SUM(1,1),1,2)"));
        assertEquals("5.0", spreadsheet.evaluateFormula("=SUM(SUM(1,1),SUM(1,2))"));
        assertEquals("7.0", spreadsheet.evaluateFormula("=SUM(SUM(1,1),SUM(1,2),2)"));
    }

    /**
     * Tests the evaluateFormula method of the Spreadsheet class with the MAX function.
     * @author Vinay
     */
    @Test
    public void testEvaluateMAX() {
        // Test with simple numerical values
        assertEquals("4.0", spreadsheet.evaluateFormula("=MAX(1,2,3,4)")); // Test MAX function with direct numerical values, expecting 4.0
        assertEquals("Error", spreadsheet.evaluateFormula("=MAX(e,2,3,4)")); // Test MAX function with an invalid string input, expecting an error

        // Test with cell references
        spreadsheet.setCellValue(0, 0, "5"); // Set cell (0,0) value to 5
        spreadsheet.setCellValue(1, 0, "3"); // Set cell (1,0) value to 3
        spreadsheet.setCellValue(2, 0, "8"); // Set cell (2,0) value to 8
        spreadsheet.setCellValue(3, 0, "1"); // Set cell (3,0) value to 1

        assertEquals("8.0", spreadsheet.evaluateFormula("=MAX($A1,$A2,$A3,$A4)")); // Test MAX function with individual cell references, expecting 8.0
        assertEquals("8.0", spreadsheet.evaluateFormula("=MAX($A1:$A4)")); // Test MAX function with a range of cells, expecting 8.0
        assertEquals("8.0", spreadsheet.evaluateFormula("=MAX(MAX(2,4),3,8)")); // Test MAX function with nested MAX function calls, expecting 8.0
    }

    /**
     * Tests the evaluateFormula method of the Spreadsheet class with the AVG function.
     * @author Vinay
     */
    @Test
    public void testEvaluateAVG() {
        // Test with simple numerical values
        assertEquals("2.5", spreadsheet.evaluateFormula("=AVG(1,2,3,4)")); // Test AVG function with direct numerical values, expecting 2.5
        assertEquals("Error", spreadsheet.evaluateFormula("=AVG(e,2,3,4)")); // Test AVG function with an invalid string input, expecting an error

        // Test with cell references
        spreadsheet.setCellValue(0, 0, "5"); // Set cell (0,0) value to 5
        spreadsheet.setCellValue(1, 0, "3"); // Set cell (1,0) value to 3
        spreadsheet.setCellValue(2, 0, "8"); // Set cell (2,0) value to 8
        spreadsheet.setCellValue(3, 0, "1"); // Set cell (3,0) value to 1

        assertEquals("4.25", spreadsheet.evaluateFormula("=AVG($A1,$A2,$A3,$A4)")); // Test AVG function with individual cell references, expecting 4.25
        assertEquals("4.25", spreadsheet.evaluateFormula("=AVG($A1:$A4)")); // Test AVG function with a range of cells, expecting 4.25
        assertEquals("2.0", spreadsheet.evaluateFormula("=AVG(AVG(2,4),3,2)")); // Test AVG function with nested AVG function calls, expecting 2.0
    }

    /**
     * Tests the evaluateFormula method of the Spreadsheet class with the CONCAT function.
     * @author Theo
     */
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

    /**
     * Tests the evaluateFormula method of the Spreadsheet class with the STDDEV function.
     * @author Theo
     */
    @Test
    public void testEvaluateSTDDEV() {
        // Test with simple numerical values
        assertEquals("4.899", spreadsheet.evaluateFormula("=STDDEV(10, 12, 23, 23, 16, 23, 21, 16)")); // correct answer
        assertEquals("Error", spreadsheet.evaluateFormula("=STDDEV(e, 10, 12, 23, 23, 16, 23, 21, 16)")); // string error

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

    /**
     * Tests the evaluateFormula method of the Spreadsheet class with the SORT function.
     * @author Theo
     */
    @Test
    public void testEvaluateFormulaSORT() {
        spreadsheet.setCellValue(0, 0, "2");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(2, 0, "13");
        spreadsheet.setCellValue(3, 0, "-1");
        spreadsheet.setCellValue(4, 0, "5");

        assertEquals("-1.0,2.0,3.0,5.0,13.0", spreadsheet.evaluateFormula("=SORT($A1:$A5)"));
    }
    
    /**
     * Tests the evaluateFormula method of the Spreadsheet class with the SORT function when an error occurs.
     * @author Theo
     */
    @Test
    public void testEvaluateFormulaSORTError() {
        spreadsheet.setCellValue(0, 0, "e");
        spreadsheet.setCellValue(1, 0, "3");
        spreadsheet.setCellValue(2, 0, "13");
        spreadsheet.setCellValue(3, 0, "-1");
        spreadsheet.setCellValue(4, 0, "5");

        assertEquals("Error", spreadsheet.evaluateFormula("=SORT($A1:$A5)"));
    }

    /**
     * Tests the evaluateFormula method of the Spreadsheet class with the MIN function.
     * @author Vinay
     */
    @Test
    public void testEvaluateMIN() {
        // Test with simple numerical values
        assertEquals("1.0", spreadsheet.evaluateFormula("=MIN(1,2,3,4)")); // Test MIN function with direct numerical values
        assertEquals("Error", spreadsheet.evaluateFormula("=MIN(e,2,3,4)")); // Test MIN function with an invalid string input, expecting an error

        // Test with cell references
        spreadsheet.setCellValue(0, 0, "5"); // Set cell (0,0) value to 5
        spreadsheet.setCellValue(1, 0, "3"); // Set cell (1,0) value to 3
        spreadsheet.setCellValue(2, 0, "8"); // Set cell (2,0) value to 8
        spreadsheet.setCellValue(3, 0, "1"); // Set cell (3,0) value to 1

        assertEquals("1.0", spreadsheet.evaluateFormula("=MIN($A1,$A2,$A3,$A4)")); // Test MIN function with individual cell references
        assertEquals("1.0", spreadsheet.evaluateFormula("=MIN($A1:$A4)")); // Test MIN function with a range of cells
        assertEquals("1.0", spreadsheet.evaluateFormula("=MIN(MIN(2,4),3,1)")); // Test MIN function with nested MIN function calls
    }
}
