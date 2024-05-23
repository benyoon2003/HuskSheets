// package org.example.view;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.example.controller.IUserController;
// import org.example.controller.MockUserController;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// public class SheetViewTest {
//     private MockSheetView sheetView;

//     @BeforeEach
//     public void init() {
//         this.sheetView = new MockSheetView();
//     }

//     @Test
//     public void testAddController() {
//         IUserController controller = new MockUserController();
//         this.sheetView.addController(controller);
            
//         assertEquals(this.sheetView.toString(), "Controller added\n");
//     }

//     @Test
//     public void testMakeVisible() {
//         this.sheetView.makeVisible();
//         assertEquals(this.sheetView.toString(), "Sheet is now visible\n");
//     }

//     @Test
//     public void testDisplayMessage() {
//         this.sheetView.displayMessage("This is one message");
//         this.sheetView.displayMessage("This is another message");

//         assertEquals(this.sheetView.toString(), "This is one message\nThis is another message\n");
//     }
// }
