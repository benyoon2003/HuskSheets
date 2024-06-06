// package org.example.controller;

// import org.example.model.Spreadsheet;
// import org.example.view.IHomeView;
// import org.example.view.ILoginView;
// import org.example.view.ISheetView;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.util.List;
// import java.io.File;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.Mockito.mock;

// public class UserControllerTest {
//     private IUserController controller;

//     private ILoginView loginViewMock;
//     private IHomeView homeViewMock;
//     private ISheetView sheetView;
//     private Spreadsheet sheet;

//     @BeforeEach
//     public void setUp() {
//         loginViewMock = mock(ILoginView.class);
//         homeViewMock = mock(IHomeView.class);
//         controller = new UserController(loginViewMock);
//         sheetView = mock(ISheetView.class);
//         sheet = new Spreadsheet("controllerTest");

//         this.controller.loginUser("test", "test");
//         this.controller.createNewSheet("controllerTest");
//     }

//     @Test
//     public void testSetCurrentSheetAndGetCurrentSheet() {
//         controller.setCurrentSheet(sheetView);
//         assertEquals(controller.getCurrentSheet(), sheetView);
//     }

//     @Test
//     public void testOpenHomeView() {

//     }

//     @Test
//     public void testSaveSheet() {
//         this.sheet.setCellValue(0, 0, "0");
//         this.sheet.setCellValue(1, 0, "1");
//         this.sheet.setCellValue(2, 0, "2");

//         String path = "..\\sheets\\controllerTest.xml";
//         this.controller.saveSheet(this.sheet, path);

//         File file = new File(path);
//         assertTrue(file.exists());

//         try {
//             List<String> lines = Files.readAllLines(Paths.get(path));
//             assertTrue(lines.contains("<sheet name=\"controllerTest\">"));
//             assertTrue(lines.contains("    <cell col=\"0\" row=\"0\">0</cell>"));
//             assertTrue(lines.contains("    <cell col=\"0\" row=\"1\">1</cell>"));
//         } catch (IOException e) {
//         }
//     }

//     @Test
//     public void testHandleToolbar() {
//     }

//     @Test
//     public void testSelectedCells() {
//     }

//     @Test
//     public void testOpenSheet() {
//     }

//     @Test
//     public void testGetSavedSheets() {
//     }

//     @Test
//     public void testDeleteSheet() {
//     }

//     @Test
//     public void testGetHomeView() {
//     }

//     @Test
//     public void testChangeSpreadsheetValueAt() {
//     }

//     @Test
//     public void testEvaluateFormula() {
//     }

//     @Test
//     public void testCutCell() {
//     }

//     @Test
//     public void testCopyCell() {
//     }

//     @Test
//     public void testPasteCell() {
//     }

//     @Test
//     public void testGetPercentile() {
//     }

//     @Test
//     public void testSaveSheetToServer() {
//         this.sheet.setCellValue(0, 0, "0");
//         this.sheet.setCellRawdata(0, 0, "0");
//         this.sheet.setCellValue(1, 0, "1");
//         this.sheet.setCellRawdata(1, 0, "1");
//         this.sheet.setCellValue(2, 0, "2");
//         this.sheet.setCellRawdata(2, 0, "2");

//         this.controller.saveSheetToServer(this.sheet, "controllerTest");
//     }

//     @Test
//     public void testDeleteSheetFromServer() {
//     }

//     @Test
//     public void testHandleReferencingCell() {
//     }

//     @Test
//     public void testGetServerSheets() {
//     }

//     @Test
//     public void testOpenServerSheet() {
//     }

//     @Test
//     public void testGetSelectedRowZeroIndex() {
//     }

//     @Test
//     public void testGetSelectedColZeroIndex() {
//     }

//     @Test
//     public void testGetFormula() {
//     }

//     @Test
//     public void testRegisterUser() {
//     }

//     @Test
//     public void testLoginUser() {
//     }
// }
