// package org.example.view;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.io.StringWriter;

// import org.example.controller.IUserController;
// import org.example.controller.MockUserController;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// public class LoginViewTest {
//     private ILoginView loginView;

//     @BeforeEach
//     public void init() {
//         this.loginView = new MockLoginView();
//     }

//     @Test
//     public void testAddController() {
//         IUserController controller = new MockUserController();
//         this.loginView.addController(controller);

//         assertEquals(this.loginView.toString(), "Controller added\n");
//     }
    
//     @Test
//     public void testDisplayErrorBox() {
//         this.loginView.displayErrorBox("Could not log in");
//         assertEquals(this.loginView.toString(), "Error: Could not log in\n");
//     }
    
//     @Test
//     public void testDisposeLoginPage() {
//         this.loginView.disposeLoginPage();
//         assertEquals(this.loginView.toString(), "Login page disposed\n");
//     }

//     private class MockLoginView extends LoginView {
//         private StringWriter out;

//         MockLoginView() {
//             this.out = new StringWriter();
//         }

//         @Override
//         public void addController(IUserController controller) {
//             this.out.append("Controller added\n");
//         }

//         @Override
//         public void displayErrorBox(Object message) {
//             this.out.append("Error: " + message + "\n");
//         }

//         @Override
//         public void disposeLoginPage() {
//             this.out.append("Login page disposed\n");
//         }

//         public String toString() {
//             return this.out.toString();
//         }
//     }
// }
