package org.example.view;

import org.example.controller.IUserController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * HomeView is the main GUI window that allows users to create, open, and delete
 * spreadsheets.
 * It interacts with the IUserController to handle user actions and update the
 * view accordingly.
 */
public class HomeView extends JFrame implements IHomeView {

    private JButton createSheet; // Button for creating a new sheet
    private JComboBox<String> openSheetDropdown; // Dropdown menu for opening sheets
    private JButton openLocalButton; // JButton for opening locally saved sheet
    private JComboBox<String> publishers; // Dropdown menu for selecting publishers
    private JComboBox<String> openSubscriberDropdown; // Dropdown menu for opening subscriber sheets
    private JButton openSheetButton; // Button for opening a selected sheet
    private JButton openSubscribeButton; // Button for subscribing and opening a selected sheet
    private JButton deleteSheetButton; // Button for deleting a selected sheet
    private IUserController controller; // Reference to the controller

    /**
     * Constructs a HomeView instance, setting up the main GUI window.
     */
    public HomeView() {
        setTitle("Main GUI"); // Set the title of the window
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation

        JPanel panel = new JPanel(); // Create a new panel
        placeComponents(panel); // Place components on the panel
        add(panel); // Add the panel to the frame
    }

    /**
     * Places and initializes the components within the specified panel.
     *
     * @param panel the JPanel to place the components on.
     */
    private void placeComponents(JPanel panel) {
        panel.setLayout(null); // Set the layout to null

        JLabel publisherLabel = new JLabel("Publisher Spreadsheets:"); // Create a label for spreadsheets
        publisherLabel.setBounds(50, 50, 200, 25); // Set the bounds for the label
        panel.add(publisherLabel); // Add the label to the panel

        JLabel subscriberLabel = new JLabel("Subscriber Spreadsheets:"); // Create a label for spreadsheets
        subscriberLabel.setBounds(50, 300, 200, 25); // Set the bounds for the label
        panel.add(subscriberLabel); // Add the label to the panel

        JLabel locallabel = new JLabel("Local Spreadsheets:"); // Create a label for spreadsheets
        locallabel.setBounds(50, 550, 200, 25); // Set the bounds for the label
        panel.add(locallabel); // Add the label to the panel

        openLocalButton = new JButton("Open locally"); // Create the button
        openLocalButton.setBounds(50, 590,200, 25); // Set bounds for the button
        panel.add(openLocalButton); // Add button to the panel

        // Button for creating a new sheet
        createSheet = new JButton("Create Spreadsheet"); // Create the button
        createSheet.setBounds(50, 70, 200, 25); // Set the bounds for the button
        panel.add(createSheet); // Add the button to the panel

        openSheetDropdown = new JComboBox<>(); // Create the dropdown menu
        openSheetDropdown.setBounds(50, 110, 200, 25); // Set the bounds for the dropdown menu
        panel.add(openSheetDropdown); // Add the dropdown menu to the panel

        publishers = new JComboBox<>(); // Create the dropdown menu for publishers
        publishers.setBounds(50, 320, 200, 25); // Set the bounds for the dropdown menu
        panel.add(publishers); // Add the dropdown menu to the panel

        openSubscriberDropdown = new JComboBox<>(); // Create the dropdown menu for subscriber sheets
        openSubscriberDropdown.setBounds(50, 360, 200, 25); // Set the bounds for the dropdown menu
        panel.add(openSubscriberDropdown); // Add the dropdown menu to the panel

        // Button to open selected sheet
        openSheetButton = new JButton("Open Spreadsheet"); // Create the button
        openSheetButton.setBounds(50, 150, 200, 25); // Set the bounds for the button
        panel.add(openSheetButton); // Add the button to the panel

        openSubscribeButton = new JButton("Subscribe and open"); // Create the button
        openSubscribeButton.setBounds(50, 400, 200, 25); // Set the bounds for the button
        panel.add(openSubscribeButton); // Add the button to the panel

        // Button to delete selected sheet
        deleteSheetButton = new JButton("Delete Spreadsheet"); // Create the button
        deleteSheetButton.setBounds(50, 190, 200, 25); // Set the bounds for the button
        panel.add(deleteSheetButton); // Add the button to the panel

        openLocalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null); // Change to showOpenDialog to select files
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    controller.openSheetLocally(selectedFile.getAbsolutePath());
                }
            }
        });

        // Create new sheet with name
        createSheet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sheetName = JOptionPane.showInputDialog(panel, "Enter sheet name:", "Create New Sheet",
                        JOptionPane.PLAIN_MESSAGE); // Show input dialog to enter sheet name
                if (sheetName != null && !sheetName.trim().isEmpty()) {
                    controller.createNewServerSheet(sheetName); // Call controller method to create a new sheet
                } else {
                    JOptionPane.showMessageDialog(panel, "Sheet name cannot be empty", "Error",
                            JOptionPane.ERROR_MESSAGE); // Show error message if sheet name is empty
                }
            }
        });

        // Add action listener for opening selected sheet
        openSheetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openSheetDropdown.getSelectedItem(); // Get selected sheet
                if (selectedSheet != null) {
                    controller.openServerSheet(selectedSheet); // Call controller method to open the sheet
                } else {
                    JOptionPane.showMessageDialog(panel, "No sheet selected to open"); // Show error message if no sheet is selected
                }
            }
        });

        // Add action listener for changing publisher
        publishers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (publishers.getSelectedItem() != null) {
                    updateSubscribeSheets((String) publishers.getSelectedItem()); // Update the subscriber sheets based on selected publisher
                }
            }
        });

        // Add action listener for subscribing and opening selected sheet
        openSubscribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openSubscriberDropdown.getSelectedItem(); // Get selected subscriber sheet
                if (selectedSheet != null && publishers.getSelectedItem() != null) {
                    controller.openSubscriberSheet(selectedSheet, (String) publishers.getSelectedItem()); // Call controller method to open subscriber sheet
                } else {
                    JOptionPane.showMessageDialog(panel, "No sheet selected to open"); // Show error message if no sheet is selected
                }
            }
        });

        // Add action listener for deleting selected sheet
        deleteSheetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openSheetDropdown.getSelectedItem(); // Get selected sheet
                if (selectedSheet != null) {
                    int option = JOptionPane.showOptionDialog(
                            null,
                            "Choose where to delete the sheet from:",
                            "Delete Option",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new Object[] { "Delete Locally", "Delete from Server" },
                            "Delete Locally"); // Show option dialog to choose delete location

                    if (option == JOptionPane.YES_OPTION) {
                        controller.deleteSheetFromServer(selectedSheet); // Call controller method to delete sheet from server
                    } else {
                        controller.deleteSheetFromServer(selectedSheet); // Call controller method to delete sheet from server (duplicate code, should probably call different method)
                        makeVisible(); // Refresh the view
                        makeVisible();
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "No sheet selected to delete"); // Show error message if no sheet is selected
                }
            }
        });

    }

    // Method to update subscriber sheets based on selected publisher
    public void updateSubscribeSheets(String selectedPublisher) {
        List<String> subscribedSheets = controller.accessSheetsFromUser(selectedPublisher); // Get list of subscribed sheets from controller
        openSubscriberDropdown.removeAllItems(); // Remove all items from subscriber dropdown
        for (String sheet : subscribedSheets) {
            openSubscriberDropdown.addItem(sheet); // Add each subscribed sheet to the dropdown
        }
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
     * Updates the list of saved sheets in the dropdown menu.
     */
    @Override
    public void updateSavedSheets() {
        if (controller != null) {
            List<String> savedSheets = controller.getSavedSheetsLocally(); // Get list of saved sheets locally
            List<String> serverSheets = controller.getAppUserSheets(); // Get list of server sheets
            List<String> listOfPublishers = controller.getPublishersFromServer(); // Get list of publishers from server
            System.out.println("Updating dropdown with saved sheets: " + savedSheets); // Print saved sheets to console (for debugging)
            openSheetDropdown.removeAllItems(); // Remove all items from open sheet dropdown
            publishers.removeAllItems(); // Remove all items from publishers dropdown
            for (String sheet : serverSheets) {
                openSheetDropdown.addItem(sheet); // Add each server sheet to the dropdown
            }

            for (String username : listOfPublishers) {
                publishers.addItem(username); // Add each publisher to the dropdown
            }
        }
    }

    /**
     * Adds the controller to this view.
     *
     * @param controller the IUserController instance to add.
     */
    @Override
    public void addController(IUserController controller) {
        this.controller = controller; // Set the controller
    }

    /**
     * Makes the home view visible and updates the list of saved sheets.
     */
    @Override
    public void makeVisible() {
        this.setVisible(true); // Make the frame visible
        updateSavedSheets(); // Update the list of saved sheets
    }

    /**
     * Disposes of the home page.
     */
    @Override
    public void disposeHomePage() {
        this.dispose(); // Dispose of the frame
    }

    /**
 * Getter method for the create sheet button.
 *
 * @return the create sheet button.
 */
public JButton getCreateSheetButton() {
    return createSheet; // Return the create sheet button
}

/**
 * Getter method for the open sheet dropdown.
 *
 * @return the open sheet dropdown.
 */
public JComboBox<String> getOpenSheetDropdown() {
    return openSheetDropdown; // Return the open sheet dropdown
}

/**
 * Getter method for the publishers dropdown.
 *
 * @return the publishers dropdown.
 */
public JComboBox<String> getPublishersDropdown() {
    return publishers; // Return the publishers dropdown
}

/**
 * Getter method for the open sheet button.
 *
 * @return the open sheet button.
 */
public JButton getOpenSheetButton() {
    return openSheetButton; // Return the open sheet button
}

/**
 * Getter method for the delete sheet button.
 *
 * @return the delete sheet button.
 */
public JButton getDeleteSheetButton() {
    return deleteSheetButton; // Return the delete sheet button
}

/**
 * Getter method for the controller.
 *
 * @return the controller.
 */
public IUserController getController() {
    return controller; // Return the controller
}

}
