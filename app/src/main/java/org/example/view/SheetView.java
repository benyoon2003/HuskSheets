package org.example.view;

import org.example.controller.IUserController;
import org.example.model.Cell;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
import org.example.model.SelectedCells;
import org.example.model.Spreadsheet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;

/**
 * The SheetView class represents the view for displaying and interacting with a spreadsheet.
 */
public class SheetView extends JFrame implements ISheetView {
    final IReadOnlySpreadSheet cells; // The spreadsheet data
    IUserController controller; // Controller for handling user actions
    JButton backButton; // Button to go back to the previous view
    JTable yourTable; // Table to display the spreadsheet data
    boolean isUpdatingTable = false; // Flag to check if the table is being updated
    JTextField formulaTextField; // Text field to display/edit the formula of the selected cell

    private double zoomFactor = 1.0; // Zoom factor for the table
    private static final int rowSize = 100; // Number of rows in the table
    private static final int colSize = 100; // Number of columns in the table
    public static final Color PINK = new Color(255, 192, 203); // Color constant for pink
    public static final Color GREEN = new Color(0, 255, 0); // Color constant for green

    private final Map<Point, Color> highlightedCells = new HashMap<>(); // Map to store highlighted cells
    private SelectedCells selectedCells; // Object to store selected cell range

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
     * Sets up the components and layout of the SheetView.
     */
    public void setup() {
        setTitle("Spreadsheet");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        makeToolbar(); // Create the toolbar

        // Initialize data array with cell values
        Object[][] data = new Object[rowSize][colSize];
        Cell[][] cellRef = this.cells.getCellsObject();

        for (Cell[] row : cellRef) {
            for (Cell c : row) {
                data[c.getRow()][c.getCol()] = c.getValue();
            }
        }

        // Initialize column names
        String[] columnNames = new String[colSize + 1];
        columnNames[0] = "";
        for (int i = 1; i <= colSize; i++) {
            columnNames[i] = getExcelColumnName(i);
        }

        // Create table model
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Make all columns except the first one editable
            }
        };

        // Set row headers
        for (int i = 0; i < rowSize; i++) {
            tableModel.setValueAt(i + 1, i, 0);
        }

        // Initialize table with the model
        yourTable = new JTable(tableModel);
        yourTable.setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
        yourTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        yourTable.setCellSelectionEnabled(true);
        yourTable.setShowGrid(true);

        // Set custom cell renderer
        yourTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer(highlightedCells));

        // Add key listener for delete and digit keys
        yourTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    System.out.println(e.getKeyCode() == KeyEvent.VK_DELETE ? "Delete key pressed" : "Backspace key pressed");
                    controller.updateSelectedCells(""); // Pass an empty string to clear cells
                } else if (Character.isDigit(e.getKeyChar())) {
                    System.out.println("Digit key pressed: " + e.getKeyChar());
                    controller.updateSelectedCells(String.valueOf(e.getKeyChar()));
                }
            }
        });

        // Add selection listener for row selection
        yourTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] selectedRows = yourTable.getSelectedRows();
                    int[] selectedColumns = yourTable.getSelectedColumns();
                    controller.selectedCells(selectedRows, selectedColumns);
                }
            }
        });

        // Add selection listener for column selection
        yourTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] selectedRows = yourTable.getSelectedRows();
                    int[] selectedColumns = yourTable.getSelectedColumns();
                    controller.selectedCells(selectedRows, selectedColumns);
                }
            }
        });

        // Add table model listener for data changes
        yourTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (!isUpdatingTable) {
                    int selRow = e.getFirstRow();
                    int selCol = e.getColumn();
                    if (selRow != -1 && selCol != -1 && selCol != 0) {
                        String val = String.valueOf(yourTable.getValueAt(selRow, selCol));
                        controller.changeSpreadSheetValueAt(selRow, selCol - 1, val);
                    }
                }
            }
        });

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(yourTable);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Handles the selection of cells in the table.
     *
     * @param selectedRows the selected rows.
     * @param selectedColumns the selected columns.
     */
    @Override
    public void selectedCells(int[] selectedRows, int[] selectedColumns) {
        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            int startRow = selectedRows[0];
            int endRow = selectedRows[selectedRows.length - 1];
            int startColumn = selectedColumns[0];
            int endColumn = selectedColumns[selectedColumns.length - 1];

            this.selectedCells = new SelectedCells(startRow + 1, endRow + 1, startColumn + 1, endColumn + 1);

            System.out.println("Selected range: (" + (selectedCells.getStartRow()) + ", " +
                    selectedCells.getStartCol() + ") to (" + selectedCells.getEndRow() + ", "
                    + selectedCells.getEndCol() + ")");

            if (this.singleCellSelected(this.selectedCells)) {
                this.changeFormulaTextField(this.cells.getCellRawdata(
                        this.selectedCells.getStartRow() - 1, this.selectedCells.getStartCol() - 1));
            }
        } else {
            this.selectedCells = new SelectedCells(-1, -1, -1, -1);
        }
    }

    /**
     * Checks if a single cell is selected.
     *
     * @param selectedCells the selected cells.
     * @return true if a single cell is selected, false otherwise.
     */
    private boolean singleCellSelected(SelectedCells selectedCells) {
        return selectedCells.getStartRow() == selectedCells.getEndRow() &&
                selectedCells.getStartCol() == selectedCells.getEndCol();
    }

    /**
     * Creates the toolbar for the SheetView.
     */
    public void makeToolbar() {
        JToolBar toolbar = new JToolBar();
        JButton cutButton = new JButton("Cut");
        JButton copyButton = new JButton("Copy");
        JButton pasteButton = new JButton("Paste");
        JButton saveButton = new JButton("Save");
        JButton zoomInButton = new JButton("Zoom In");
        JButton zoomOutButton = new JButton("Zoom Out");
        JButton getUpdates = new JButton("Get Updates");
        JButton conditionalFormattingButton = new JButton("Add Conditional Formatting");
        backButton = new JButton("Back");
        formulaTextField = new JTextField(20);
        formulaTextField.setEditable(true);

        toolbar.add(new JLabel("Formula:"));
        toolbar.add(formulaTextField);
        toolbar.add(cutButton);
        toolbar.add(copyButton);
        toolbar.add(pasteButton);
        toolbar.add(getUpdates);
        toolbar.add(zoomInButton);
        toolbar.add(zoomOutButton);
        toolbar.add(saveButton);
        toolbar.add(conditionalFormattingButton);
        toolbar.add(backButton);

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
                dispose(); // Close the current view
                IHomeView homeView = controller.getHomeView();
                homeView.updateSavedSheets();
                homeView.makeVisible();
            }
        });

        getUpdates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    controller.getUpdatesForPublished(cells.getName(), cells.getId_version());
                } catch (Exception j) {
                    JOptionPane.showMessageDialog(null, j.getMessage());
                }
            }
        });

        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomTable(1.1);
            }
        });

        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomTable(0.9);
            }
        });

        conditionalFormattingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.applyConditionalFormatting();
            }
        });

        formulaTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeSpreadSheetValueAt(controller.getSelectedRowZeroIndex(),
                        controller.getSelectedColZeroIndex(), formulaTextField.getText());
            }
        });

        add(toolbar, BorderLayout.NORTH);

        revalidate();
        repaint();
    }

    /**
     * Changes the text in the formula text field.
     *
     * @param rawdata the raw data to set.
     */
    public void changeFormulaTextField(String rawdata) {
        formulaTextField.setText(rawdata);
    }

    /**
     * Highlights a cell with the specified color.
     *
     * @param row the row of the cell.
     * @param col the column of the cell.
     * @param color the color to highlight.
     */
    public void highlightCell(int row, int col, Color color) {
        highlightedCells.put(new Point(row, col + 1), color);
        yourTable.repaint();
    }

    /**
     * Converts a column number to its corresponding Excel column name.
     *
     * @param columnNumber the column number.
     * @return the Excel column name.
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
     * Adds a controller to the SheetView.
     *
     * @param controller the controller to add.
     */
    @Override
    public void addController(IUserController controller) {
        this.controller = controller;
    }

    /**
     * Updates the table with the latest data.
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
                String value = controller.handleReferencingCell(row, col, data[row][col]);
                model.setValueAt(value, row, col + 1);
                if (!value.isEmpty()) {
                    System.out.println("Setting cell (" + row + ", " + col + ") to value: " + value);
                }
            }
        }
        model.fireTableDataChanged();
        isUpdatingTable = false;
    }

    /**
     * Gets the table used in the SheetView.
     *
     * @return the table.
     */
    protected JTable getTable() {
        return yourTable;
    }

    /**
     * Gets the controller associated with the SheetView.
     *
     * @return the controller.
     */
    public IUserController getController() {
        return this.controller;
    }

    /**
     * Makes the SheetView visible.
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

    /**
     * Handles the save action.
     */
    public void handleSave() {
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
            controller.saveSheetToServer(cells, cells.getName());
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
     * Zooms the table by the specified factor.
     *
     * @param factor the zoom factor.
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
     * Loads changes into the SheetView.
     *
     * @throws Exception if an error occurs while loading changes.
     */
    @Override
    public void loadChanges() throws Exception {
    }

    /**
     * The ToolbarButtonListener class handles toolbar button actions.
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
                    this.view.getController().saveSheetToServer(this.view.cells,
                            ((Spreadsheet) this.view.cells).getName());
                }
            } else {
                view.getController().handleToolbar(command);
            }
        }
    }

    /**
     * The RightClickButtonListener class handles right-click button actions.
     */
    class RightClickButtonListener implements ActionListener {
        private SheetView view;

        RightClickButtonListener(SheetView view) {
            this.view = view;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            int row = this.view.yourTable.getSelectedRow();
            int col = this.view.yourTable.getSelectedColumn() - 1;

            if (command.equals("Percentile")) {
                this.view.getController().getPercentile(row, col);
            }

            this.view.updateTable();
        }
    }

    /**
     * The CustomTableCellRenderer class customizes the rendering of table cells.
     */
    class CustomTableCellRenderer extends DefaultTableCellRenderer {
        private final Map<Point, Color> highlightedCells;

        /**
         * Constructs a CustomTableCellRenderer with the specified highlighted cells.
         *
         * @param highlightedCells the highlighted cells.
         */
        public CustomTableCellRenderer(Map<Point, Color> highlightedCells) {
            this.highlightedCells = highlightedCells;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Point cellLocation = new Point(row, column);
            Color highlightColor = highlightedCells.get(cellLocation);
            if (highlightColor != null) {
                c.setBackground(highlightColor);
            } else {
                c.setBackground(Color.WHITE);
            }
            if (isSelected) {
                c.setBackground(Color.CYAN);
            }
            return c;
        }
    }
}
