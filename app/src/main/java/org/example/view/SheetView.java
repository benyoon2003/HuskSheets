package org.example.view;

import org.example.controller.IUserController;
import org.example.model.Cell;
import org.example.model.ISpreadsheet;
import org.example.model.ReadOnlySpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;

public class SheetView extends JFrame implements ISheetView {
    final ReadOnlySpreadSheet cells;
    private IUserController controller;
    private JButton backButton;
    JTable yourTable;
    private boolean isUpdatingTable = false;

    private static final int rowSize = 100;
    private static final int colSize = 100;

    private static final Logger logger = LoggerFactory.getLogger(SheetView.class);

    public SheetView(ISpreadsheet openSheet) {
        this.cells = new ReadOnlySpreadSheet(openSheet.getCellsObject());
        setup();
    }

    private void setup() {
        setTitle("Spreadsheet");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create toolbar
        JToolBar toolbar = new JToolBar();
        JButton cutButton = new JButton("Cut");
        JButton copyButton = new JButton("Copy");
        JButton pasteButton = new JButton("Paste");
        JButton saveButton = new JButton("Save");
        backButton = new JButton("Back");

        toolbar.add(cutButton);
        toolbar.add(copyButton);
        toolbar.add(pasteButton);
        toolbar.add(saveButton);
        toolbar.add(backButton);

        // Create dropdown menu for statistical calculations
        JComboBox<String> statsDropdown = new JComboBox<>(new String[]{"Mean", "Median", "Mode"});
        toolbar.add(statsDropdown);

        // Add action listeners for buttons and dropdown
        cutButton.addActionListener(new ToolbarButtonListener(this));
        copyButton.addActionListener(new ToolbarButtonListener(this));
        pasteButton.addActionListener(new ToolbarButtonListener(this));
        saveButton.addActionListener(new ToolbarButtonListener(this));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                IHomeView homeView = controller.getHomeView();
                homeView.updateSavedSheets(); // Update the dropdown before making it visible
                homeView.makeVisible();
            }
        });
        statsDropdown.addActionListener(new StatsDropdownListener());

        add(toolbar, BorderLayout.NORTH);

        JTable table;

        // Get data and set column names
        Object[][] data = new Object[rowSize][colSize];//this.cells.getCellStringsObject();
        Cell[][] cellRef = this.cells.getCellsObject();

        for (Cell[] row : cellRef) {
            for (Cell c : row) {
                data[c.getRow()][c.getCol()] = c.getValue();
            }
        }

        String[] columnNames = new String[colSize + 1];
        columnNames[0] = ""; // Empty first column
        for (int i = 1; i <= colSize; i++) {
            columnNames[i] = getExcelColumnName(i); // Generate Excel-like column labels
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
        this.yourTable = table; // Set the yourTable variable
        table.setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
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
                if (!isUpdatingTable) {
                    int selRow = e.getFirstRow();
                    int selCol = e.getColumn();
                    if (selRow != -1 && selCol != -1 && selCol != 0) {
                        String val = String.valueOf(table.getValueAt(selRow, selCol));
                        controller.changeSpreadSheetValueAt(selRow, selCol - 1, val);
                    }
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
    }

    // Helper function to generate Excel-like column names
    private String getExcelColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder();
        while (columnNumber > 0) {
            int remainder = (columnNumber - 1) % 26;
            columnName.insert(0, (char) (remainder + 'A'));
            columnNumber = (columnNumber - 1) / 26;
        }
        return columnName.toString();
    }

    @Override
    public void addController(IUserController controller) {
        this.controller = controller;
    }

    public void updateTable() {
        isUpdatingTable = true;
        JTable table = getTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String[][] data = this.cells.getCellStringsObject();
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                model.setValueAt(data[row][col], row, col + 1);
            }
        }
        model.fireTableDataChanged();
        isUpdatingTable = false;
    }

    private JTable getTable() {
        return yourTable;
    }

    public IUserController getController() {
        return this.controller;
    }

    @Override
    public void makeVisible() {
        this.updateTable();
        this.setVisible(true);
    }

    public void save(String path) {
        try {
            this.controller.saveSheet(this.cells, path);
            System.out.println("Saved spreadsheet '" + path + ".xml'");
        } catch (Exception e) {
            System.out.println("Could not save spreadsheet: " + e.getMessage());
        }
    }

    @Override
    public void displayMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    class ToolbarButtonListener implements ActionListener {
        private SheetView view;

        ToolbarButtonListener(SheetView view) {
            this.view = view;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("Cut")) {
                int selRow = view.yourTable.getSelectedRow();
                int selCol = view.yourTable.getSelectedColumn();
                if (selRow != -1 && selCol != -1 && selCol != 0) {
                    view.getController().cutCell(selRow, selCol - 1);
                }
            } else if (command.equals("Copy")) {
                int selRow = view.yourTable.getSelectedRow();
                int selCol = view.yourTable.getSelectedColumn();
                if (selRow != -1 && selCol != -1 && selCol != 0) {
                    view.getController().copyCell(selRow, selCol - 1);
                }
            } else if (command.equals("Paste")) {
                int selRow = view.yourTable.getSelectedRow();
                int selCol = view.yourTable.getSelectedColumn();
                if (selRow != -1 && selCol != -1 && selCol != 0) {
                    view.getController().pasteCell(selRow, selCol - 1);
                }
            } else if (command.equals("Save")) {
                int option = JOptionPane.showOptionDialog(
                        null,
                        "Choose where to save the sheet:",
                        "Save Option",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Save Locally", "Save to Server"},
                        "Save Locally");

                if (option == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    int returnValue = fileChooser.showSaveDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        this.view.save(selectedFile.getAbsolutePath());
                    }
                } else if (option == JOptionPane.NO_OPTION) {
                    String name = JOptionPane.showInputDialog("Enter a name for the sheet:");
                    if (name != null && !name.trim().isEmpty()) {
                        this.view.getController().saveSheetToServer(this.view.cells, name);
                    } else {
                        JOptionPane.showMessageDialog(null, "Sheet name cannot be empty.");
                    }
                }
            } else {
                view.getController().handleToolbar(command);
            }
        }
    }

    class StatsDropdownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
            String selectedStat = (String) comboBox.getSelectedItem();
            // Handle statistical calculation here
            controller.handleStatsDropdown(selectedStat);
        }
    }

    // Updating the formula calculation to use the new column nomenclature
    public String evaluateFormula(String formula) {
        if (!formula.startsWith("=")) {
            return formula;
        }

        // Remove the initial "="
        formula = formula.substring(1);

        try {
            if (formula.contains(":")) {
                String[] parts = formula.split(":");
                return rangeOperation(parts[0].trim(), parts[1].trim());
            }

            // Replace cell references with their values
            formula = replaceCellReferences(formula);

            // Handle special operations
            if (formula.contains("<>")) {
                String[] parts = formula.split("<>");
                return compareNotEqual(parts[0].trim(), parts[1].trim());
            } else if (formula.contains("<") && !formula.contains("=")) {
                String[] parts = formula.split("<");
                return compareLess(parts[0].trim(), parts[1].trim());
            } else if (formula.contains(">") && !formula.contains("=")) {
                String[] parts = formula.split(">");
                return compareGreater(parts[0].trim(), parts[1].trim());
            } else if (formula.contains("=") && !formula.contains("<") && !formula.contains(">")) {
                String[] parts = formula.split("=");
                return compareEqual(parts[0].trim(), parts[1].trim());
            } else if (formula.contains("&")) {
                String[] parts = formula.split("&");
                return andOperation(parts[0].trim(), parts[1].trim());
            } else if (formula.contains("|")) {
                String[] parts = formula.split("\\|");
                return orOperation(parts[0].trim(), parts[1].trim());
            } else if (formula.startsWith("IF(")) {
                return evaluateIF(formula.substring(3, formula.length() - 1));
            } else if (formula.startsWith("SUM(")) {
                return evaluateSUM(formula.substring(4, formula.length() - 1));
            } else if (formula.startsWith("MIN(")) {
                return evaluateMIN(formula.substring(4, formula.length() - 1));
            } else if (formula.startsWith("MAX(")) {
                return evaluateMAX(formula.substring(4, formula.length() - 1));
            } else if (formula.startsWith("AVG(")) {
                return evaluateAVG(formula.substring(4, formula.length() - 1));
            } else if (formula.startsWith("CONCAT(")) {
                return evaluateCONCAT(formula.substring(7, formula.length() - 1));
            } else if (formula.startsWith("DEBUG(")) {
                return evaluateDEBUG(formula.substring(6, formula.length() - 1));
            } else {
                // For simplicity, handle basic arithmetic operations
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                Object result = engine.eval(formula);
                return result.toString();
            }
        } catch (ScriptException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    private String replaceCellReferences(String formula) {
        Pattern pattern = Pattern.compile("\\$[A-Z]+[0-9]+");
        Matcher matcher = pattern.matcher(formula);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String cellReference = matcher.group();
            int row = getRow(cellReference);
            int col = getColumn(cellReference);
            String cellValue = getCellValue(row, col);
            matcher.appendReplacement(result, cellValue);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private int getRow(String cell) {
        try {
            return Integer.parseInt(cell.replaceAll("[^0-9]", "")) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int getColumn(String cell) {
        String col = cell.replaceAll("[^A-Z]", "").toUpperCase();
        int column = 0;
        for (int i = 0; i < col.length(); i++) {
            column = column * 26 + (col.charAt(i) - 'A' + 1);
        }
        return column - 1;
    }

    private String getCellValue(int row, int col) {
        return cells.getCellsObject()[row][col].getValue();
    }

    private String compareLess(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a < b ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String compareGreater(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a > b ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String compareEqual(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a == b ? "1" : "0";
        } catch (NumberFormatException e) {
            if (x.equals(y)) {
                return "1";
            } else {
                return "0";
            }
        }
    }

    private String compareNotEqual(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a != b ? "1" : "0";
        } catch (NumberFormatException e) {
            if (!x.equals(y)) {
                return "1";
            } else {
                return "0";
            }
        }
    }

    private String andOperation(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return (a != 0 && b != 0) ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String orOperation(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return (a != 0 || b != 0) ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String rangeOperation(String startCell, String endCell) {
        int startRow = getRow(startCell);
        int endRow = getRow(endCell);
        int startCol = getColumn(startCell);
        int endCol = getColumn(endCell);

        // Check if the range is valid
        if (startRow > endRow || startCol > endCol || startRow == -1 || endRow == -1 || startCol == -1 || endCol == -1) {
            return "Error";
        }

        StringBuilder rangeResult = new StringBuilder();
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                String cellValue = getCellValue(row, col);
                if (!cellValue.isEmpty()) {
                    rangeResult.append(cellValue).append(",");
                }
            }
        }
        return rangeResult.length() > 0 ? rangeResult.substring(0, rangeResult.length() - 1) : "";
    }

    private String evaluateIF(String parameters) {
        String[] parts = parameters.split(",");
        if (parts.length != 3) {
            return "Error";
        }
        String condition = parts[0].trim();
        String trueResult = parts[1].trim();
        String falseResult = parts[2].trim();

        try {
            double conditionValue = Double.parseDouble(condition);
            return conditionValue != 0 ? trueResult : falseResult;
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateSUM(String parameters) {
        String[] parts = parameters.split(",");
        double sum = 0;
        try {
            for (String part : parts) {
                sum += Double.parseDouble(part.trim());
            }
            return String.valueOf(sum);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateMIN(String parameters) {
        String[] parts = parameters.split(",");
        double min = Double.MAX_VALUE;
        try {
            for (String part : parts) {
                double value = Double.parseDouble(part.trim());
                if (value < min) {
                    min = value;
                }
            }
            return String.valueOf(min);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateMAX(String parameters) {
        String[] parts = parameters.split(",");
        double max = Double.MIN_VALUE;
        try {
            for (String part : parts) {
                double value = Double.parseDouble(part.trim());
                if (value > max) {
                    max = value;
                }
            }
            return String.valueOf(max);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateAVG(String parameters) {
        String[] parts = parameters.split(",");
        double sum = 0;
        try {
            for (String part : parts) {
                sum += Double.parseDouble(part.trim());
            }
            return String.valueOf(sum / parts.length);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateCONCAT(String parameters) {
        String[] parts = parameters.split(",");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            result.append(part.trim());
        }
        return result.toString();
    }

    private String evaluateDEBUG(String parameter) {
        return parameter.trim();
    }
}

