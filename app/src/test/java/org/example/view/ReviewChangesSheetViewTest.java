package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.controller.IUserController;
import org.example.controller.UserController;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReviewChangesSheetViewTest {
    private ReviewChangesSheetView reviewChangesSheetView;
    private IUserController controller;
    private ISpreadsheet currentSpreadSheet;
    private ISpreadsheet changesSpreadSheet;
    private IAppUser user = new AppUser("John", "12345");
    private ILoginView login;

    @BeforeEach
    public void init() {
        this.login = new LoginView();
        this.currentSpreadSheet = new Spreadsheet("current");
        this.changesSpreadSheet = new Spreadsheet("changes");
        this.reviewChangesSheetView = new ReviewChangesSheetView(changesSpreadSheet, currentSpreadSheet);
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
        this.reviewChangesSheetView.addController(this.controller);
    }

    @Test
    public void testAddController() throws Exception {
        // Test if user is correctly logged into reviewChangesSheetView
        IUserController sheetController = this.reviewChangesSheetView.getController();
        assertEquals(this.controller, sheetController);

        IAppUser sheetUser = sheetController.getAppUser();
        String username = sheetUser.getUsername();
        assertEquals(this.user.getUsername(), username);

        String password = sheetUser.getPassword();
        assertEquals(this.user.getPassword(), password);
    }

    @Test
    public void testSelectedCells() {
        int[] selectedRows = {0, 1, 2, 3, 4};
        int[] selectedCols = {0, 1, 2, 3, 4};

        this.reviewChangesSheetView.selectedCells(selectedRows, selectedCols);
        assertEquals(this.reviewChangesSheetView.selectedCells.getStartCol(), selectedCols[0]);
        assertEquals(this.reviewChangesSheetView.selectedCells.getStartRow(), selectedRows[0]);
        assertEquals(this.reviewChangesSheetView.selectedCells.getEndCol(), selectedCols[selectedCols.length - 1]);
        assertEquals(this.reviewChangesSheetView.selectedCells.getEndRow(), selectedRows[selectedRows.length - 1]);

        assertEquals(this.reviewChangesSheetView.selectedCells.getStartCol(), 0);
        assertEquals(this.reviewChangesSheetView.selectedCells.getStartRow(), 0);
        assertEquals(this.reviewChangesSheetView.selectedCells.getEndCol(), 4);
        assertEquals(this.reviewChangesSheetView.selectedCells.getEndRow(), 4);
    }

    @Test
    public void testChangeFormulaTextField() {
        String rawData1 = "hello";
        this.reviewChangesSheetView.changeFormulaTextField(rawData1);
        assertEquals(rawData1, this.reviewChangesSheetView.formulaTextField.getText());
        assertEquals("hello", this.reviewChangesSheetView.formulaTextField.getText());
    }

    @Test
    public void testHighlightCell() {
        int row = 0;
        int col = 0;
        Color color = Color.PINK;
        this.reviewChangesSheetView.highlightCell(row, col, color);

        Set<Point> points = this.reviewChangesSheetView.highlightedCells.keySet();

        for (Point point : points) {
            int pRow = point.x;
            int pCol = point.y - 1;
            Color pColor = this.reviewChangesSheetView.highlightedCells.get(point);

            assertEquals(row, pRow);
            assertEquals(col, pCol);
            assertEquals(color, pColor);
        }
    }

    @Test
    public void testExcelColumnName() {
        int colNum = 1;
        String col = this.reviewChangesSheetView.getExcelColumnName(colNum);
        assertEquals("A", col);

        colNum = 4;
        col = this.reviewChangesSheetView.getExcelColumnName(colNum);
        assertEquals("D", col);

        colNum = 27;
        col = this.reviewChangesSheetView.getExcelColumnName(colNum);
        assertEquals("AA", col);
    }

    @Test
    public void testGetTable() {
        JTable testTable = new JTable();
        this.reviewChangesSheetView.yourTable = testTable;

        assertEquals(testTable, this.reviewChangesSheetView.getTable());
    }

    @Test
    public void testGetController() {
        assertEquals(this.controller, this.reviewChangesSheetView.getController());
    }

    @Test
    public void testZoomTable() {
        JTable testTable = new JTable();
        Font initialFont = new Font("Arial", Font.PLAIN, 12);
        testTable.setFont(initialFont);
        testTable.setRowHeight(20);
        testTable.getTableHeader().setFont(initialFont);
        double factor = 1.5;
        this.reviewChangesSheetView.yourTable = testTable;
        this.reviewChangesSheetView.zoomTable(factor);

        Font tableFont = testTable.getFont();
        float expectedFontSize = 12 * (float) factor;
        int expectedRowHeight = (int) (20 * factor);

        assertEquals(expectedFontSize, tableFont.getSize2D(), 0.01);
        assertEquals(expectedRowHeight, testTable.getRowHeight());
        assertEquals(expectedFontSize, testTable.getTableHeader().getFont().getSize2D(), 0.01);
    }

    // @Test
    // public void testLoadChanges() throws Exception {
    //     // Setting up current spreadsheet with initial data
    //     for (int i = 0; i < 5; i++) {
    //         for (int j = 0; j < 5; j++) {
    //             this.currentSpreadSheet.setCellRawdata(i, j, "initial " + i + "," + j);
    //             this.currentSpreadSheet.setCellValue(i, j, "initial " + i + "," + j);
    //         }
    //     }

    //     // Setting up changes spreadsheet with changed data
    //     for (int i = 0; i < 5; i++) {
    //         for (int j = 0; j < 5; j++) {
    //             this.changesSpreadSheet.setCellRawdata(i, j, "changed " + i + "," + j);
    //             this.changesSpreadSheet.setCellValue(i, j, "changed " + i + "," + j);
    //         }
    //     }

    //     // Load changes into the view
    //     this.reviewChangesSheetView.loadChanges();

    //     // Check that the changes have been applied correctly
    //     for (int i = 0; i < 5; i++) {
    //         for (int j = 0; j < 5; j++) {
    //             assertEquals("changed " + i + "," + j, this.currentSpreadSheet.getCellRawdata(i, j));
    //         }
    //     }
    // }

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
