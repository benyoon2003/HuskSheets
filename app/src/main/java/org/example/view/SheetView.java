package org.example.view;

import org.example.controller.IUserController;
import org.example.model.Cell;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
import org.example.model.Spreadsheet;

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

import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;

/**
 * The SheetView class provides a GUI for interacting with a spreadsheet.
 * It implements the ISheetView interface to interact with the user controller.
 */
public class SheetView extends JFrame implements ISheetView {
    final IReadOnlySpreadSheet cells;
    IUserController controller;
    JButton backButton;
    JTable yourTable;

    boolean isUpdatingTable = false;
    JTextField formulaTextField;

    private double zoomFactor = 1.0;
    private static final int rowSize = 100;
    private static final int colSize = 100;

    /**
     * Constructs a SheetView with the given spreadsheet.
     *
     * @param openSheet the spreadsheet to be displayed.
     */
    public SheetView(ISpreadsheet openSheet) {
        this.cells = openSheet;
        setup();
    }

    /**
     * Sets up the GUI components for the spreadsheet view.
     */
    public void setup() {
        setTitle("Spreadsheet");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        makeToolbar();

        JTable table;

        // Get data and set column names
        Object[][] data = new Object[rowSize][colSize];
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

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] selectedRows = table.getSelectedRows();
                    int[] selectedColumns = table.getSelectedColumns();
                    controller.selectedCells(selectedRows, selectedColumns);
                }
            }
        });
        table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] selectedRows = table.getSelectedRows();
                    int[] selectedColumns = table.getSelectedColumns();
                    controller.selectedCells(selectedRows, selectedColumns);
                }
            }
        });

        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (!isUpdatingTable) {
                    int selRow = e.getFirstRow();
                    int selCol = e.getColumn();
                    if (selRow != -1 && selCol != -1 && selCol != 0) {
                        String val = String.valueOf(table.getValueAt(selRow, selCol));
                        controller.changeSpreadSheetValueAt(selRow, selCol - 1, val); // Store the formula
                    }
                }
            }
        });

        add(table, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void makeToolbar(){
        // Create toolbar
        JToolBar toolbar = new JToolBar();
        JButton cutButton = new JButton("Cut");
        JButton copyButton = new JButton("Copy");
        JButton pasteButton = new JButton("Paste");
        JButton saveButton = new JButton("Save");
        JButton zoomInButton = new JButton("Zoom In");
        JButton zoomOutButton = new JButton("Zoom Out");
        JButton getUpdates = new JButton("Get Updates");
        backButton = new JButton("Back");
        formulaTextField = new JTextField(20);
        formulaTextField.setEditable(true);
        toolbar.add(new JLabel("Formula:"));
        toolbar.add(formulaTextField);
        formulaTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeSpreadSheetValueAt(controller.getSelectedRowZeroIndex(),
                        controller.getSelectedColZeroIndex(), formulaTextField.getText());
            }
        });

        toolbar.add(cutButton);
        toolbar.add(copyButton);
        toolbar.add(pasteButton);
        toolbar.add(getUpdates);
        toolbar.add(saveButton);
        toolbar.add(zoomInButton);
        toolbar.add(zoomOutButton);
        toolbar.add(backButton);

        // Add action listeners for buttons
        cutButton.addActionListener(new ToolbarButtonListener(this));
        copyButton.addActionListener(new ToolbarButtonListener(this));
        pasteButton.addActionListener(new ToolbarButtonListener(this));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave();
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                IHomeView homeView = controller.getHomeView();
                homeView.updateSavedSheets(); // Update the dropdown before making it visible
                homeView.makeVisible();
            }
        });

        getUpdates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getUpdatesForPublished(cells.getName(), cells.getId_version());
            }
        });

        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomTable(1.1); //Zoom in by 10%
            }
        });

        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomTable(0.9); //Zoom out by 10%
            }
        });

        add(toolbar, BorderLayout.NORTH);
    }

    /**
     * Changes the formula text field to display the given raw data.
     *
     * @param rawdata the raw data to display in the formula text field.
     */
    public void changeFormulaTextField(String rawdata) {
        formulaTextField.setText(rawdata);
    }

    /**
     * Generates an Excel-like column name based on the given column number.
     *
     * @param columnNumber the column number to convert.
     * @return the Excel-like column name.
     */
    public String getExcelColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder();
        while (columnNumber > 0) {
            int remainder = (columnNumber - 1) % 26;
            columnName.insert(0, (char) (remainder + 'A'));
            columnNumber = (columnNumber - 1) / 26;
        }
        return columnName.toString();
    }

    /**
     * Adds a controller to the sheet view.
     *
     * @param controller the IUserController instance to add.
     */
    @Override
    public void addController(IUserController controller) {
        this.controller = controller;
    }

    /**
     * Updates the table with the latest cell data from the spreadsheet.
     */
    public void updateTable() {
        isUpdatingTable = true;
        JTable table = getTable();
        if (table == null) {
            System.out.println("Error: yourTable is null.");
            isUpdatingTable = false;
            return;
        }
        if (cells == null) {
            System.out.println("Error: cells is null.");
            isUpdatingTable = false;
            return;
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String[][] data = this.cells.getCellStringsObject();
        if (data == null) {
            System.out.println("Error: cells.getCellStringsObject() returned null.");
            isUpdatingTable = false;
            return;
        }
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                model.setValueAt(controller.handleReferencingCell(row, col, data[row][col]), row, col + 1);
            }
        }
        model.fireTableDataChanged();
        isUpdatingTable = false;
    }

    /**
     * Gets the JTable instance used in the view.
     *
     * @return the JTable instance.
     */
    protected JTable getTable() {
        return yourTable;
    }

    /**
     * Gets the controller associated with the sheet view.
     *
     * @return the IUserController instance.
     */
    public IUserController getController() {
        return this.controller;
    }

    /**
     * Makes the sheet view visible and updates the table with the latest data.
     */
    @Override
    public void makeVisible() {
        this.updateTable();
        this.setVisible(true);
    }

    /**
     * Saves the spreadsheet to the specified path.
     *
     * @param path the path to save the spreadsheet.
     */
    public void save(String path) {
        try {
            this.controller.saveSheetToServer(this.cells, path);
            System.out.println("Saved spreadsheet '" + path + ".xml'");
        } catch (Exception e) {
            System.out.println("Could not save spreadsheet: " + e.getMessage());
        }
    }

    public void handleSave(){
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
                controller.saveSheetToServer(cells, selectedFile.getAbsolutePath());
            }
        } else if (option == JOptionPane.NO_OPTION) {
            controller.saveSheetToServer(cells, ((Spreadsheet) cells).getName());
            System.out.println(((Spreadsheet) cells).getName());
            makeVisible();
        }
    }
    /**
     * Displays a message in a dialog box.
     *
     * @param s the message to display.
     */
    @Override
    public void displayMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    /**
     * Zooms the table view by the specified factor.
     *
     * @param factor the factor to zoom by.
     */
    void zoomTable(double factor) {
        this.zoomFactor *= factor;
        Font tableFont = yourTable.getFont();
        float newSize = (float) (tableFont.getSize() * factor);
        yourTable.setFont(tableFont.deriveFont(newSize));
        yourTable.setRowHeight((int) (yourTable.getRowHeight() * factor));
        yourTable.getTableHeader().setFont(tableFont.deriveFont(newSize));
    }

    /**
     * Inner class to handle toolbar button actions.
     */
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
                    this.view.getController().saveSheetToServer(this.view.cells, ((Spreadsheet) this.view.cells).getName());
                }
            } else {
                view.getController().handleToolbar(command);
            }
        }
    }
}
