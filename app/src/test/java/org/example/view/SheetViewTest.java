package org.example.view;

import static org.junit.jupiter.api.Assertions.*;

import org.example.controller.IUserController;
import org.example.controller.UserController;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Set;

public class SheetViewTest {
    private SheetView sheetView;
    private IUserController controller;
    private ISpreadsheet testSpreadSheet;
    private IAppUser user = new AppUser("John", "12345");
    private ILoginView login;

    @BeforeEach
    public void init() {
        this.login = new LoginView();
        this.testSpreadSheet = new Spreadsheet("test");
        this.sheetView = new SheetView(testSpreadSheet);
        this.controller = new UserController(login);

        // Check if user is registered first.
        try {
            this.controller.registerUser(this.user.getUsername(), this.user.getPassword());
        } catch (Exception e) {
            try {
                this.controller.loginUser(this.user.getUsername(), this.user.getPassword());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        this.sheetView.addController(this.controller);
    }

    @Test
    public void testAddController() throws Exception {
        // Test if user is correctly logged into sheetView
        IUserController sheetController = this.sheetView.getController();
        assertEquals(this.controller, sheetController);

        IAppUser sheetUser = sheetController.getAppUser();
        assertNotNull(sheetUser);

        String username = sheetUser.getUsername();
        assertEquals(this.user.getUsername(), username);

        String password = sheetUser.getPassword();
        assertEquals(this.user.getPassword(), password);
    }

    @Test
    public void testSelectedCells() {
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

        // Test selecting a different range
        int[] newSelectedRows = {2, 3};
        int[] newSelectedCols = {1, 2};

        this.sheetView.selectedCells(newSelectedRows, newSelectedCols);
        assertEquals(this.sheetView.selectedCells.getStartCol(), newSelectedCols[0]);
        assertEquals(this.sheetView.selectedCells.getStartRow(), newSelectedRows[0]);
        assertEquals(this.sheetView.selectedCells.getEndCol(), newSelectedCols[newSelectedCols.length - 1]);
        assertEquals(this.sheetView.selectedCells.getEndRow(), newSelectedRows[newSelectedRows.length - 1]);
    }

    @Test
    public void testChangeFormulaTextField() {
        String rawData1 = "hello";
        this.sheetView.changeFormulaTextField(rawData1);
        assertEquals(rawData1, this.sheetView.formulaTextField.getText());
        assertEquals("hello", this.sheetView.formulaTextField.getText());

        // Test with a more complex formula
        String formula = "=SUM(A1:A10)";
        this.sheetView.changeFormulaTextField(formula);
        assertEquals(formula, this.sheetView.formulaTextField.getText());
    }

    @Test
    public void testHighlightCell() {
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

        // Test highlighting a different cell
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

        // Test with a higher column number
        colNum = 703;
        col = this.sheetView.getExcelColumnName(colNum);
        assertEquals("AAA", col);

        colNum = 18278;
        col = this.sheetView.getExcelColumnName(colNum);
        assertEquals("ZZZ", col);
    }

    @Test
    public void testGetTable() {
        JTable testTable = new JTable();
        this.sheetView.yourTable = testTable;

        assertEquals(testTable, this.sheetView.getTable());

        // Test with a different table
        JTable newTable = new JTable();
        this.sheetView.yourTable = newTable;

        assertEquals(newTable, this.sheetView.getTable());
    }

    @Test
    public void testGetController() {
        assertEquals(this.controller, this.sheetView.getController());

        // Test with a different controller
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

        // Test zooming out
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

    @Test
    public void testReturnView() {
        SheetView testView = this.sheetView.returnView();
        assertEquals(this.sheetView, testView);
    }

    @Test
    public void testInitializeData() {
        Object[][] data = sheetView.initializeData();
        for (Cell[] row : testSpreadSheet.getCellsObject()) {
            for (Cell cell : row) {
                assertEquals(cell.getValue(), data[cell.getRow()][cell.getCol()]);
            }
        }
    }

//    @Test
//    public void testLoadChanges(){
//        Spreadsheet subscriberTestSpreadsheet = new Spreadsheet(testSpreadSheet.getCells(), testSpreadSheet.getName());
//        ISheetView newView = new ReviewChangesSheetView(testSpreadSheet, subscriberTestSpreadsheet);
//        newView.addController(this.controller);
//        try{
//            this.controller.setCurrentSheet(newView);
//            newView.loadChanges();
//        } catch (Exception e){
//            assertEquals(testSpreadSheet, this.controller.getSpreadsheetModel());
//        }
//
//
//    }

    @Test
    public void testSetRowHeaders(){
        DefaultTableModel sheetTable = (DefaultTableModel) sheetView.getTable().getModel();
        sheetView.setRowHeaders(sheetTable);
        DefaultTableModel compareTable = new DefaultTableModel();
        for(int i = 0; i < sheetView.cells.getRows(); i++){
            compareTable.setValueAt(i, i, 0);
        }

        for(int i = 0; i < sheetView.cells.getRows(); i++){
            assertEquals(compareTable.getValueAt(1, 0), sheetTable.getValueAt(i, 0));
        }
    }

}
