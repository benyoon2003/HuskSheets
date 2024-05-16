package org.example.view;

import org.example.controller.IUserController;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI extends JFrame implements IMainGUI {

    private JTextField[][] cells;
    private IUserController controller;

    public MainGUI() {
        setTitle("Main GUI");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create toolbar
        JToolBar toolbar = new JToolBar();
        JButton cutButton = new JButton("Cut");
        JButton copyButton = new JButton("Copy");
        JButton pasteButton = new JButton("Paste");
        JButton saveButton = new JButton("Save");

        toolbar.add(cutButton);
        toolbar.add(copyButton);
        toolbar.add(pasteButton);
        toolbar.add(saveButton);

        // Create dropdown menu for statistical calculations
        JComboBox<String> statsDropdown = new JComboBox<>(new String[]{"Mean", "Median", "Mode"});
        toolbar.add(statsDropdown);

        // Add action listeners for buttons and dropdown
        cutButton.addActionListener(new ToolbarButtonListener());
        copyButton.addActionListener(new ToolbarButtonListener());
        pasteButton.addActionListener(new ToolbarButtonListener());
        saveButton.addActionListener(new ToolbarButtonListener());
        statsDropdown.addActionListener(new StatsDropdownListener());

        add(toolbar, BorderLayout.NORTH);

        // Create grid panel with row and column labels
        JPanel gridPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Update grid size to 100x100
        int gridSize = 100;
        cells = new JTextField[gridSize][gridSize];

        // Create column labels
        for (int col = 0; col < gridSize; col++) {
            JLabel label = new JLabel(String.valueOf(col + 1), SwingConstants.CENTER);
            gbc.gridx = col + 1;
            gbc.gridy = 0;
            gridPanel.add(label, gbc);
        }

        // Create row labels and cells
        for (int row = 0; row < gridSize; row++) {
            JLabel label = new JLabel(String.valueOf(row + 1), SwingConstants.CENTER);
            gbc.gridx = 0;
            gbc.gridy = row + 1;
            gridPanel.add(label, gbc);

            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setColumns(10);
                cells[row][col].setBorder(LineBorder.createGrayLineBorder());
                cells[row][col].setFont(new Font("Arial", Font.PLAIN, 12));

                // Align text to the right for numeric values
                cells[row][col].setHorizontalAlignment(JTextField.RIGHT);

                final int finalRow = row;
                final int finalCol = col;
                // Add DocumentListener to adjust alignment when text is entered
                cells[finalRow][finalCol].getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        alignText(cells[finalRow][finalCol]);
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        alignText(cells[finalRow][finalCol]);
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        alignText(cells[finalRow][finalCol]);
                    }

                    private void alignText(JTextField textField) {
                        if (textField.getText().matches("\\d+")) {
                            textField.setHorizontalAlignment(JTextField.RIGHT);
                        } else {
                            textField.setHorizontalAlignment(JTextField.LEFT);
                        }
                    }
                });

                gbc.gridx = col + 1;
                gbc.gridy = row + 1;
                gridPanel.add(cells[row][col], gbc);
            }
        }

        // Add scroll bars
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        //pack components

        pack();
    }

    public void makeVisible() {
        this.setVisible(true);
    }

    @Override
    public void addController(IUserController controller) {
        this.controller = controller;
    }

    private class ToolbarButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            // Handle cut, copy, paste, and save actions here
            JOptionPane.showMessageDialog(MainGUI.this, command + " button clicked");
        }
    }

    private class StatsDropdownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
            String selectedStat = (String) comboBox.getSelectedItem();
            // Handle statistical calculation here
            JOptionPane.showMessageDialog(MainGUI.this, selectedStat + " selected");
        }
    }

}
