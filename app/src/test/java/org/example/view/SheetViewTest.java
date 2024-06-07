// package org.example.view;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.example.controller.IUserController;
// import org.example.controller.MockUserController;
// import org.example.controller.UserController;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// public class SheetViewTest {
//     private MockSheetView sheetView;
//     private IUserController controller;

//     @BeforeEach
//     public void init() {
//         this.sheetView = new MockSheetView();
//         this.controller = new UserController(new LoginView());
//         this.sheetView.addController(this.controller);
//     }

//     @Test
//     public void testAddController() {
//         assertEquals("Controller added\n", this.sheetView.toString());
//     }

//     @Test
//     public void testMakeVisible() {
//         this.sheetView.makeVisible();
//         assertEquals("Sheet is now visible\n", this.sheetView.toString());
//     }

//     @Test
//     public void testDisplayMessage() {
//         this.sheetView.displayMessage("This is one message");
//         this.sheetView.displayMessage("This is another message");

//         assertEquals("This is one message\nThis is another message\n", this.sheetView.toString());
//     }

//     @Test
//     public void testUpdateTable() {
//         this.controller.createNewSheet("Test");
//         this.sheetView.updateTable();
//         assertEquals("Table updated with latest changes\n", this.sheetView.toString());
//     }

//     @Test
//     public void testChangeFormulaTextField() {
//         this.sheetView.changeFormulaTextField("= raw data");
//         assertEquals("Formula text field: = raw data\n", this.sheetView.toString());
//     }

//     @Test
//     public void testGetExcelColumnName() {
//         assertEquals("A", this.sheetView.getExcelColumnName(1));
//         assertEquals("Z", this.sheetView.getExcelColumnName(26));
//         assertEquals("AA", this.sheetView.getExcelColumnName(27));
//         assertEquals("", this.sheetView.getExcelColumnName(0));
//     }
// }
