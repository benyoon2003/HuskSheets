package org.example.view;

import org.example.controller.IUserController;
import org.example.controller.UserController;
import org.example.model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The LoginView class provides a GUI for user login and registration.
 * It implements the ILoginView interface to interact with the user controller.
 */
public class LoginView extends JFrame implements ILoginView {

    private JTextField usernameField; // Text field for entering the username
    private JPasswordField passwordField; // Password field for entering the password
    private JButton loginButton; // Button for login action
    private JButton registerButton; // Button for registration action

    private IUserController controller; // Reference to the user controller

    /**
     * Constructor for LoginView.
     * Initializes the login window and its components.
     */
    public LoginView() {
        setTitle("Login"); // Set the title of the window
        setSize(300, 180); // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        setLocationRelativeTo(null); // Center the window on the screen

        JPanel panel = new JPanel(); // Create a new JPanel
        placeComponents(panel); // Place components on the panel

        add(panel); // Add the panel to the frame
        this.setVisible(true); // Make the frame visible
    }

    /**
     * Disposes of the login page, closing the window.
     */
    public void disposeLoginPage() {
        this.dispose(); // Dispose of the frame
    }

    /**
     * Adds a controller to the login view.
     *
     * @param controller the IUserController instance to add.
     */
    public void addController(IUserController controller) {
        this.controller = controller; // Set the controller
    }

    /**
     * Displays an error message in a dialog box.
     *
     * @param message the error message to display.
     */
    @Override
    public void displayErrorBox(String message) {
        JOptionPane.showMessageDialog(this, message); // Show error message dialog
    }
    
    /**
     * Places the components on the login panel.
     *
     * @param panel the JPanel to place components on.
     */
    private void placeComponents(JPanel panel) {
        panel.setLayout(null); // Set the layout to null

        JLabel userLabel = new JLabel("Username:"); // Create a label for username
        userLabel.setBounds(10, 20, 80, 25); // Set the bounds for the label
        panel.add(userLabel); // Add the label to the panel

        usernameField = new JTextField(20); // Create the text field for username
        usernameField.setBounds(100, 20, 165, 25); // Set the bounds for the text field
        panel.add(usernameField); // Add the text field to the panel

        JLabel passwordLabel = new JLabel("Password:"); // Create a label for password
        passwordLabel.setBounds(10, 50, 80, 25); // Set the bounds for the label
        panel.add(passwordLabel); // Add the label to the panel

        passwordField = new JPasswordField(20); // Create the password field
        passwordField.setBounds(100, 50, 165, 25); // Set the bounds for the password field
        panel.add(passwordField); // Add the password field to the panel

        loginButton = new JButton("Login"); // Create the login button
        loginButton.setBounds(10, 80, 80, 25); // Set the bounds for the button
        panel.add(loginButton); // Add the button to the panel

        registerButton = new JButton("Register"); // Create the register button
        registerButton.setBounds(100, 80, 165, 25); // Set the bounds for the button
        panel.add(registerButton); // Add the button to the panel

        // Add action listener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText(); // Get the entered username
                String password = new String(passwordField.getPassword()); // Get the entered password
                try {
                    controller.loginUser(username, password); // Call controller method to login user
                } catch (Exception e1) {
                    displayErrorBox(e1.getMessage()); // Display error message if login fails
                }
            }
        });

        // Add action listener for register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText(); // Get the entered username
                String password = new String(passwordField.getPassword()); // Get the entered password
                try {
                    controller.registerUser(username, password); // Call controller method to register user
                } catch (Exception e1) {
                    displayErrorBox(e1.getMessage()); // Display error message if registration fails
                }
            }
        });
    }
}
