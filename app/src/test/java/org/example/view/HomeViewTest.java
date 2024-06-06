package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.StringWriter;

import org.example.controller.IUserController;
import org.example.controller.MockUserController;
import org.example.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HomeViewTest {
    private MockHomeView homeView;
    private IUserController controller;

    @BeforeEach
    public void init() {
        this.homeView = new MockHomeView();
        this.controller = mock(IUserController.class);
        this.homeView.addController(this.controller);
    }

    @Test
    public void testAddController() {
        assertEquals(this.controller, this.homeView.controller);
    }

    @Test
    public void testMakeVisible() {
        this.homeView.makeVisible();
        assertTrue(this.homeView.toString().contains("Home page is now visible\n"));
    }

    @Test
    public void testDisposeHomePage() {
        this.homeView.disposeHomePage();
        assertTrue(this.homeView.toString().contains("Home page disposed\n"));
    }

    @Test
    public void testUpdateSavedSheets() {
        this.homeView.updateSavedSheets();
        assertTrue(this.homeView.toString().contains("List of saved sheets updated\n"));
    }

    @Test
    public void testOpenSheet() {
        this.homeView.openSheet("..\\sheets\\test.xml");
        assertTrue(this.homeView.toString().contains("Sheet '..\\sheets\\test.xml' opened\n"));
    }

    @Test
    public void testOpenSheetFromServer() {
        this.homeView.openSheetFromServer("..\\sheets\\test.xml");
        assertTrue(this.homeView.toString().contains("Sheet '..\\sheets\\test.xml' opened from server\n"));
    }

    @Test
    public void testDisplayErrorBox() {
        this.homeView.displayErrorBox("This is a test error");
        assertTrue(this.homeView.toString().contains("Error: This is a test error\n"));
    }

    // A mock version of the HomeView class used for testing
    private class MockHomeView extends HomeView {
        private StringWriter out;
        private IUserController controller;

        MockHomeView() {
            this.out = new StringWriter();
        }

        @Override
        public void addController(IUserController controller) {
            this.controller = controller;
        }

        @Override
        public void makeVisible() {
            this.out.append("Home page is now visible\n");
        }

        @Override
        public void disposeHomePage() {
            this.out.append("Home page disposed\n");
        }

        @Override
        public void updateSavedSheets() {
            this.out.append("List of saved sheets updated\n");
        }

        @Override
        public void displayErrorBox(String message) {
            this.out.append("Error: ").append(message).append("\n");
        }

        @Override
        public void openSheet(String path) {
            this.out.append("Sheet '").append(path).append("' opened\n");
        }
        
        @Override
        public void openSheetFromServer(String path) {
            this.out.append("Sheet '").append(path).append("' opened from server\n");
        }

        @Override
        public String toString() {
            return this.out.toString();
        }
    }
}
