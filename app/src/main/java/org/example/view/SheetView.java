package org.example.view;

import org.example.controller.IUserController;
import org.example.model.Cell;
import org.example.model.Spreadsheet;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SheetView extends JFrame implements ISheetView {
  private Spreadsheet cells;
  private IUserController controller;

  public SheetView(){
    this.cells = new Spreadsheet();
    setup();
  }

  public SheetView(Spreadsheet openSheet){
    this.cells = openSheet;
    setup();
  }

  private void setup(){
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
    cutButton.addActionListener(new SheetView.ToolbarButtonListener());
    copyButton.addActionListener(new SheetView.ToolbarButtonListener());
    pasteButton.addActionListener(new SheetView.ToolbarButtonListener());
    saveButton.addActionListener(new SheetView.ToolbarButtonListener());
    statsDropdown.addActionListener(new SheetView.StatsDropdownListener());

    add(toolbar, BorderLayout.NORTH);


    //create grid

    int rows = this.cells.getRows();
    int cols = this.cells.getCols();

    JPanel gridPanel = new JPanel(new GridLayout(rows+1, cols+1, 0, 0));

    //print blank cell
    gridPanel.add(new JLabel());
    // Create column labels
    for (int c = 0; c < cols; c++) {
      JLabel label = new JLabel(String.valueOf(c + 1), SwingConstants.CENTER);
      gridPanel.add(label);
    }
    for (int r = 0; r < rows; r++) {
      JLabel label2 = new JLabel(String.valueOf(r + 1), SwingConstants.CENTER);
      gridPanel.add(label2);

      for (Cell cell : this.cells.getCells().get(r)) {
        JTextField textField = new JTextField(cell.getValue());
        textField.setPreferredSize(new Dimension(cell.getWidth(), cell.getHeight()));
        gridPanel.add(textField);
      }
    }

    add(gridPanel, BorderLayout.CENTER);
    // Add scroll bars
    JScrollPane scrollPane = new JScrollPane(gridPanel);
    scrollPane.setPreferredSize(new Dimension(800, 600));
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    add(scrollPane, BorderLayout.CENTER);

    // Set visibility and pack components
    pack();



  }

  @Override
  public void addController(IUserController controller) {
    this.controller = controller;
  }

  @Override
  public void makeVisible() {
    this.setVisible(true);
  }

  private class ToolbarButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();
      // Handle cut, copy, paste, and save actions here
      JOptionPane.showMessageDialog(SheetView.this, command + " button clicked");
    }
  }

  private class StatsDropdownListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
      String selectedStat = (String) comboBox.getSelectedItem();
      // Handle statistical calculation here
      JOptionPane.showMessageDialog(SheetView.this, selectedStat + " selected");
    }
  }
}
