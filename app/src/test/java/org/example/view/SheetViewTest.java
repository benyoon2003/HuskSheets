package org.example.view;

import static org.junit.jupiter.api.Assertions.*;

import org.example.controller.IUserController;
import org.example.controller.UserController;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Unit test for the SheetView class.
 */
public class SheetViewTest {
    private SheetView sheetView;
    private IUserController controller;
    private ISpreadsheet testSpreadSheet;
    private IAppUser user = new AppUser("John", "12345");
    private ILoginView login;

    /**
     * Initialize the test environment before each test.
     */
    @BeforeEach
    public void init() {
        this.login = new LoginView();
        this.testSpreadSheet = new Spreadsheet("test");
        this.sheetView = new SheetView(testSpreadSheet);
        this.controller = new UserController(login);

        // Check if user is registered first, if not, log in the user.
        try {
            this.controller.registerUser(this.user.getUsername(), this.user.getPassword());
        } catch (Exception e) {
            try {
                this.controller.loginUser(this.user.getUsername(), this.user.getPassword());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        // Add the controller to the sheet view.
        this.sheetView.addController(this.controller);
    }

    /**
     * Test if the controller is correctly added to the sheet view and if the user is correctly logged in.
     * @throws Exception if there is an error during the test.
     */
    @Test
    public void testAddController() throws Exception {
        // Test if the controller is correctly added to the sheet view.
        IUserController sheetController = this.sheetView.getController();
        assertEquals(this.controller, sheetController);

        // Check if the logged-in user is correctly associated with the controller.
        IAppUser sheetUser = sheetController.getAppUser();
        assertNotNull(sheetUser);

        String username = sheetUser.getUsername();
        assertEquals(this.user.getUsername(), username);

        String password = sheetUser.getPassword();
        assertEquals(this.user.getPassword(), password);
    }

    /**
     * Test selecting a range of cells in the sheet view.
     */
    @Test
    public void testSelectedCells() {
        // Test selecting a range of cells.
        int[] selectedRows = {0, 1, 2, 3, 4};
        int[] selectedCols = {0, 1, 2, 3, 4};

        this.sheetView.selectedCells(selectedRows, selectedCols);
        assertEquals(this.sheetView.selectedCells.getStartCol(), selectedCols[0]);
        assertEquals(this.sheetView.selectedCells.getStartRow(), selectedRows[0]);
        assertEquals(this.sheetView.selectedCells.getEndCol(), selectedCols[selectedCols.length - 1]);
        assertEquals(this.sheetView.selectedCells.getEndRow(), selectedRows[selectedRows.length - 1]);

        assertEquals(this.sheetView.selectedCells.getStartCol(), 0);
        assertEquals(this.sheetView.selectedCells.getStartRow(), 0);
        assertEquals(this.sheetView.selectedCells.getEndCol(), 4);
        assertEquals(this.sheetView.selectedCells.getEndRow(), 4);

        // Test selecting a different range.
        int[] newSelectedRows = {2, 3};
        int[] newSelectedCols = {1, 2};

        this.sheetView.selectedCells(newSelectedRows, newSelectedCols);
        assertEquals(this.sheetView.selectedCells.getStartCol(), newSelectedCols[0]);
        assertEquals(this.sheetView.selectedCells.getStartRow(), newSelectedRows[0]);
        assertEquals(this.sheetView.selectedCells.getEndCol(), newSelectedCols[newSelectedCols.length - 1]);
        assertEquals(this.sheetView.selectedCells.getEndRow(), newSelectedRows[newSelectedRows.length - 1]);
    }

    /**
     * Test changing the formula text field in the sheet view.
     */
    @Test
    public void testChangeFormulaTextField() {
        // Test changing the formula text field.
        String rawData1 = "hello";
        this.sheetView.changeFormulaTextField(rawData1);
        assertEquals(rawData1, this.sheetView.formulaTextField.getText());
        assertEquals("hello", this.sheetView.formulaTextField.getText());

        // Test with a more complex formula.
        String formula = "=SUM(A1:A10)";
        this.sheetView.changeFormulaTextField(formula);
        assertEquals(formula, this.sheetView.formulaTextField.getText());
    }

    /**
     * Test highlighting a cell in the sheet view.
     */
    @Test
    public void testHighlightCell() {
        // Test highlighting a cell.
        int row = 0;
        int col = 0;
        Color color = Color.PINK;
        this.sheetView.highlightCell(row, col, color);

        Set<Point> points = this.sheetView.highlightedCells.keySet();
        assertTrue(points.contains(new Point(row, col + 1)));

        for (Point point : points) {
            int pRow = point.x;
            int pCol = point.y - 1;
            Color pColor = this.sheetView.highlightedCells.get(point);

            assertEquals(row, pRow);
            assertEquals(col, pCol);
            assertEquals(color, pColor);
        }

        // Test highlighting a different cell.
        int newRow = 2;
        int newCol = 2;
        Color newColor = Color.YELLOW;
        this.sheetView.highlightCell(newRow, newCol, newColor);

        points = this.sheetView.highlightedCells.keySet();
        assertTrue(points.contains(new Point(newRow, newCol + 1)));

        for (Point point : points) {
            int pRow = point.x;
            int pCol = point.y - 1;
            Color pColor = this.sheetView.highlightedCells.get(point);

            if (pRow == newRow && pCol == newCol) {
                assertEquals(newColor, pColor);
            }
        }
    }

    /**
     * Test converting column numbers to Excel column names.
     */
    @Test
    public void testExcelColumnName() {
        int colNum = 1;
        String col = this.sheetView.getExcelColumnName(colNum);
        assertEquals("A", col);

        colNum = 4;
        col = this.sheetView.getExcelColumnName(colNum);
        assertEquals("D", col);

        colNum = 27;
        col = this.sheetView.getExcelColumnName(colNum);
        assertEquals("AA", col);

        // Test with a higher column number.
        colNum = 703;
        col = this.sheetView.getExcelColumnName(colNum);
        assertEquals("AAA", col);

        colNum = 18278;
        col = this.sheetView.getExcelColumnName(colNum);
        assertEquals("ZZZ", col);
    }

    /**
     * Test getting the table from the sheet view.
     */
    @Test
    public void testGetTable() {
        JTable testTable = new JTable();
        this.sheetView.yourTable = testTable;

        assertEquals(testTable, this.sheetView.getTable());

        // Test with a different table.
        JTable newTable = new JTable();
        this.sheetView.yourTable = newTable;

        assertEquals(newTable, this.sheetView.getTable());
    }

    /**
     * Test getting the controller from the sheet view.
     */
    @Test
    public void testGetController() {
        assertEquals(this.controller, this.sheetView.getController());

        // Test with a different controller.
        IUserController newController = new UserController(this.login);
        IAppUser newUser = new AppUser("LeBron", "goat");
        try {
            newController.registerUser(newUser.getUsername(), newUser.getPassword());
        } catch (Exception e) {
            try {
                newController.loginUser(newUser.getUsername(), newUser.getPassword());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        this.sheetView.addController(newController);

        assertEquals(newController, this.sheetView.getController());
    }

    /**
     * Test zooming the table in and out.
     */
    @Test
    public void testZoomTable() {
        JTable testTable = new JTable();
        Font initialFont = new Font("Arial", Font.PLAIN, 12);
        testTable.setFont(initialFont);
        testTable.setRowHeight(20);
        testTable.getTableHeader().setFont(initialFont);
        double factor = 1.5;
        this.sheetView.yourTable = testTable;
        this.sheetView.zoomTable(factor);

        Font tableFont = testTable.getFont();
        float expectedFontSize = 12 * (float) factor;
        int expectedRowHeight = (int) (20 * factor);

        assertEquals(expectedFontSize, tableFont.getSize2D(), 0.01);
        assertEquals(expectedRowHeight, testTable.getRowHeight());
        assertEquals(expectedFontSize, testTable.getTableHeader().getFont().getSize2D(), 0.01);

        // Test zooming out.
        factor = 0.5;
        testTable.setFont(initialFont);
        testTable.setRowHeight(20);
        this.sheetView.zoomTable(factor);

        tableFont = testTable.getFont();
        expectedFontSize = 12 * (float) factor;
        expectedRowHeight = (int) (20 * factor);

        assertEquals(expectedFontSize, tableFont.getSize2D(), 0.01);
        assertEquals(expectedRowHeight, testTable.getRowHeight());
        assertEquals(expectedFontSize, testTable.getTableHeader().getFont().getSize2D(), 0.01);
    }

    /**
     * Test returning the sheet view.
     */
    @Test
    public void testReturnView() {
        SheetView testView = this.sheetView.returnView();
        assertEquals(this.sheetView, testView);
    }

    /**
     * Test initializing data in the sheet view.
     */
    @Test
    public void testInitializeData() {
        Object[][] data = sheetView.initializeData();
        for (Cell[] row : testSpreadSheet.getCellsObject()) {
            for (Cell cell : row) {
                assertEquals(cell.getValue(), data[cell.getRow()][cell.getCol()]);
            }
        }
    }
}
