package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;

import org.example.controller.IUserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.swing.*;

/**
 * Tests the methods within the HomeView class.
 */
public class HomeViewTest {
    private HomeView homeView; // Instance of HomeView to be tested
    private IUserController mockController; // Mocked controller to simulate IUserController behavior

    /**
     * Initializes the test environment by creating a HomeView instance and mocking the IUserController.
     * @author Vinay
     */
    @BeforeEach
    public void init() {
        homeView = new HomeView(); // Create a new instance of HomeView
        mockController = mock(IUserController.class); // Mock the IUserController
        homeView.addController(mockController); // Add the mocked controller to HomeView
    }

    /**
     * Tests the addController method of the HomeView class.
     * @author Vinay
     */
    @Test
    public void testAddController() {
        assertEquals(mockController, homeView.getController()); // Verify that the controller was added correctly
    }

    /**
     * Tests the createSheet method of the HomeView class.
     * @author Vinay
     */
    @Test
    public void testCreateSheet() {
        String sheetName = "TestSheet"; // Define a test sheet name

        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) { // Mock JOptionPane static methods
            mocked.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn(sheetName); // Return the test sheet name when showInputDialog is called

            // Trigger the action
            homeView.getCreateSheetButton().doClick(); // Simulate clicking the "create sheet" button

            // Verify that the controller's createNewServerSheet method was called with the correct name
            verify(mockController).createNewServerSheet(sheetName); // Check if the correct method was called on the controller
        }
    }

    /**
     * Tests the updateSavedSheets method of the HomeView class.
     * @author Vinay
     */
    @Test
    public void testUpdateSavedSheets() {
        // Mock the controller to return some sheets
        when(mockController.getSavedSheetsLocally()).thenReturn(List.of("LocalSheet1", "LocalSheet2")); // Mock local sheets
        when(mockController.getAppUserSheets()).thenReturn(List.of("ServerSheet1", "ServerSheet2")); // Mock server sheets
        when(mockController.getPublishersFromServer()).thenReturn(List.of("Publisher1", "Publisher2")); // Mock publishers

        // Call the method to update saved sheets
        homeView.updateSavedSheets(); // Update the saved sheets in the view

        // Verify the dropdowns have been populated correctly
        assertEquals(2, homeView.getOpenSheetDropdown().getItemCount()); // Check the number of items in the open sheet dropdown
        assertEquals(2, homeView.getPublishersDropdown().getItemCount()); // Check the number of items in the publishers dropdown

        assertEquals("ServerSheet1", homeView.getOpenSheetDropdown().getItemAt(0)); // Check the first item in the open sheet dropdown
        assertEquals("ServerSheet2", homeView.getOpenSheetDropdown().getItemAt(1)); // Check the second item in the open sheet dropdown

        assertEquals("Publisher1", homeView.getPublishersDropdown().getItemAt(0)); // Check the first item in the publishers dropdown
        assertEquals("Publisher2", homeView.getPublishersDropdown().getItemAt(1)); // Check the second item in the publishers dropdown
    }

    /**
     * Tests the displayErrorBox method of the HomeView class.
     * @author Vinay
     */
    @Test
    public void testDisplayErrorBox() {
        String errorMessage = "Test Error"; // Define a test error message

        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) { // Mock JOptionPane static methods
            homeView.displayErrorBox(errorMessage); // Call the method to display an error box

            // Verify the error message is displayed
            mocked.verify(() -> JOptionPane.showMessageDialog(homeView, errorMessage)); // Check if the error message was shown
        }
    }

    /**
     * Tests the disposeHomePage method of the HomeView class.
     * @author Vinay
     */
    @Test
    public void testDisposeHomePage() {
        homeView.disposeHomePage(); // Call the method to dispose the home page
        // Verify the home page is disposed
        assertEquals(false, homeView.isDisplayable()); // Check if the home page is no longer displayable
    }

    /**
     * Tests the makeVisible method of the HomeView class.
     * @author Vinay
     */
    @Test
    public void testMakeVisible() {
        homeView.makeVisible(); // Call the method to make the home page visible
        // Verify the home page is visible
        assertEquals(true, homeView.isVisible()); // Check if the home page is visible
    }
}
