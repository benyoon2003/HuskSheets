package org.example.view;

import org.example.controller.IUserController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * HomeView is the main GUI window that allows users to create, open, and delete spreadsheets.
 * It interacts with the IUserController to handle user actions and update the view accordingly.
 */
public class HomeView extends JFrame implements IHomeView {

    private JButton createSheet;
    private JComboBox<String> openPublisherSheetDropdown;
    private JComboBox<String> openSubscriberSheetDropdown;
    private JButton openSheetButton;
    private JButton deleteSheetButton;
    private IUserController controller;

    /**
     * Constructs a HomeView instance, setting up the main GUI window.
     */
    public HomeView() {
        setTitle("Main GUI");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        placeComponents(panel);
        add(panel);
    }

    /**
     * Places and initializes the components within the specified panel.
     *
     * @param panel the JPanel to place the components on.
     */
    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Label for Publisher Sheets
        JLabel publisherSheetsLabel = new JLabel("Publisher Sheets:");
        publisherSheetsLabel.setBounds(50, 50, 150, 25);
        panel.add(publisherSheetsLabel);

        // Dropdown menu for publisher sheets
        openPublisherSheetDropdown = new JComboBox<>();
        openPublisherSheetDropdown.setBounds(50, 80, 200, 25);
        panel.add(openPublisherSheetDropdown);

        // Label for Subscriber Sheets
        JLabel subscriberSheetsLabel = new JLabel("Subscriber Sheets:");
        subscriberSheetsLabel.setBounds(300, 50, 150, 25);
        panel.add(subscriberSheetsLabel);

        // Dropdown menu for subscriber sheets
        openSubscriberSheetDropdown = new JComboBox<>();
        openSubscriberSheetDropdown.setBounds(300, 80, 200, 25);
        panel.add(openSubscriberSheetDropdown);

        // Button for creating a new sheet
        createSheet = new JButton("Create Spreadsheet");
        createSheet.setBounds(50, 120, 200, 25);
        panel.add(createSheet);

        // Button to open selected sheet
        openSheetButton = new JButton("Open Spreadsheet");
        openSheetButton.setBounds(50, 160, 200, 25);
        panel.add(openSheetButton);

        // Button to delete selected sheet
        deleteSheetButton = new JButton("Delete Spreadsheet");
        deleteSheetButton.setBounds(50, 200, 200, 25);
        panel.add(deleteSheetButton);

        // Create new sheet with name
        createSheet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sheetName = JOptionPane.showInputDialog(panel, "Enter sheet name:", "Create New Sheet", JOptionPane.PLAIN_MESSAGE);
                if (sheetName != null && !sheetName.trim().isEmpty()) {
                    controller.createNewSheet(sheetName);
                } else {
                    JOptionPane.showMessageDialog(panel, "Sheet name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Open selected sheet
        openSheetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openPublisherSheetDropdown.getSelectedItem();
                if (selectedSheet != null) {
                    controller.openServerSheet(selectedSheet);
                } else {
                    JOptionPane.showMessageDialog(panel, "No sheet selected to open");
                }
            }
        });

        deleteSheetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openPublisherSheetDropdown.getSelectedItem();
                if (selectedSheet != null) {
                    int option = JOptionPane.showOptionDialog(
                            null,
                            "Choose where to delete the sheet from:",
                            "Delete Option",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new Object[]{"Delete Locally", "Delete from Server"},
                            "Delete Locally");

                    if (option == JOptionPane.YES_OPTION) {
                        controller.deleteSheet(selectedSheet);
                    } else if (option == JOptionPane.NO_OPTION) {
                        controller.deleteSheetFromServer(selectedSheet);
                        makeVisible();
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "No sheet selected to delete");
                }
            }
        });
    }

    /**
     * Opens a sheet from the specified path.
     *
     * @param path the path to the sheet to open.
     */
    @Override
    public void openSheet(String path) {
        try {
            this.controller.openSheet(path);
        } catch (Exception e) {
            System.out.println("Could not load spreadsheet: " + e.getMessage());
        }
    }

    /**
     * Opens a sheet from the server.
     *
     * @param path the path to the sheet on the server to open.
     */
    @Override
    public void openSheetFromServer(String path) {
        try {
            this.controller.openSheet(path);
        } catch (Exception e) {
            System.out.println("Could not load spreadsheet from server: " + e.getMessage());
        }
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
     * Updates the list of saved sheets in the dropdown menu.
     */
    @Override
    public void updateSavedSheets() {
        if (controller != null) {
            List<String> savedSheets = controller.getSavedSheets();
            List<String> serverSheets = controller.getServerSheets();
            System.out.println("Updating dropdown with saved sheets: " + savedSheets);

            openPublisherSheetDropdown.removeAllItems();
            for (String sheet : savedSheets) {
                openPublisherSheetDropdown.addItem(sheet);
            }

            openSubscriberSheetDropdown.removeAllItems();
            for (String sheet : serverSheets) {
                openSubscriberSheetDropdown.addItem(sheet);
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
        this.controller = controller;
    }

    /**
     * Makes the home view visible and updates the list of saved sheets.
     */
    @Override
    public void makeVisible() {
        this.setVisible(true);
        updateSavedSheets();
        this.controller.getServerSheets();
    }

    /**
     * Disposes of the home page.
     */
    @Override
    public void disposeHomePage() {
        this.dispose();
    }
}
