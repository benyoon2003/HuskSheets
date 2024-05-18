package org.example.view;

import org.example.controller.IUserController;
import org.example.model.Cell;
import org.example.model.Spreadsheet;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

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

  private void setup() {
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
//
//    int rows = this.cells.getRows();
//    int cols = this.cells.getCols();

//    JPanel gridPanel = new JPanel(new GridLayout(rows+1, cols+1, 0, 0));
//
//    //print blank cell
//    gridPanel.add(new JLabel());
//    // Create column labels
//    for (int c = 0; c < cols; c++) {
//      JLabel label = new JLabel(String.valueOf(c + 1), SwingConstants.CENTER);
//      gridPanel.add(label);
//    }
//    for (int r = 0; r < rows; r++) {
//      JLabel label2 = new JLabel(String.valueOf(r + 1), SwingConstants.CENTER);
//      gridPanel.add(label2);
//
//      for (Cell cell : this.cells.getCells().get(r)) {
//        JTextField textField = new JTextField(cell.getValue());
//        textField.setPreferredSize(new Dimension(cell.getWidth(), cell.getHeight()));
//        gridPanel.add(textField);
//      }
//    }

    JTable table;
    DefaultTableModel tableModel;

    // Variables to track the starting and ending cell of the selection
    int startRow, startColumn, endRow, endColumn;
    Object[][] data = {
            {"Kundan Kumar Jha", "4031", "CSE"},
            {"Anand Jha", "6014", "IT"},
            {},
            {"Anand Jha", "6014", "IT"}
    };

    // Column Names
    String[] columnNames = {"Name", "Roll Number", "Department"};

    // Create table model
    tableModel = new DefaultTableModel(data, columnNames);

    // Create JTable with the model
    table = new JTable(tableModel);
    table.setSelectionMode(MULTIPLE_INTERVAL_SELECTION);

    table.setCellSelectionEnabled(true);

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        // Display selected cells
        StringBuilder selectedCells = new StringBuilder();
        System.out.println("ROWS: ");
        for (int row : selectedRows) {
          System.out.println(row);
        }
        System.out.println("COLS: ");
        for (int column : selectedColumns) {
          System.out.println(column);
        }
      }
    });

    for (int row = 0; row < table.getRowCount(); row++) {
      for (int col = 0; col < table.getColumnCount(); col++) {
        System.out.println(table.getSelectedRow() + ", " + table.getSelectedColumn());
      }
    }


    add(table, BorderLayout.CENTER);

    // Add scroll bars
    JScrollPane scrollPane = new JScrollPane(table);
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
