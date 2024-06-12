 package org.example.view;

 import static org.junit.jupiter.api.Assertions.assertEquals;

 import org.example.controller.IUserController;
 import org.example.controller.UserController;
 import org.example.model.*;
 import org.example.server.Server;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;

 import javax.swing.*;
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

         //check if user is registered first.
         try{
             this.controller.registerUser(this.user.getUsername(), this.user.getPassword());
         } catch (Exception e){
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

         //test if user is correctly logged into sheetview

         IUserController sheetController = this.sheetView.getController();
         assertEquals(this.controller, sheetController);

         IAppUser sheetUser = sheetController.getAppUser();
         //assertEquals(this.user, sheetUser);

         String username =sheetUser.getUsername();
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
         assertEquals(this.sheetView.selectedCells.getEndCol(), selectedCols[selectedCols.length-1]);
         assertEquals(this.sheetView.selectedCells.getEndRow(), selectedRows[selectedRows.length-1]);

         assertEquals(this.sheetView.selectedCells.getStartCol(), 0);
         assertEquals(this.sheetView.selectedCells.getStartRow(), 0);
         assertEquals(this.sheetView.selectedCells.getEndCol(), 4);
         assertEquals(this.sheetView.selectedCells.getEndRow(), 4);
     }




     @Test
     public void testChangeFormulaTextField() {
        String rawData1 = "hello";
        this.sheetView.changeFormulaTextField(rawData1);
        assertEquals(rawData1, this.sheetView.formulaTextField.getText());
         assertEquals("hello", this.sheetView.formulaTextField.getText());
     }

     @Test
     public void testHighlightCell() {
         int row = 0;
         int col = 0;;
         Color color = Color.PINK;
         this.sheetView.highlightCell(row, col, color);

         Set<Point> points = this.sheetView.highlightedCells.keySet();

         for(Point point : points){
             int pRow = point.x;
             int pCol = point.y-1;
             Color pColor = this.sheetView.highlightedCells.get(point);

             assertEquals(row, pRow);
             assertEquals(col, pCol);
             assertEquals(color, pColor);
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
     }



     @Test
     public void testGetTable(){
         JTable testTable = new JTable();
         this.sheetView.yourTable = testTable;

         assertEquals(testTable, this.sheetView.getTable());

     }
     @Test
     public void testGetController(){
         assertEquals(this.controller, this.sheetView.getController());
     }

//     @Test
//     public void testSave(){
//
//     }

     @Test
     public void testZoomTable(){
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

     }

     @Test
     public void testReturnView(){
         SheetView testView = this.sheetView.returnView();
         assertEquals(this.sheetView, testView);
     }

     public void testUpdateTable(){
         
     }

 }
