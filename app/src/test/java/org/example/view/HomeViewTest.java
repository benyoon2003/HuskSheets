package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringWriter;

import org.example.controller.IUserController;
import org.example.controller.MockUserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HomeViewTest {
    private IHomeView homeView;

    @BeforeEach
    public void init() {
        this.homeView = new MockHomeView();
    }
    
    @Test
    public void testAddController() {
        IUserController controller = new MockUserController();
        this.homeView.addController(controller);

        assertEquals(this.homeView.toString(), "Controller added\n");
    }
    
    @Test
    public void testMakeVisible() {
        this.homeView.makeVisible();
        assertEquals(this.homeView.toString(), "Home page is now visible\n");
    }
    
    @Test
    public void testDisposeHomePage() {
        this.homeView.disposeHomePage();
        assertEquals(this.homeView.toString(), "Home page disposed\n");
    }

    private class MockHomeView extends HomeView {
        private StringWriter out;

        MockHomeView() {
            this.out = new StringWriter();
        }

        @Override
        public void addController(IUserController controller) {
            this.out.append("Controller added\n");
        }

        @Override
        public void makeVisible() {
            this.out.append("Home page is now visible\n");
        }

        @Override
        public void disposeHomePage() {
            this.out.append("Home page disposed\n");
        }

        public String toString() {
            return this.out.toString();
        }
    }
}
