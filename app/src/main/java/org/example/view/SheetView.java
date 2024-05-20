package org.example.view;

import org.example.controller.IUserController;
import org.example.controller.UserController;
import org.example.model.Cell;
import org.example.model.Spreadsheet;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;

public class SheetView extends JFrame implements ISheetView {
  private Spreadsheet cells;
  private IUserController controller;

  private static final int rowSize = 100;
  private static final int colSize = 100;

 private static final Logger logger = LoggerFactory.getLogger(SheetView.class);

  public SheetView()  {
    this.cells = new Spreadsheet();
    setup();
  }

  public SheetView(Spreadsheet openSheet) {
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
    JComboBox<String> statsDropdown = new JComboBox<>(new String[] { "Mean", "Median", "Mode" });
    toolbar.add(statsDropdown);

    // Add action listeners for buttons and dropdown
    cutButton.addActionListener(new SheetView.ToolbarButtonListener(this));
    copyButton.addActionListener(new SheetView.ToolbarButtonListener(this));
    pasteButton.addActionListener(new SheetView.ToolbarButtonListener(this));
    saveButton.addActionListener(new SheetView.ToolbarButtonListener(this));
    statsDropdown.addActionListener(new SheetView.StatsDropdownListener());

    add(toolbar, BorderLayout.NORTH);

    JTable table;

    // Get data and set column names
    Object[][] data = this.cells.getCellStringsObject();
    Cell[][] cellRef = this.cells.getCellsObject();

    String[] columnNames = new String[this.cells.getCols() + 1];
    columnNames[0] = ""; // Empty first column
    for (int i = 1; i <= 100; i++) {
      columnNames[i] = String.valueOf((char) ('A' + (i - 1) % 26)) + (i - 1) / 26; // Generate column labels (A, B, ..., Z, AA, AB, ...)
    }

    // Custom table model with row labels
    DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
      @Override
      public boolean isCellEditable(int row, int column) {
        // Make the first column non-editable (for row labels)
        return column != 0;
      }
    };

    // Add row labels
    for (int i = 0; i < rowSize; i++) {
      tableModel.setValueAt(i + 1, i, 0); // Set row label values
    }

    // Create JTable with the model
    table = new JTable(tableModel);
    table.setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
    table.setAutoResizeMode(0);

    table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(SwingConstants.CENTER); // Align labels to the center
        return this;
      }
    });

    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setCellSelectionEnabled(true);
    table.setShowGrid(true);

    ListSelectionListener cellSelectionListener = new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          int[] selectedRows = table.getSelectedRows();
          int[] selectedColumns = table.getSelectedColumns();
          controller.selectedCells(selectedRows, selectedColumns);
        }
      }
    };

    table.getSelectionModel().addListSelectionListener(cellSelectionListener);
    table.getColumnModel().getSelectionModel().addListSelectionListener(cellSelectionListener);

    table.getModel().addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        int selRow = table.getSelectedRow();
        int selCol = table.getSelectedColumn();
        if (selRow != -1 && selCol != -1) {
          String val = String.valueOf(table.getValueAt(selRow, selCol));
          cellRef[selRow][selCol].setValue(val);
          System.out.println("New Val: " + val);
        }
      }
    });

    add(table, BorderLayout.CENTER);

    // Add scroll bars
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setPreferredSize(new Dimension(800, 600));
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    add(scrollPane, BorderLayout.CENTER);

    // Set visibility and pack components
//    pack();

  }

  @Override
  public void addController(IUserController controller) {
    this.controller = controller;
  }

  @Override
  public void makeVisible() {
    this.setVisible(true);
  }

  private void save(String path) {
    try {
      this.controller.saveSheet(this.cells, path);
      logger.info("Saved spreadsheet");
    } catch (Exception e) {
      logger.info("Could not save spreadsheet: {}", e.getMessage());
    }

  @Override
  public void displayMessage(String s) {
    JOptionPane.showMessageDialog(this, s);
  }

  private class ToolbarButtonListener implements ActionListener {
    private SheetView view;

    ToolbarButtonListener(SheetView view) {
      this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();
      // Handle cut, copy, paste, and save actions here
      JOptionPane.showMessageDialog(SheetView.this, command + " button clicked");

      if (command == "Save") {
        this.view.save("./example.xml");
      }
      
      controller.handleToolbar(command);
    }
  }

  private class StatsDropdownListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
      String selectedStat = (String) comboBox.getSelectedItem();
      // Handle statistical calculation here
      controller.handleStatsDropdown(selectedStat);
    }
  }
}
