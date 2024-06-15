package org.example.controller;

import org.example.model.*;
import org.example.view.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the methods within the user controller.
 */
public class UserControllerTest {

    @Mock
    private ILoginView loginView; // Mock object for the login view
    @Mock
    private IHomeView homeView; // Mock object for the home view
    @Mock
    private ISheetView sheetView; // Mock object for the sheet view
    @Mock
    private IAppUser appUser; // Mock object for the app user
    @Mock
    private ISpreadsheet spreadsheetModel; // Mock object for the spreadsheet model
    @Mock
    private ISelectedCells selectedCells; // Mock object for the selected cells
    @Mock
    private ServerEndpoint serverEndpoint; // Mock object for the server endpoint
    @InjectMocks
    private UserController userController; // Inject mocks into the UserController instance

    /**
     * Sets up the mock UserController.
     * @throws Exception irrelevant to actual tests
     * @author Vinay
     */
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        userController = new UserController(loginView); // Create a new UserController instance with the login view

        setField(userController, "homeView", homeView); // Set the home view field in UserController
        setField(userController, "sheetView", sheetView); // Set the sheet view field in UserController
        setField(userController, "appUser", appUser); // Set the app user field in UserController
        setField(userController, "spreadsheetModel", spreadsheetModel); // Set the spreadsheet model field in UserController
        setField(userController, "selectedCells", selectedCells); // Set the selected cells field in UserController
        setField(userController, "serverEndpoint", serverEndpoint); // Set the server endpoint field in UserController
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName); // Get the field by name
        field.setAccessible(true); // Make the field accessible
        field.set(target, value); // Set the field value
    }

    /**
     * Tests the registerUser method for a successful registration.
     * @throws Exception if there is an error during registration
     * @author Ben
     */
    @Test
    public void testRegisterUser_Success() throws Exception {
        when(serverEndpoint.register(any(IAppUser.class))).thenReturn(new Result(true, null, null)); // Mock successful registration

        userController.registerUser("username", "password"); // Call registerUser method

        verify(loginView).disposeLoginPage(); // Verify that disposeLoginPage is called
        assertNotNull(userController.getAppUser()); // Assert that appUser is not null
        verify(loginView, never()).displayErrorBox(anyString()); // Verify that displayErrorBox is not called
    }

    /**
     * Tests the registerUser method for a failed registration.
     * @author Ben
     */
    @Test
    public void testRegisterUser_Failure() {
        try {
            when(serverEndpoint.register(any(IAppUser.class))).thenReturn(new Result(false, "Error", null)); // Mock failed registration
            userController.registerUser("username", "password"); // Call registerUser method
            fail("Exception not thrown"); // Fail the test if no exception is thrown
        } catch (Exception e) {
            assertEquals("Error", e.getMessage()); // Assert that the exception message is "Error"
        }
        // appUser does not change after failed register
        assertEquals(userController.getAppUser(), appUser); // Assert that appUser does not change
    }

    /**
     * Tests the loginUser method for a successful login.
     * @throws Exception if there is an error during login
     * @author Ben
     */
    @Test
    public void testLoginUser_Success() throws Exception {
        when(serverEndpoint.login(any(IAppUser.class))).thenReturn(new Result(true, null, null)); // Mock successful login

        userController.loginUser("username", "password"); // Call loginUser method

        verify(loginView).disposeLoginPage(); // Verify that disposeLoginPage is called
        assertNotNull(userController.getAppUser()); // Assert that appUser is not null
        verify(loginView, never()).displayErrorBox(anyString()); // Verify that displayErrorBox is not called
    }

    /**
     * Tests the loginUser method for a failed login.
     * @author Ben
     */
    @Test
    public void testLoginUser_Failure() {
        try {
            when(serverEndpoint.login(any(IAppUser.class))).thenReturn(new Result(false, "Error", null)); // Mock failed login
            userController.loginUser("username", "password"); // Call loginUser method
            fail("Exception not thrown"); // Fail the test if no exception is thrown
        } catch (Exception e) {
            assertEquals("Error", e.getMessage()); // Assert that the exception message is "Error"
        }
        // Current appuser must not change after failed login
        assertEquals(userController.getAppUser(), appUser); // Assert that appUser does not change
    }

    /**
     * Tests the getPublishersFromServer method for a successful response.
     * @throws Exception if there is an error during the request
     * @author Tony
     */
    @Test
    public void testGetPublishersFromServer_Success() throws Exception {
        List<Argument> arguments = new ArrayList<>();
        arguments.add(new Argument("publisher1", null, null, null)); // Mock a successful response with one publisher
        when(serverEndpoint.getPublishers()).thenReturn(new Result(true, null, arguments)); // Mock server response

        List<String> publishers = userController.getPublishersFromServer(); // Call getPublishersFromServer method

        assertEquals(1, publishers.size()); // Assert that there is one publisher
        assertEquals("publisher1", publishers.get(0)); // Assert that the publisher name is "publisher1"
        verify(homeView, never()).displayErrorBox(anyString()); // Verify that displayErrorBox is not called
    }

    /**
     * Tests the getPublishersFromServer method for a failed response.
     * @throws Exception if there is an error during the request
     * @author Tony
     */
    @Test
    public void testGetPublishersFromServer_Failure() throws Exception {
        when(serverEndpoint.getPublishers()).thenReturn(new Result(false, "Error", null)); // Mock a failed response

        List<String> publishers = userController.getPublishersFromServer(); // Call getPublishersFromServer method

        assertTrue(publishers.isEmpty()); // Assert that the publishers list is empty
        verify(homeView).displayErrorBox("Error"); // Verify that displayErrorBox is called with "Error"
    }

    /**
     * Tests the createNewServerSheet method for a successful sheet creation.
     * @throws Exception if there is an error during the request
     * @author Tony
     */
    @Test
    public void testCreateNewServerSheet_Success() throws Exception {
        when(serverEndpoint.createSheet(anyString())).thenReturn(new Result(true, null, null));

        userController.createNewServerSheet("sheetName"); // Call createNewServerSheet method

        verify(homeView).disposeHomePage(); // Verify that disposeHomePage is called
        assertNotNull(userController.getSpreadsheetModel()); // Assert that spreadsheetModel is not null
        verify(homeView, never()).displayErrorBox(anyString()); // Verify that displayErrorBox is not called
    }

    /**
     * Tests the createNewServerSheet method for a failed sheet creation.
     * @throws Exception if there is an error during the request
     * @author Tony
     */
    @Test
    public void testCreateNewServerSheet_Failure() throws Exception {
        when(serverEndpoint.createSheet(anyString())).thenReturn(new Result(false, "Error", null)); // Mock failed sheet creation
        userController.createNewServerSheet("sheetName"); // Call createNewServerSheet method
        verify(homeView).displayErrorBox("Error"); // Verify that displayErrorBox is called with "Error"
    }

    /**
     * Tests the saveSheetToServer method for a successful save.
     * @throws Exception if there is an error during the request
     * @author Tony
     */
    @Test
    public void testSaveSheetToServer_Success() throws Exception {
        // Mocking the serverEndpoint to return a successful result
        when(serverEndpoint.updatePublished(anyString(), anyString(), anyString())).thenReturn(
                new Result(true, "Sheet updated successfully", new ArrayList<>())); // Mock successful save
        when(appUser.getUsername()).thenReturn("testUser"); // Mock app user username
        IReadOnlySpreadSheet sheet = mock(IReadOnlySpreadSheet.class); // Mock IReadOnlySpreadSheet
        userController.saveSheetToServer(sheet, "sheetName"); // Call saveSheetToServer method
        verify(sheetView, never()).displayMessage(anyString()); // Verify that displayMessage is not called
    }

    /**
     * Tests the saveSheetToServer method for a failed save.
     * @throws Exception if there is an error during the request
     * @author Tony
     */
    @Test
    public void testSaveSheetToServer_Failure() throws Exception {
        when(serverEndpoint.updatePublished(anyString(), anyString(), anyString())).thenReturn(
                new Result(false, "Error", null)); // Mock failed save
        when(appUser.getUsername()).thenReturn("testUser"); // Mock app user username
        IReadOnlySpreadSheet sheet = mock(IReadOnlySpreadSheet.class); // Mock IReadOnlySpreadSheet
        userController.saveSheetToServer(sheet, "sheetName"); // Call saveSheetToServer method
        verify(sheetView).displayMessage("Error"); // Verify that displayMessage is called with "Error"
    }

    /**
     * Tests the deleteSheetFromServer method for a failed deletion.
     * @throws Exception if there is an error during the request
     * @author Theo
     */
    @Test
    public void testDeleteSheetFromServer_Failure() throws Exception {
        when(serverEndpoint.deleteSheet(anyString(), anyString())).thenReturn(
                new Result(false, "Error", null)); // Mock failed deletion
        when(appUser.getUsername()).thenReturn("testUser"); // Mock app user username
        userController.deleteSheetFromServer("sheetName"); // Call deleteSheetFromServer method
        verify(homeView).displayErrorBox("Error"); // Verify that displayErrorBox is called with "Error"
    }

    /**
     * Tests the saveSheetLocally method of UserController.
     * Ensures that a sheet is correctly saved to a local file with the expected content.
     * @author Theo
     */
    @Test
    public void testSaveSheetLocally() {
        Spreadsheet sheet = new Spreadsheet("Test"); // Create a new Spreadsheet instance
        sheet.setCellValue(0, 0, "0"); // Set cell value at (0, 0)
        sheet.setCellValue(1, 0, "1"); // Set cell value at (1, 0)

        String path = "..\\sheets\\writeTestSheet.xml"; // Define the file path
        this.userController.saveSheetLocally(sheet, path); // Call saveSheetLocally method

        File file = new File(path); // Create a File object
        assertTrue(file.exists()); // Assert that the file exists

        try {
            List<String> lines = Files.readAllLines(Paths.get(path)); // Read all lines from the file
            for (String line : lines) {
                line.trim(); // Trim each line
            }
            assertTrue(lines.contains("<sheet name=\"writeTestSheet\">")); // Assert that the file contains the sheet name
            assertTrue(lines.contains("    <cell col=\"0\" row=\"0\">0</cell>")); // Assert that the file contains the cell value
            assertTrue(lines.contains("    <cell col=\"0\" row=\"1\">1</cell>")); // Assert that the file contains the cell value
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if an exception occurs
        }
    }
    
    /**
     * Tests the openSheetLocally method of UserController.
     * Ensures that a sheet is correctly opened from a local file and the content matches the expected values.
     * @author Theo
     */
    @Test
    public void testOpenSheetLocally() {
        File file = new File("..\\sheets\\readTestSheet.xml"); // Create a File object
        this.userController.openSheetLocally(file.getAbsolutePath()); // Call openSheetLocally method

        ISpreadsheet sheet = this.userController.getSpreadsheetModel(); // Get the spreadsheet model
        assertEquals("6", sheet.getCellValue(0, 0)); // Assert that the cell value matches
        assertEquals("7", sheet.getCellValue(1, 0)); // Assert that the cell value matches
        assertEquals("8", sheet.getCellValue(2, 0)); // Assert that the cell value matches
        assertEquals("9", sheet.getCellValue(3, 0)); // Assert that the cell value matches
        assertEquals("10", sheet.getCellValue(4, 0)); // Assert that the cell value matches
        assertEquals("11", sheet.getCellValue(5, 0)); // Assert that the cell value matches
        assertEquals("12", sheet.getCellValue(6, 0)); // Assert that the cell value matches
        assertEquals("13", sheet.getCellValue(7, 0)); // Assert that the cell value matches
        assertEquals("", sheet.getCellValue(0, 1)); // Assert that the cell value matches
    }

    /**
     * Tests the getSavedSheetsLocally method of UserController.
     * Ensures that the list of saved sheets locally is not empty and contains the expected sheet name.
     * @author Theo
     */
    @Test
    public void testGetSavedSheetsLocally() {
        List<String> sheets = this.userController.getSavedSheetsLocally(); // Call getSavedSheetsLocally method
        assertFalse(sheets.isEmpty()); // Assert that the sheets list is not empty
        assertTrue(() -> {
            for (String sheet : sheets) {
                if (sheet.equals("readTestSheet.xml")) { // Check if the sheet name matches
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Tests the updateSelectedCells method of UserController.
     * Ensures that the selected cells are updated with the specified value.
     * 
     * @throws Exception if there is an error during the test.
     * @author Vinay
     */
    @Test
    public void testUpdateSelectedCells() throws Exception {
        when(selectedCells.getStartRow()).thenReturn(0); // Mock start row
        when(selectedCells.getEndRow()).thenReturn(1); // Mock end row
        when(selectedCells.getStartCol()).thenReturn(0); // Mock start column
        when(selectedCells.getEndCol()).thenReturn(1); // Mock end column

        userController.updateSelectedCells("value"); // Call updateSelectedCells method

        verify(spreadsheetModel, times(4)).setCellRawdata(anyInt(), anyInt(), eq("value")); // Verify that setCellRawdata is called 4 times
    }

    /**
     * Tests the changeSpreadSheetValueAt method.
     * @author Ben
     */
    @Test
    public void testChangeSpreadSheetValueAt() {
        int row = 1;
        int col = 1;
        String value = "=SUM(A1:B2)"; // Define the formula value

        userController.changeSpreadSheetValueAt(row, col, value); // Call changeSpreadSheetValueAt method

        verify(spreadsheetModel).setCellRawdata(row, col, value); // Verify that setCellRawdata is called
        verify(spreadsheetModel).setCellValue(row, col, value); // Verify that setCellValue is called
        verify(spreadsheetModel).evaluateFormula(value); // Verify that evaluateFormula is called
        verify(sheetView).updateTable(); // Verify that updateTable is called
    }

    /**
     * Tests the cutCell method.
     * @author Vinay
     */
    @Test
    public void testCutCell() {
        int row = 1;
        int col = 1;
        String value = "cutValue"; // Define the cut value

        when(spreadsheetModel.getCellRawdata(row, col)).thenReturn(value); // Mock the cell raw data

        userController.cutCell(row, col); // Call cutCell method

        verify(spreadsheetModel).setCellValue(row, col, ""); // Verify that setCellValue is called
        verify(sheetView).updateTable(); // Verify that updateTable is called
        assertEquals(value, userController.getClipboardContent()); // Assert that clipboard content matches
        assertTrue(userController.isCutOperation()); // Assert that cut operation is true
    }

    /**
     * Tests the copyCell method.
     * @author Vinay
     */
    @Test
    public void testCopyCell() {
        int row = 1;
        int col = 1;
        String value = "copyValue"; // Define the copy value

        when(spreadsheetModel.getCellRawdata(row, col)).thenReturn(value); // Mock the cell raw data

        userController.copyCell(row, col); // Call copyCell method

        assertEquals(value, userController.getClipboardContent()); // Assert that clipboard content matches
        assertFalse(userController.isCutOperation()); // Assert that cut operation is false
    }

    /**
     * Tests the getPercentile method.
     * @author Theo
     */
    @Test
    public void testGetPercentile() {
        int row = 1;
        int col = 1;
        String value = "0.75"; // Define the percentile value

        when(spreadsheetModel.getCellValue(row, col)).thenReturn(value); // Mock the cell value

        userController.getPercentile(row, col); // Call getPercentile method

        verify(spreadsheetModel).setCellValue(row, col, "75.0%"); // Verify that setCellValue is called with formatted percentile
    }

    /**
     * Tests the applyConditionalFormatting method.
     * @throws Exception if there is an error during the application
     * @author Vinay
     */
    @Test
    public void testApplyConditionalFormatting() throws Exception {
        Cell[][] cells = new Cell[100][100];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                cells[i][j] = new Cell(); // Initialize cells
            }
        }
        when(spreadsheetModel.getCellsObject()).thenReturn(cells); // Mock the cells object
        when(spreadsheetModel.getRows()).thenReturn(100); // Mock the number of rows
        when(spreadsheetModel.getCols()).thenReturn(100); // Mock the number of columns

        userController.applyConditionalFormatting(); // Call applyConditionalFormatting method

        verify(sheetView, times(10000)).highlightCell(anyInt(), anyInt(), any(Color.class)); // Verify that highlightCell is called 10000 times
        verify(sheetView).updateTable(); // Verify that updateTable is called
    }

    /**
     * Tests the setSelectedCells method.
     * @author Vinay
     */
    @Test
    public void testSetSelectedCells() {
        userController.setSelectedCells(new int[] { 1, 3 }, new int[] { 5, 10 }); // Call setSelectedCells method
        assertEquals(userController.getSelectedStartRow(), 1); // Assert that start row matches
        assertEquals(userController.getSelectedEndRow(), 3); // Assert that end row matches
        assertEquals(userController.getSelectedStartCol(), 4); // Assert that start column matches
        assertEquals(userController.getSelectedEndCol(), 9); // Assert that end column matches
    }
}
