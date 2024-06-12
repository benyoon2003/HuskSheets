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

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    private IUserController controller;

    /**
     * Constructor for LoginView.
     * Initializes the login window and its components.
     */
    public LoginView() {
        setTitle("Login");
        setSize(300, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        placeComponents(panel);

        add(panel);
        this.setVisible(true);
    }

    /**
     * Disposes of the login page, closing the window.
     */
    public void disposeLoginPage() {
        this.dispose();
    }

    /**
     * Adds a controller to the login view.
     *
     * @param controller the IUserController instance to add.
     */
    public void addController(IUserController controller) {
        this.controller = controller;
    }

    /**
     * Displays an error message in a dialog box.
     *
     * @param message the error message to display.
     */
    @Override
    public void displayErrorBox(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    /**
     * Places the components on the login panel.
     *
     * @param panel the JPanel to place components on.
     */
    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setBounds(100, 80, 165, 25);
        panel.add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                try {
                    controller.loginUser(username, password);
                } catch (Exception e1) {
                    displayErrorBox(e1.getMessage());
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                try {
                    controller.registerUser(username, password);
                } catch (Exception e1) {
                    displayErrorBox(e1.getMessage());
                }
            }
        });
    }
}
