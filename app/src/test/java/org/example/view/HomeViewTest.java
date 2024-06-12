package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.example.controller.IUserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.swing.*;

public class HomeViewTest {
    private HomeView homeView;
    private IUserController mockController;

    @BeforeEach
    public void init() {
        homeView = new HomeView();
        mockController = mock(IUserController.class);
        homeView.addController(mockController);
    }

    @Test
    public void testAddController() {
        assertEquals(mockController, homeView.getController());
    }

    @Test
    public void testCreateSheet() {
        String sheetName = "TestSheet";

        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
                  .thenReturn(sheetName);

            // Trigger the action
            homeView.getCreateSheetButton().doClick();

            // Verify that the controller's createNewServerSheet method was called with the correct name
            verify(mockController).createNewServerSheet(sheetName);
        }
    }

    // @Test
    // public void testOpenSelectedSheet() {
    //     // Setup the dropdown to return a selected sheet
    //     String selectedSheet = "TestSheet";
    //     JComboBox<String> comboBox = new JComboBox<>();
    //     comboBox.addItem(selectedSheet);
    //     homeView.getOpenSheetDropdown().setSelectedItem(selectedSheet);

    //     // Trigger the action
    //     homeView.getOpenSheetButton().doClick();

    //     // Verify that the controller's openServerSheet method was called with the correct sheet
    //     verify(mockController).openServerSheet(selectedSheet);
    // }

    // @Test
    // public void testDeleteSheet() {
    //     // Setup the dropdown to return a selected sheet
    //     String selectedSheet = "TestSheet";
    //     JComboBox<String> comboBox = new JComboBox<>();
    //     comboBox.addItem(selectedSheet);
    //     homeView.getOpenSheetDropdown().setSelectedItem(selectedSheet);

    //     // Mock the JOptionPane to always select "Delete Locally"
    //     try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
    //         mocked.when(() -> JOptionPane.showOptionDialog(any(), anyString(), anyString(), anyInt(), anyInt(), any(), any(), any()))
    //               .thenReturn(JOptionPane.YES_OPTION);

    //         // Trigger the action
    //         homeView.getDeleteSheetButton().doClick();

    //         // Verify that the controller's deleteSheetFromServer method was called with the correct sheet
    //         verify(mockController).deleteSheetFromServer(selectedSheet);
    //     }
    // }

    @Test
    public void testUpdateSavedSheets() {
        // Mock the controller to return some sheets
        when(mockController.getSavedSheetsLocally()).thenReturn(List.of("LocalSheet1", "LocalSheet2"));
        when(mockController.getAppUserSheets()).thenReturn(List.of("ServerSheet1", "ServerSheet2"));
        when(mockController.getPublishersFromServer()).thenReturn(List.of("Publisher1", "Publisher2"));

        // Call the method to update saved sheets
        homeView.updateSavedSheets();

        // Verify the dropdowns have been populated correctly
        assertEquals(2, homeView.getOpenSheetDropdown().getItemCount());
        assertEquals(2, homeView.getPublishersDropdown().getItemCount());

        assertEquals("ServerSheet1", homeView.getOpenSheetDropdown().getItemAt(0));
        assertEquals("ServerSheet2", homeView.getOpenSheetDropdown().getItemAt(1));

        assertEquals("Publisher1", homeView.getPublishersDropdown().getItemAt(0));
        assertEquals("Publisher2", homeView.getPublishersDropdown().getItemAt(1));
    }

    @Test
    public void testDisplayErrorBox() {
        String errorMessage = "Test Error";

        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            homeView.displayErrorBox(errorMessage);

            // Verify the error message is displayed
            mocked.verify(() -> JOptionPane.showMessageDialog(homeView, errorMessage));
        }
    }

    @Test
    public void testDisposeHomePage() {
        homeView.disposeHomePage();
        // Verify the home page is disposed
        assertEquals(false, homeView.isDisplayable());
    }

    @Test
    public void testMakeVisible() {
        homeView.makeVisible();
        // Verify the home page is visible
        assertEquals(true, homeView.isVisible());
    }
}
