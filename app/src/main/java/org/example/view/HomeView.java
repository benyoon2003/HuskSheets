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

        createSheet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.createNewSheet();
            }
        });

        openSheetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openSheetDropdown.getSelectedItem();
                if (selectedSheet != null) {
                    System.out.println("Opening sheet: " + selectedSheet); // Debug statement
                    controller.openSheet("./sheets/" + selectedSheet);
                }
            }
        });
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
}
