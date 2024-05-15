package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI extends JFrame {

    private JTextArea[][] cells;

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

        cells = new JTextArea[100][100];

        // Create column labels
        for (int col = 0; col < 100; col++) {
            JLabel label = new JLabel(String.valueOf(col + 1), SwingConstants.CENTER);
            gbc.gridx = col + 1;
            gbc.gridy = 0;
            gridPanel.add(label, gbc);
        }

        // Create row labels and cells
        for (int row = 0; row < 100; row++) {
            JLabel label = new JLabel(String.valueOf(row + 1), SwingConstants.CENTER);
            gbc.gridx = 0;
            gbc.gridy = row + 1;
            gridPanel.add(label, gbc);

            for (int col = 0; col < 100; col++) {
                cells[row][col] = new JTextArea();
                cells[row][col].setRows(1);
                cells[row][col].setColumns(10);
                gbc.gridx = col + 1;
                gbc.gridy = row + 1;
                gridPanel.add(cells[row][col], gbc);
            }
        }

        // Add scroll bars
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        add(scrollPane, BorderLayout.CENTER);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainGUI().setVisible(true);
            }
        });
    }
}

