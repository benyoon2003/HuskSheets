package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;

import org.example.controller.IUserController;
import org.example.controller.UserController;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class LoginViewTest {
    private MockLoginView loginView = new MockLoginView();

    @Test
    public void testAddController() {
        IUserController controller = new UserController(this.loginView);
        this.loginView.addController(controller);

        assertEquals(controller, this.loginView.getController());
        assertTrue(this.loginView.toString().contains("Controller added\n"));
    }
    
    @Test
    public void testDisplayErrorBox() {
        this.loginView.displayErrorBox("Could not log in");
        assertTrue(this.loginView.toString().contains("Error: Could not log in\n"));
    }
    
    @Test
    public void testDisposeLoginPage() {
        this.loginView.disposeLoginPage();
        assertTrue(this.loginView.toString().contains("Login page disposed\n"));
    }

    @AfterEach
    public void after() {
        this.loginView.disposeLoginPage();
    }

    private class MockLoginView extends LoginView {
        private StringWriter out;
        private IUserController controller;

        MockLoginView() {
            this.out = new StringWriter();
        }

        @Override
        public void addController(IUserController controller) {
            super.addController(controller);
            this.controller = controller;
            this.out.append("Controller added\n");
        }

        public IUserController getController() {
            return this.controller;
        }

        @Override
        public void displayErrorBox(String message) {
            super.displayErrorBox(message);
            this.out.append("Error: " + message + "\n");
        }

        @Override
        public void disposeLoginPage() {
            super.disposeLoginPage();
            this.out.append("Login page disposed\n");
        }

        @Override
        public String toString() {
            return this.out.toString();
        }
    }
}
