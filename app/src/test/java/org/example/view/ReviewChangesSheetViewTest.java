package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals; // Import assertion for equality checks
import static org.junit.jupiter.api.Assertions.assertThrows; // Import assertion for expecting exceptions

import org.example.controller.IUserController; // Import interface for user controller
import org.example.controller.UserController; // Import implementation of user controller
import org.example.model.*; // Import model classes
import org.junit.jupiter.api.BeforeEach; // Import for setup method
import org.junit.jupiter.api.Test; // Import for test annotation

import javax.swing.*; // Import for Swing components
import java.awt.*; // Import for AWT components
import java.util.ArrayList; // Import for ArrayList
import java.util.List; // Import for List
import java.util.Set; // Import for Set

/**
 * Tests the methods within the ReviewChangesSheetView class.
 */
public class ReviewChangesSheetViewTest {
    private ReviewChangesSheetView reviewChangesSheetView; // Instance of ReviewChangesSheetView to be tested
    private IUserController controller; // Instance of IUserController for testing interactions
    private ISpreadsheet currentSpreadSheet; // Current spreadsheet instance for testing
    private ISpreadsheet changesSpreadSheet; // Changes spreadsheet instance for testing
    private IAppUser user = new AppUser("John", "12345"); // Test user
    private ILoginView login; // Login view instance for controller initialization

    /**
     * Initializes the test environment by setting up the necessary components.
     */
    @BeforeEach
    public void init() {
        this.login = new LoginView(); // Create a new instance of LoginView for testing
        this.currentSpreadSheet = new Spreadsheet("current"); // Create a new current spreadsheet
        this.changesSpreadSheet = new Spreadsheet("changes"); // Create a new changes spreadsheet
        this.reviewChangesSheetView = new ReviewChangesSheetView(changesSpreadSheet, currentSpreadSheet); // Create instance of ReviewChangesSheetView
        this.controller = new UserController(login); // Create a new instance of UserController with login view

        // Check if user is registered first
        try {
            this.controller.registerUser(this.user.getUsername(), this.user.getPassword()); // Register the test user
        } catch (Exception e) {
            try {
                this.controller.loginUser(this.user.getUsername(), this.user.getPassword()); // If registration fails, attempt login
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        this.reviewChangesSheetView.addController(this.controller); // Add the controller to the reviewChangesSheetView
    }

    /**
     * Tests the addController method of the ReviewChangesSheetView class.
     * @throws Exception if there is an error during the test
     */
    @Test
    public void testAddController() throws Exception {
        // Test if user is correctly logged into reviewChangesSheetView
        IUserController sheetController = this.reviewChangesSheetView.getController(); // Get the controller from reviewChangesSheetView
        assertEquals(this.controller, sheetController); // Assert that the controllers are equal

        IAppUser sheetUser = sheetController.getAppUser(); // Get the user associated with the controller
        String username = sheetUser.getUsername(); // Get the username
        assertEquals(this.user.getUsername(), username); // Assert that the usernames are equal

        String password = sheetUser.getPassword(); // Get the password
        assertEquals(this.user.getPassword(), password); // Assert that the passwords are equal
    }

    /**
     * Tests the selectedCells method of the ReviewChangesSheetView class.
     */
    @Test
    public void testSelectedCells() {
        int[] selectedRows = {0, 1, 2, 3, 4}; // Define an array of selected rows
        int[] selectedCols = {0, 1, 2, 3, 4}; // Define an array of selected columns

        this.reviewChangesSheetView.selectedCells(selectedRows, selectedCols); // Call selectedCells method
        assertEquals(this.reviewChangesSheetView.selectedCells.getStartCol(), selectedCols[0]); // Assert start column
        assertEquals(this.reviewChangesSheetView.selectedCells.getStartRow(), selectedRows[0]); // Assert start row
        assertEquals(this.reviewChangesSheetView.selectedCells.getEndCol(), selectedCols[selectedCols.length - 1]); // Assert end column
        assertEquals(this.reviewChangesSheetView.selectedCells.getEndRow(), selectedRows[selectedRows.length - 1]); // Assert end row

        assertEquals(this.reviewChangesSheetView.selectedCells.getStartCol(), 0); // Assert start column is 0
        assertEquals(this.reviewChangesSheetView.selectedCells.getStartRow(), 0); // Assert start row is 0
        assertEquals(this.reviewChangesSheetView.selectedCells.getEndCol(), 4); // Assert end column is 4
        assertEquals(this.reviewChangesSheetView.selectedCells.getEndRow(), 4); // Assert end row is 4
    }

    /**
     * Tests the changeFormulaTextField method of the ReviewChangesSheetView class.
     */
    @Test
    public void testChangeFormulaTextField() {
        String rawData1 = "hello"; // Define test raw data
        this.reviewChangesSheetView.changeFormulaTextField(rawData1); // Call changeFormulaTextField method
        assertEquals(rawData1, this.reviewChangesSheetView.formulaTextField.getText()); // Assert text field content
        assertEquals("hello", this.reviewChangesSheetView.formulaTextField.getText()); // Assert text field content (redundant)
    }

    /**
     * Tests the highlightCell method of the ReviewChangesSheetView class.
     */
    @Test
    public void testHighlightCell() {
        int row = 0; // Define test row
        int col = 0; // Define test column
        Color color = Color.PINK; // Define test color
        this.reviewChangesSheetView.highlightCell(row, col, color); // Call highlightCell method

        Set<Point> points = this.reviewChangesSheetView.highlightedCells.keySet(); // Get keys from highlightedCells map

        for (Point point : points) { // Iterate through points
            int pRow = point.x; // Get row from point
            int pCol = point.y - 1; // Get column from point (adjusted for implementation)
            Color pColor = this.reviewChangesSheetView.highlightedCells.get(point); // Get color from highlightedCells map

            assertEquals(row, pRow); // Assert row
            assertEquals(col, pCol); // Assert column
            assertEquals(color, pColor); // Assert color
        }
    }

    /**
     * Tests the getExcelColumnName method of the ReviewChangesSheetView class.
     */
    @Test
    public void testExcelColumnName() {
        int colNum = 1; // Define test column number
        String col = this.reviewChangesSheetView.getExcelColumnName(colNum); // Get Excel column name
        assertEquals("A", col); // Assert column name

        colNum = 4; // Define another test column number
        col = this.reviewChangesSheetView.getExcelColumnName(colNum); // Get Excel column name
        assertEquals("D", col); // Assert column name

        colNum = 27; // Define another test column number
        col = this.reviewChangesSheetView.getExcelColumnName(colNum); // Get Excel column name
        assertEquals("AA", col); // Assert column name
    }

    /**
     * Tests the getTable method of the ReviewChangesSheetView class.
     */
    @Test
    public void testGetTable() {
        JTable testTable = new JTable(); // Create a new JTable instance
        this.reviewChangesSheetView.yourTable = testTable; // Set the test table in reviewChangesSheetView

        assertEquals(testTable, this.reviewChangesSheetView.getTable()); // Assert that the retrieved table matches the test table
    }

    /**
     * Tests the getController method of the ReviewChangesSheetView class.
     */
    @Test
    public void testGetController() {
        assertEquals(this.controller, this.reviewChangesSheetView.getController()); // Assert that the retrieved controller matches the expected controller
    }

    /**
     * Tests the zoomTable method of the ReviewChangesSheetView class.
     */
    @Test
    public void testZoomTable() {
        JTable testTable = new JTable(); // Create a new JTable instance
        Font initialFont = new Font("Arial", Font.PLAIN, 12); // Define initial font
        testTable.setFont(initialFont); // Set font of test table
        testTable.setRowHeight(20); // Set row height of test table
        testTable.getTableHeader().setFont(initialFont); // Set font of test table header
        double factor = 1.5; // Define zoom factor
        this.reviewChangesSheetView.yourTable = testTable; // Set the test table in reviewChangesSheetView
        this.reviewChangesSheetView.zoomTable(factor); // Zoom the table

        Font tableFont = testTable.getFont(); // Get font of the table
        float expectedFontSize = 12 * (float) factor; // Calculate expected font size
        int expectedRowHeight = (int) (20 * factor); // Calculate expected row height

        assertEquals(expectedFontSize, tableFont.getSize2D(), 0.01); // Assert table font size
        assertEquals(expectedRowHeight, testTable.getRowHeight()); // Assert table row height
        assertEquals(expectedFontSize, testTable.getTableHeader().getFont().getSize2D(), 0.01); // Assert table header font size
    }

    /**
     * Tests the loadChanges method of the ReviewChangesSheetView class when no changes are found.
     */
    @Test
    public void testNoChangesFound() {
        // Setting up current and changes spreadsheets with the same data
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                this.currentSpreadSheet.setCellRawdata(i, j, "data " + i + "," + j);
                this.currentSpreadSheet.setCellValue(i, j, "data " + i + "," + j);
                this.changesSpreadSheet.setCellRawdata(i, j, "data " + i + "," + j);
                this.changesSpreadSheet.setCellValue(i, j, "data " + i + "," + j);
            }
        }

        // Check that an exception is thrown when no changes are found
        Exception exception = assertThrows(Exception.class, () -> {
            this.reviewChangesSheetView.loadChanges();
        });

        assertEquals("No changes found", exception.getMessage());
    }
}
