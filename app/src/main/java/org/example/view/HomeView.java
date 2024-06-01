package org.example.view;

import org.example.controller.IUserController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeView extends JFrame implements IHomeView {

    private JButton createSheet;
    private JComboBox<String> openSheetDropdown;
    private JButton openSheetButton;
    private JButton deleteSheetButton;
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


        //Button for creating a new sheet
        createSheet = new JButton("Create Spreadsheet");
        createSheet.setBounds(50, 70, 200, 25);
        panel.add(createSheet);

        //dropdown menu for locally saved sheets
        openSheetDropdown = new JComboBox<>();
        openSheetDropdown.setBounds(50, 110, 200, 25);
        panel.add(openSheetDropdown);

        //Button to open selected sheet
        openSheetButton = new JButton("Open Spreadsheet");
        openSheetButton.setBounds(50, 150, 200, 25);
        panel.add(openSheetButton);


        //Button to delete selected sheet
        deleteSheetButton = new JButton("Delete Spreadsheet");
        deleteSheetButton.setBounds(50, 190, 200, 25);
        panel.add(deleteSheetButton);

        //Create new sheet with name
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

        //open selected sheet
        openSheetButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openSheetDropdown.getSelectedItem();
                if (selectedSheet != null) {
//                    controller.openSheet("sheets/" + selectedSheet);
                    controller.openServerSheet(selectedSheet);
                } else {
                    JOptionPane.showMessageDialog(panel, "No sheet selected to open");
                }
            }
        });

        deleteSheetButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSheet = (String) openSheetDropdown.getSelectedItem();
                // if (selectedSheet != null) {
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
                        // controller.deleteSheet(selectedSheet);
                    } else if (option == JOptionPane.NO_OPTION) {
                       System.out.println(selectedSheet);
                       controller.deleteSheetFromServer(selectedSheet);
                       makeVisible();
                    }
                // } else {
                //     JOptionPane.showMessageDialog(panel, "No sheet selected to delete");
                // }
            }
        });

    }

//    private String getSheetToDeleteFromServer() {
//        try {
//            HttpClient client = HttpClient.newHttpClient();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI("http://localhost:8080/api/getSheets"))
//                    .header("Content-Type", "application/json")
//                    .GET()
//                    .build();
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                String responseBody = response.body();
//                JSONArray sheetsArray = new JSONArray(responseBody);
//                List<String> sheetNames = new ArrayList<>();
//                for (int i = 0; i < sheetsArray.length(); i++) {
//                    JSONObject sheetObject = sheetsArray.getJSONObject(i);
//                    sheetNames.add(sheetObject.getString("name"));
//                }
//
//                String[] sheetArray = sheetNames.toArray(new String[0]);
//                return (String) JOptionPane.showInputDialog(
//                        null,
//                        "Select a sheet to delete from the server:",
//                        "Delete Sheet from Server",
//                        JOptionPane.QUESTION_MESSAGE,
//                        null,
//                        sheetArray,
//                        sheetArray[0]);
//
//            } else {
//                JOptionPane.showMessageDialog(null, "Failed to retrieve sheets from server.");
//                return null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, "Error occurred: " + e.getMessage());
//            return null;
//        }
//    }

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
            this.controller.openSheet(path);
        } catch (Exception e) {
            System.out.println("Could not load spreadsheet from server: " + e.getMessage());
        }
    }

    @Override
    public void updateSavedSheets() {
        if (controller != null) {
            List<String> savedSheets = controller.getSavedSheets();
            List<String> serverSheets = controller.getServerSheets();
            System.out.println("Updating dropdown with saved sheets: " + savedSheets);
            openSheetDropdown.removeAllItems();
            for (String sheet : serverSheets) {
                openSheetDropdown.addItem(sheet);
            }
        }
    }

    @Override
    public void addController(IUserController controller) {
        this.controller = controller;
    }

    @Override
    public void makeVisible() {
        this.setVisible(true);
        updateSavedSheets();
        this.controller.getServerSheets();
    }

    @Override
    public void disposeHomePage() {
        this.dispose();
    }
}
