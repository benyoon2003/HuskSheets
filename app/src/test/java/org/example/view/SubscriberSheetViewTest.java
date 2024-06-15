package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.controller.IUserController;
import org.example.controller.UserController;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Unit test for the SubscriberSheetView class.
 */
public class SubscriberSheetViewTest {
    private SubscriberSheetView subscriberSheetView;
    private IUserController controller;
    private ISpreadsheet testSpreadSheet;
    private IAppUser user = new AppUser("John", "12345");
    private ILoginView login;

    /**
     * Initializes the test environment before each test.
     * @author Vinay
     */
    @BeforeEach
    public void init() {
        this.login = new LoginView(); // Initialize a new LoginView instance
        this.testSpreadSheet = new Spreadsheet("test"); // Initialize a new Spreadsheet instance with the name test
        this.subscriberSheetView = new SubscriberSheetView("Publisher", testSpreadSheet); // Initialize a new SubscriberSheetView with Publisher and test spreadsheet
        this.controller = new UserController(login); // Initialize a new UserController with the login view

        // Check if user is registered first
        try {
            this.controller.registerUser(this.user.getUsername(), this.user.getPassword()); // Attempt to register the user
        } catch (Exception e) {
            try {
                this.controller.loginUser(this.user.getUsername(), this.user.getPassword()); // If registration fails, attempt to log in the user
            } catch (Exception e1) {
                e1.printStackTrace(); // Print stack trace if login fails
            }
        }
        this.subscriberSheetView.addController(this.controller); // Add the controller to the subscriberSheetView
    }

    /**
     * Tests if the controller is correctly added to the subscriberSheetView and if the user is correctly logged in.
     * @throws Exception if there is an error during the test.
     * @author Vinay
     */
    @Test
    public void testAddController() throws Exception {
        // Test if user is correctly logged into subscriberSheetView
        IUserController sheetController = this.subscriberSheetView.getController(); // Get the controller from the subscriberSheetView
        assertEquals(this.controller, sheetController); // Assert that the controller is correctly added

        IAppUser sheetUser = sheetController.getAppUser(); // Get the app user from the controller
        String username = sheetUser.getUsername(); // Get the username of the app user
        assertEquals(this.user.getUsername(), username); // Assert that the username matches the expected value

        String password = sheetUser.getPassword(); // Get the password of the app user
        assertEquals(this.user.getPassword(), password); // Assert that the password matches the expected value
    }

    /**
     * Tests selecting a range of cells in the subscriberSheetView.
     * @author Ben
     */
    @Test
    public void testSelectedCells() {
        int[] selectedRows = {0, 1, 2, 3, 4};
        int[] selectedCols = {0, 1, 2, 3, 4};

        this.subscriberSheetView.selectedCells(selectedRows, selectedCols);
        assertEquals(this.subscriberSheetView.selectedCells.getStartCol(), selectedCols[0]);
        assertEquals(this.subscriberSheetView.selectedCells.getStartRow(), selectedRows[0]);
        assertEquals(this.subscriberSheetView.selectedCells.getEndCol(), selectedCols[selectedCols.length - 1]);
        assertEquals(this.subscriberSheetView.selectedCells.getEndRow(), selectedRows[selectedRows.length - 1]);

        assertEquals(this.subscriberSheetView.selectedCells.getStartCol(), 0);
        assertEquals(this.subscriberSheetView.selectedCells.getStartRow(), 0);
        assertEquals(this.subscriberSheetView.selectedCells.getEndCol(), 4);
        assertEquals(this.subscriberSheetView.selectedCells.getEndRow(), 4);
    }

    /**
     * Tests changing the formula text field in the subscriberSheetView.
     * @author Ben
     */
    @Test
    public void testChangeFormulaTextField() {
        String rawData1 = "hello";
        this.subscriberSheetView.changeFormulaTextField(rawData1);
        assertEquals(rawData1, this.subscriberSheetView.formulaTextField.getText());
        assertEquals("hello", this.subscriberSheetView.formulaTextField.getText());
    }

    /**
     * Tests highlighting a cell in the subscriberSheetView.
     * @author Vinay
     */
    @Test
    public void testHighlightCell() {
        int row = 0; // Row to highlight
        int col = 0; // Column to highlight
        Color color = Color.PINK; // Color to highlight the cell
        this.subscriberSheetView.highlightCell(row, col, color); // Highlight the cell at (0,0) with pink color

        Set<Point> points = this.subscriberSheetView.highlightedCells.keySet(); // Get all highlighted cell points

        for (Point point : points) { // Iterate over all highlighted cell points
            int pRow = point.x; // Get the row of the highlighted cell
            int pCol = point.y - 1; // Get the column of the highlighted cell (adjusting for 1-based index)
            Color pColor = this.subscriberSheetView.highlightedCells.get(point); // Get the color of the highlighted cell

            assertEquals(row, pRow); // Assert that the row matches the expected row
            assertEquals(col, pCol); // Assert that the column matches the expected column
            assertEquals(color, pColor); // Assert that the color matches the expected color
        }
    }

    /**
     * Tests converting column numbers to Excel column names in the subscriberSheetView.
     * @author Vinay
     */
    @Test
    public void testExcelColumnName() {
        int colNum = 1; // Column number to convert
        String col = this.subscriberSheetView.getExcelColumnName(colNum); // Get the Excel column name for column 1
        assertEquals("A", col); // Assert that the column name is "A"

        colNum = 4; // Column number to convert
        col = this.subscriberSheetView.getExcelColumnName(colNum); // Get the Excel column name for column 4
        assertEquals("D", col); // Assert that the column name is "D"

        colNum = 27; // Column number to convert
        col = this.subscriberSheetView.getExcelColumnName(colNum); // Get the Excel column name for column 27
        assertEquals("AA", col); // Assert that the column name is "AA"
    }

    /**
     * Tests getting the table from the subscriberSheetView.
     * @author Vinay
     */
    @Test
    public void testGetTable() {
        JTable testTable = new JTable(); // Create a new JTable instance
        this.subscriberSheetView.yourTable = testTable; // Set the subscriberSheetView's table to the test table

        assertEquals(testTable, this.subscriberSheetView.getTable()); // Assert that the getTable method returns the correct table
    }

    /**
     * Tests getting the controller from the subscriberSheetView.
     * @author Vinay
     */
    @Test
    public void testGetController() {
        assertEquals(this.controller, this.subscriberSheetView.getController()); // Assert that the getController method returns the correct controller
    }

    /**
     * Tests zooming the table in the subscriberSheetView.
     * @author Vinay
     */
    @Test
    public void testZoomTable() {
        JTable testTable = new JTable(); // Create a new JTable instance
        Font initialFont = new Font("Arial", Font.PLAIN, 12); // Create an initial font
        testTable.setFont(initialFont); // Set the table's font to the initial font
        testTable.setRowHeight(20); // Set the table's row height
        testTable.getTableHeader().setFont(initialFont); // Set the table header's font to the initial font
        double factor = 1.5; // Zoom factor
        this.subscriberSheetView.yourTable = testTable; // Set the subscriberSheetView's table to the test table
        this.subscriberSheetView.zoomTable(factor); // Zoom the table by the factor

        Font tableFont = testTable.getFont(); // Get the table's font after zooming
        float expectedFontSize = 12 * (float) factor; // Calculate the expected font size
        int expectedRowHeight = (int) (20 * factor); // Calculate the expected row height

        assertEquals(expectedFontSize, tableFont.getSize2D(), 0.01); // Assert that the font size matches the expected font size
        assertEquals(expectedRowHeight, testTable.getRowHeight()); // Assert that the row height matches the expected row height
        assertEquals(expectedFontSize, testTable.getTableHeader().getFont().getSize2D(), 0.01); // Assert that the table header font size matches the expected font size
    }
}
