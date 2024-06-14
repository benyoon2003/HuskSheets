package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;

import org.example.controller.IUserController;
import org.example.controller.UserController;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the methods within the LoginView class.
 */
public class LoginViewTest {
    private MockLoginView loginView = new MockLoginView();
    
    /**
     * Tests the addController method of the LoginView class.
     */
    @Test
    public void testAddController() {
        IUserController controller = new UserController(this.loginView);
        this.loginView.addController(controller);

        assertEquals(controller, this.loginView.getController());
        assertTrue(this.loginView.toString().contains("Controller added\n"));
    }

    /**
     * Tests the displayErrorBox method of the LoginView class.
     */
    @Test
    public void testDisplayErrorBox() {
        this.loginView.displayErrorBox("Could not log in");
        assertTrue(this.loginView.toString().contains("Error: Could not log in\n"));
    }

    /**
     * Tests the disposeLoginPage method of the LoginView class.
     */
    @Test
    public void testDisposeLoginPage() {
        this.loginView.disposeLoginPage();
        assertTrue(this.loginView.toString().contains("Login page disposed\n"));
    }

    /**
     * Cleans up after each test by disposing of the login page.
     */
    @AfterEach
    public void after() {
        this.loginView.disposeLoginPage();
    }

    /**
     * A mock class extending LoginView to capture output for testing.
     */
    private class MockLoginView extends LoginView {
        private StringWriter out;
        private IUserController controller;

        /**
         * Initializes the mock login view.
         */
        MockLoginView() {
            this.out = new StringWriter();
        }

        /**
         * Adds a controller to the login view and logs the action.
         * @param controller the controller to add
         */
        @Override
        public void addController(IUserController controller) {
            super.addController(controller);
            this.controller = controller;
            this.out.append("Controller added\n");
        }

       /**
         * Gets the controller associated with the login view.
         * @return the controller
         */
        public IUserController getController() {
            return this.controller;
        }

        /**
         * Displays an error message and logs the action.
         * @param message the error message to display
         */
        @Override
        public void displayErrorBox(String message) {
            super.displayErrorBox(message);
            this.out.append("Error: " + message + "\n");
        }

        /**
         * Disposes of the login page and logs the action.
         */
        @Override
        public void disposeLoginPage() {
            super.disposeLoginPage();
            this.out.append("Login page disposed\n");
        }
        
        /**
         * Returns the log of actions performed on the mock login view.
         * @return the log of actions
         */
        @Override
        public String toString() {
            return this.out.toString();
        }
    }
}
