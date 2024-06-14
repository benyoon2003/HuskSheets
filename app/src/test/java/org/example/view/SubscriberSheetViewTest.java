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
     */
    @BeforeEach
    public void init() {
        this.login = new LoginView();
        this.testSpreadSheet = new Spreadsheet("test");
        this.subscriberSheetView = new SubscriberSheetView("Publisher", testSpreadSheet);
        this.controller = new UserController(login);

        // Check if user is registered first
        try {
            this.controller.registerUser(this.user.getUsername(), this.user.getPassword());
        } catch (Exception e) {
            try {
                this.controller.loginUser(this.user.getUsername(), this.user.getPassword());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        this.subscriberSheetView.addController(this.controller);
    }

    /**
     * Tests if the controller is correctly added to the subscriberSheetView and if the user is correctly logged in.
     * @throws Exception if there is an error during the test.
     */
    @Test
    public void testAddController() throws Exception {
        // Test if user is correctly logged into subscriberSheetView
        IUserController sheetController = this.subscriberSheetView.getController();
        assertEquals(this.controller, sheetController);

        IAppUser sheetUser = sheetController.getAppUser();
        String username = sheetUser.getUsername();
        assertEquals(this.user.getUsername(), username);

        String password = sheetUser.getPassword();
        assertEquals(this.user.getPassword(), password);
    }

    /**
     * Tests selecting a range of cells in the subscriberSheetView.
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
     */
    @Test
    public void testHighlightCell() {
        int row = 0;
        int col = 0;
        Color color = Color.PINK;
        this.subscriberSheetView.highlightCell(row, col, color);

        Set<Point> points = this.subscriberSheetView.highlightedCells.keySet();

        for (Point point : points) {
            int pRow = point.x;
            int pCol = point.y - 1;
            Color pColor = this.subscriberSheetView.highlightedCells.get(point);

            assertEquals(row, pRow);
            assertEquals(col, pCol);
            assertEquals(color, pColor);
        }
    }

    /**
     * Tests converting column numbers to Excel column names in the subscriberSheetView.
     */
    @Test
    public void testExcelColumnName() {
        int colNum = 1;
        String col = this.subscriberSheetView.getExcelColumnName(colNum);
        assertEquals("A", col);

        colNum = 4;
        col = this.subscriberSheetView.getExcelColumnName(colNum);
        assertEquals("D", col);

        colNum = 27;
        col = this.subscriberSheetView.getExcelColumnName(colNum);
        assertEquals("AA", col);
    }

    /**
     * Tests getting the table from the subscriberSheetView.
     */
    @Test
    public void testGetTable() {
        JTable testTable = new JTable();
        this.subscriberSheetView.yourTable = testTable;

        assertEquals(testTable, this.subscriberSheetView.getTable());
    }

    /**
     * Tests getting the controller from the subscriberSheetView.
     */
    @Test
    public void testGetController() {
        assertEquals(this.controller, this.subscriberSheetView.getController());
    }

    /**
     * Tests zooming the table in the subscriberSheetView.
     */
    @Test
    public void testZoomTable() {
        JTable testTable = new JTable();
        Font initialFont = new Font("Arial", Font.PLAIN, 12);
        testTable.setFont(initialFont);
        testTable.setRowHeight(20);
        testTable.getTableHeader().setFont(initialFont);
        double factor = 1.5;
        this.subscriberSheetView.yourTable = testTable;
        this.subscriberSheetView.zoomTable(factor);

        Font tableFont = testTable.getFont();
        float expectedFontSize = 12 * (float) factor;
        int expectedRowHeight = (int) (20 * factor);

        assertEquals(expectedFontSize, tableFont.getSize2D(), 0.01);
        assertEquals(expectedRowHeight, testTable.getRowHeight());
        assertEquals(expectedFontSize, testTable.getTableHeader().getFont().getSize2D(), 0.01);
    }
}
