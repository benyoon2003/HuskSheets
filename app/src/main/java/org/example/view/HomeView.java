package org.example.view;

import org.example.controller.IUserController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class HomeView extends JFrame implements IHomeView {

    private JButton createSheet;
    private JComboBox<String> openSheetDropdown;
    private JButton openSheetButton;
    private JButton deleteSheetButton; // Add this line
    private JButton openSheetFromServerButton;
    private IUserController controller;

    public HomeView() {
        setTitle("Main GUI");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        placeComponents(panel);
        add(panel);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel spreadheetsLabel = new JLabel("Spreadsheets:");
        spreadheetsLabel.setBounds(50, 50, 100, 25);
        panel.add(spreadheetsLabel);

        createSheet = new JButton("Create Spreadsheet");
        createSheet.setBounds(50, 70, 200, 25);
        panel.add(createSheet);

        openSheetDropdown = new JComboBox<>();
        openSheetDropdown.setBounds(50, 110, 200, 25);
        panel.add(openSheetDropdown);

        openSheetButton = new JButton("Open Spreadsheet");
        openSheetButton.setBounds(50, 150, 200, 25);
        panel.add(openSheetButton);

        deleteSheetButton = new JButton("Delete Spreadsheet"); // Add this line
        deleteSheetButton.setBounds(50, 190, 200, 25); // Add this line
        panel.add(deleteSheetButton); // Add this line

        openSheetFromServerButton = new JButton("Open Spreadsheet from Server");
        openSheetFromServerButton.setBounds(50, 210, 200, 25);
        panel.add(openSheetFromServerButton);

        createSheet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.createNewSheet();
            }
        });

        openSheetButton.addActionListener(new ActionListener() { // Update this block
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openSheetDropdown.getSelectedItem();
                if (selectedSheet != null) {
                    controller.openSheet("sheets/" + selectedSheet);
                } else {
                    JOptionPane.showMessageDialog(panel, "No sheet selected to open");
                }
            }
        });

        deleteSheetButton.addActionListener(new ActionListener() { // Add this block
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openSheetDropdown.getSelectedItem();
                if (selectedSheet != null) {
                    controller.deleteSheet(selectedSheet);
                } else {
                    JOptionPane.showMessageDialog(panel, "No sheet selected to delete");
                }
            }
        });
    }

    @Override
    public void openSheet(String path) {
        try {
            this.controller.openSheet(path);
        } catch (Exception e) {
            System.out.println("Could not load spreadsheet: " + e.getMessage());
        }
    }
    
    @Override
    public void openSheetFromServer(String path) {
        try {
            this.controller.openSheetFromServer(path);
        } catch (Exception e) {
            System.out.println("Could not load spreadsheet from server: " + e.getMessage());
        }
    }

    @Override
    public void updateSavedSheets() {
        if (controller != null) {
            List<String> savedSheets = controller.getSavedSheets();
            System.out.println("Updating dropdown with saved sheets: " + savedSheets); // Debug statement
            openSheetDropdown.removeAllItems();
            for (String sheet : savedSheets) {
                openSheetDropdown.addItem(sheet);
            }
        }
    }

    @Override
    public void addController(IUserController controller) {
        this.controller = controller;
        updateSavedSheets(); // Call updateSavedSheets() after setting the controller
    }

    @Override
    public void makeVisible() {
        this.setVisible(true);
        updateSavedSheets(); // Ensure the dropdown is updated whenever the view is made visible
    }

    @Override
    public void disposeHomePage() {
        this.dispose();
    }

    // private class OpenSheetListener implements ActionListener {
    //     private IHomeView view;

    //     OpenSheetListener(IHomeView view) {
    //         this.view = view;
    //     }

    //     @Override
    //     public void actionPerformed(ActionEvent e) {
    //         JFileChooser fileChooser = new JFileChooser();
    //         int returnValue = fileChooser.showOpenDialog(null);
    //         if (returnValue == JFileChooser.APPROVE_OPTION) {
    //             File selectedFile = fileChooser.getSelectedFile();
    //             this.view.openSheet(selectedFile.getAbsolutePath());
    //         }
    //     }
    // }
}
