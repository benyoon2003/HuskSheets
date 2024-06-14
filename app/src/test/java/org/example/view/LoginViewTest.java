package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals; // Import assertion for equality checks
import static org.junit.jupiter.api.Assertions.assertTrue; // Import assertion for boolean checks

import java.io.StringWriter; // Import for StringWriter to capture output

import org.example.controller.IUserController; // Import interface for user controller
import org.example.controller.UserController; // Import implementation of user controller

import org.junit.jupiter.api.AfterEach; // Import for cleanup method after each test
import org.junit.jupiter.api.Test; // Import for test annotation

/**
 * Tests the methods within the LoginView class.
 */
public class LoginViewTest {
    private MockLoginView loginView = new MockLoginView(); // Create instance of MockLoginView for testing

    /**
     * Tests the addController method of the LoginView class.
     */
    @Test
    public void testAddController() {
        IUserController controller = new UserController(this.loginView); // Create a new UserController with the mock login view
        this.loginView.addController(controller); // Add the controller to the login view

        assertEquals(controller, this.loginView.getController()); // Assert that the controller was added correctly
        assertTrue(this.loginView.toString().contains("Controller added\n")); // Check if the log contains the correct message
    }

    /**
     * Tests the displayErrorBox method of the LoginView class.
     */
    @Test
    public void testDisplayErrorBox() {
        this.loginView.displayErrorBox("Could not log in"); // Display an error message
        assertTrue(this.loginView.toString().contains("Error: Could not log in\n")); // Check if the log contains the error message
    }

    /**
     * Tests the disposeLoginPage method of the LoginView class.
     */
    @Test
    public void testDisposeLoginPage() {
        this.loginView.disposeLoginPage(); // Dispose of the login page
        assertTrue(this.loginView.toString().contains("Login page disposed\n")); // Check if the log contains the dispose message
    }

    /**
     * Cleans up after each test by disposing of the login page.
     */
    @AfterEach
    public void after() {
        this.loginView.disposeLoginPage(); // Ensure the login page is disposed after each test
    }

    /**
     * A mock class extending LoginView to capture output for testing.
     */
    private class MockLoginView extends LoginView {
        private StringWriter out; // StringWriter to capture output
        private IUserController controller; // Variable to store the controller

        /**
         * Initializes the mock login view.
         */
        MockLoginView() {
            this.out = new StringWriter(); // Initialize the StringWriter
        }

        /**
         * Adds a controller to the login view and logs the action.
         * @param controller the controller to add
         */
        @Override
        public void addController(IUserController controller) {
            super.addController(controller); // Call the superclass method
            this.controller = controller; // Store the controller
            this.out.append("Controller added\n"); // Log the action
        }

        /**
         * Gets the controller associated with the login view.
         * @return the controller
         */
        public IUserController getController() {
            return this.controller; // Return the stored controller
        }

        /**
         * Displays an error message and logs the action.
         * @param message the error message to display
         */
        @Override
        public void displayErrorBox(String message) {
            super.displayErrorBox(message); // Call the superclass method
            this.out.append("Error: " + message + "\n"); // Log the action
        }

        /**
         * Disposes of the login page and logs the action.
         */
        @Override
        public void disposeLoginPage() {
            super.disposeLoginPage(); // Call the superclass method
            this.out.append("Login page disposed\n"); // Log the action
        }

        /**
         * Returns the log of actions performed on the mock login view.
         * @return the log of actions
         */
        @Override
        public String toString() {
            return this.out.toString(); // Return the log as a string
        }
    }
}
