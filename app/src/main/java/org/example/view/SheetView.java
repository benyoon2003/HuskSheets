package org.example.view;

import org.example.controller.IUserController;
import org.example.model.Cell;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
import org.example.model.SelectedCells;
import org.example.view.button.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;

/**
 * The SheetView class represents the view for displaying and interacting with a
 * spreadsheet.
 */
public class SheetView extends SheetViewFactory<SheetView> implements ISheetView {
    public IReadOnlySpreadSheet cells; // The spreadsheet data
    protected IUserController controller; // Controller for handling user actions
    protected JButton backButton; // Button to go back to the previous view
    public JTable yourTable; // Table to display the spreadsheet data
    protected boolean isUpdatingTable = false; // Flag to check if the table is being updated
    protected JTextField formulaTextField; // Text field to display/edit the formula of the selected cell

    protected static final int rowSize = 100; // Number of rows in the table
    protected static final int colSize = 100; // Number of columns in the table
    public static final Color PINK = new Color(255, 192, 203); // Color constant for pink
    public static final Color GREEN = new Color(0, 255, 0); // Color constant for green

    public String publisher;
    protected final Map<Point, Color> highlightedCells = new HashMap<>(); // Map to store highlighted cells
    protected SelectedCells selectedCells; // Object to store selected cell range


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
        setTitle("Spreadsheet"); // Set the title of the window
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        makeToolbar(); // Create the toolbar
        Object[][] data = initializeData(); // Initialize the data array
        initializeColumnNames(); // Initialize the column names
        String[] columnNames = initializeColumnNames(); // Get the column names
        DefaultTableModel tableModel = createTableModel(data, columnNames); // Create the table model
        setRowHeaders(tableModel); // Set the row headers
        initalizeTableModel(tableModel); // Initialize the table model
        JPanel rightClickPanel = configureCells(); // Configure the cells for right-click actions
        addMouseListener(rightClickPanel); // Add mouse listener for right-click actions
        listenForDelete(); // Add key listener for delete actions
        listenForSelectionRow(); // Add selection listener for row selection
        listenForSelectionCol(); // Add selection listener for column selection
        listenForDataChanges(); // Add table model listener for data changes
        addTableToScroll(); // Add the table to a scroll pane
    }


    /**
     * Handles the selection of cells in the table.
     *
     * @param selectedRows    the selected rows.
     * @param selectedColumns the selected columns.
     */
    @Override
    public void selectedCells(int[] selectedRows, int[] selectedColumns) {
        if (selectedRows.length > 0 && selectedColumns.length > 0) { // Check if any cells are selected
            int startRow = selectedRows[0]; // Get the starting row of the selection
            int endRow = selectedRows[selectedRows.length - 1]; // Get the ending row of the selection
            int startColumn = selectedColumns[0]; // Get the starting column of the selection
            int endColumn = selectedColumns[selectedColumns.length - 1]; // Get the ending column of the selection

            this.selectedCells = new SelectedCells(startRow + 1, endRow + 1, startColumn + 1, endColumn + 1); // Create a new SelectedCells object with the selected range

            System.out.println("Selected range: (" + (selectedCells.getStartRow()) + ", " +
                    selectedCells.getStartCol() + ") to (" + selectedCells.getEndRow() + ", "
                    + selectedCells.getEndCol() + ")"); // Print the selected range

            if (this.singleCellSelected(this.selectedCells)) { // Check if a single cell is selected
                this.changeFormulaTextField(this.cells.getCellRawdata(
                        this.selectedCells.getStartRow(), this.selectedCells.getStartCol())); // Change the formula text field to the raw data of the selected cell
            }
        } else {
            this.selectedCells = new SelectedCells(-1, -1, -1, -1); // Set selectedCells to an invalid range if no cells are selected
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
                selectedCells.getStartCol() == selectedCells.getEndCol(); // Return true if the start and end rows and columns are the same
    }

    /**
     * Creates the toolbar for the SheetView.
     */
    protected void makeToolbar() {
        formulaTextField = new JTextField(20); // Create a text field for formulas
        formulaTextField.setEditable(true); // Make the text field editable
        this.addComponent(new JLabel("Formula")) // Add a label for the formula text field
                .addComponent(formulaTextField) // Add the formula text field to the toolbar
                .addComponent(new Cut(this)) // Add Cut button to the toolbar
                .addComponent(new Copy(this)) // Add Copy button to the toolbar
                .addComponent(new Paste(this)) // Add Paste button to the toolbar
                .addComponent(new ZoomI(this)) // Add Zoom In button to the toolbar
                .addComponent(new ZoomO(this)) // Add Zoom Out button to the toolbar
                .addComponent(new GetUpdatesFromSubscriber(this)) // Add Get Updates from Subscriber button to the toolbar
                .addComponent(new SavePublisher(this)) // Add Save Publisher button to the toolbar
                .addComponent(new AddConditionalFormat(this)) // Add Add Conditional Format button to the toolbar
                .addComponent(new Back(this)); // Add Back button to the toolbar

        formulaTextField.addActionListener(new ActionListener() { // Add action listener to the formula text field
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeSpreadSheetValueAt(controller.getSelectedStartRow(),
                        controller.getSelectedStartCol(), formulaTextField.getText()); // Change the spreadsheet value when action is performed
            }
        });
        revalidate(); // Revalidate the component hierarchy
        repaint(); // Repaint the component hierarchy
    }

    /**
     * Changes the text in the formula text field.
     *
     * @param rawdata the raw data to set.
     */
    public void changeFormulaTextField(String rawdata) {
        formulaTextField.setText(rawdata); // Set the text of the formula text field to the provided raw data
    }

    /**
     * Highlights a cell with the specified color.
     *
     * @param row   the row of the cell.
     * @param col   the column of the cell.
     * @param color the color to highlight.
     */
    public void highlightCell(int row, int col, Color color) {
        highlightedCells.put(new Point(row, col + 1), color); // Add the cell to the highlighted cells map with the specified color
        yourTable.repaint(); // Repaint the table to reflect the highlighted cell
    }

    /**
     * Converts a column number to its corresponding Excel column name.
     *
     * @param columnNumber the column number.
     * @return the Excel column name.
     */
    public String getExcelColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder(); // Create a StringBuilder for the column name
        while (columnNumber > 0) { // Loop to convert the column number to an Excel column name
            int remainder = (columnNumber - 1) % 26; // Calculate the remainder
            columnName.insert(0, (char) (remainder + 'A')); // Insert the character at the beginning of the column name
            columnNumber = (columnNumber - 1) / 26; // Update the column number
        }
        return columnName.toString(); // Return the column name
    }

    /**
     * Adds a controller to the SheetView.
     *
     * @param controller the controller to add.
     */
    @Override
    public void addController(IUserController controller) {
        this.controller = controller; // Set the controller
        this.publisher = controller.getAppUser().getUsername(); // Set the publisher to the username of the app user
    }

    /**
     * Updates the table with the latest data.
     */
    public void updateTable() {
        isUpdatingTable = true; // Set the flag to indicate that the table is being updated
        JTable table = getTable(); // Get the table
        if (table == null) {
            System.out.println("Error: yourTable is null."); // Print an error message if the table is null
            isUpdatingTable = false; // Reset the flag
            return;
        }
        if (cells == null) {
            System.out.println("Error: cells is null."); // Print an error message if the cells are null
            isUpdatingTable = false; // Reset the flag
            return;
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel(); // Get the table model
        String[][] data = this.cells.getCellStringsObject(); // Get the cell data as a 2D array
        if (data == null) {
            System.out.println("Error: cells.getCellStringsObject() returned null."); // Print an error message if the data is null
            isUpdatingTable = false; // Reset the flag
            return;
        }
        for (int row = 0; row < data.length; row++) { // Loop through the rows
            for (int col = 0; col < data[row].length; col++) { // Loop through the columns
                String value = controller.handleReevaluatingCellFormula(row, col, data[row][col]); // Reevaluate the cell formula
                model.setValueAt(value, row, col + 1); // Set the cell value in the model
                if (!value.isEmpty()) {
                    System.out.println("Setting cell (" + row + ", " + col + ") to value: " + value); // Print the cell value if it is not empty
                }
            }
        }
        model.fireTableDataChanged(); // Notify the model that the data has changed
        isUpdatingTable = false; // Reset the flag
    }

    /**
     * Gets the table used in the SheetView.
     *
     * @return the table.
     */
    protected JTable getTable() {
        return yourTable; // Return the table
    }

    /**
     * Gets the controller associated with the SheetView.
     *
     * @return the controller.
     */
    public IUserController getController() {
        return this.controller; // Return the controller
    }

    /**
     * Makes the SheetView visible.
     */
    @Override
    public void makeVisible() {
        this.updateTable(); // Update the table with the latest data
        this.setVisible(true); // Make the view visible
    }

    /**
     * Saves the spreadsheet to the specified path.
     *
     * @param path the path to save the spreadsheet.
     */
    public void save(String path) {
        try {
            this.controller.saveSheetLocally(this.cells, path); // Save the spreadsheet locally
            System.out.println("Saved spreadsheet '" + path + ".xml'"); // Print a message indicating successful save
        } catch (Exception e) {
            System.out.println("Could not save spreadsheet: " + e.getMessage()); // Print an error message if save fails
        }
    }

    /**
     * Displays a message in a dialog box.
     *
     * @param s the message to display.
     */
    @Override
    public void displayMessage(String s) {
        JOptionPane.showMessageDialog(this, s); // Display the message in a dialog box
    }

    /**
     * Zooms the table by the specified factor.
     *
     * @param factor the zoom factor.
     */
    public void zoomTable(double factor) {
        Font tableFont = yourTable.getFont(); // Get the current font of the table
        float newSize = (float) (tableFont.getSize() * factor); // Calculate the new font size
        yourTable.setFont(tableFont.deriveFont(newSize)); // Set the new font size
        yourTable.setRowHeight((int) (yourTable.getRowHeight() * factor)); // Set the new row height
        yourTable.getTableHeader().setFont(tableFont.deriveFont(newSize)); // Set the new font size for the table header
    }

    /**
     * Loads changes into the SheetView.
     *
     * @throws Exception if an error occurs while loading changes.
     */
    @Override
    public void loadChanges() throws Exception {
        // No implementation provided in the base class
    }

    @Override
    protected SheetView returnView() {
        return this; // Return the current instance of SheetView
    }

    @Override
    protected SheetView build() {
        this.add(toolBar, BorderLayout.NORTH); // Add the toolbar to the north of the layout
        return this; // Return the current instance of SheetView
    }

    /**
     * Initializes the data array with cell values.
     *
     * @return the initialized data array.
     */
    protected Object[][] initializeData(){
        // Initialize data array with cell values
        Object[][] data = new Object[rowSize][colSize]; // Create a new data array
        Cell[][] cellRef = this.cells.getCellsObject(); // Get the cell reference array

        for (Cell[] row : cellRef) { // Loop through the rows
            for (Cell c : row) { // Loop through the cells
                data[c.getRow()][c.getCol()] = c.getValue(); // Set the cell value in the data array
            }
        }
        return data; // Return the data array
    }

    /**
     * Initializes the column names.
     *
     * @return the initialized column names.
     */
    protected String[] initializeColumnNames(){
        // Initialize column names
        String[] columnNames = new String[colSize + 1]; // Create a new column names array
        columnNames[0] = ""; // Set the first column name to an empty string
        for (int i = 1; i <= colSize; i++) { // Loop through the columns
            columnNames[i] = getExcelColumnName(i); // Set the column name to the corresponding Excel column name
        }
        return columnNames; // Return the column names array
    }

    /**
     * Configures the cells for right-click actions.
     *
     * @return the panel for right-click actions.
     */
    protected JPanel configureCells(){
        // Set custom cell renderer
        yourTable.setDefaultRenderer(Object.class, new HighlightedCellRenderer(highlightedCells)); // Set the custom cell renderer for the table

        // Add panel for right-clicks
        JPanel rightClickPanel = new JPanel(new GridLayout(1, 1)); // Create a new panel for right-click actions
        rightClickPanel.setSize(new Dimension(100, 15)); // Set the size of the panel

        // Add buttons to right-click panel
        JButton percentiles = new JButton("Percentile"); // Create a new button for percentiles
        percentiles.setPreferredSize(new Dimension(100, 15)); // Set the preferred size of the button
        percentiles.addActionListener(new RightClickButtonListener(this)); // Add action listener to the button
        percentiles.setVisible(rightClickPanel.isVisible()); // Set the visibility of the button
        rightClickPanel.add(percentiles); // Add the button to the panel
        rightClickPanel.setVisible(false); // Set the visibility of the panel to false

        yourTable.add(rightClickPanel); // Add the panel to the table
        return rightClickPanel; // Return the panel
    }

    /**
     * Initializes the table model.
     *
     * @param tableModel the table model to initialize.
     */
    protected void initalizeTableModel(DefaultTableModel tableModel) {
        yourTable = new JTable(tableModel); // Initialize the table with the model
        yourTable.setSelectionMode(MULTIPLE_INTERVAL_SELECTION); // Set the selection mode to multiple interval selection
        yourTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Disable auto-resize mode
        yourTable.setCellSelectionEnabled(true); // Enable cell selection
        yourTable.setShowGrid(true); // Show grid lines
    }

    /**
     * Sets the row headers in the table model.
     *
     * @param tableModel the table model to set the row headers in.
     */
    protected void setRowHeaders(DefaultTableModel tableModel){
        // Set row headers
        for (int i = 0; i < rowSize; i++) { // Loop through the rows
            tableModel.setValueAt(i + 1, i, 0); // Set the row header value
        }
    }

    /**
     * Adds mouse listener for right-click actions.
     *
     * @param rightClickPanel the panel for right-click actions.
     */
    protected void addMouseListener(JPanel rightClickPanel) {
        yourTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // For right-clicks
                    int row = yourTable.rowAtPoint(e.getPoint()); // Get the row at the mouse point
                    int col = yourTable.columnAtPoint(e.getPoint()); // Get the column at the mouse point
                    if (row >= 0 && row < rowSize && col >= 1 && col < colSize) { // Check if the cell is within bounds
                        rightClickPanel.setLocation(e.getX(), e.getY()); // Set the location of the panel
                        rightClickPanel.setVisible(true); // Set the visibility of the panel to true
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) { // For left-clicks
                    if (rightClickPanel.isVisible()) { // Check if the panel is visible
                        rightClickPanel.setVisible(false); // Set the visibility of the panel to false
                    }
                }
            }
        });
    }

    /**
     * Adds key listener for delete actions.
     */
    protected void listenForDelete(){
        // Add key listener for delete and digit keys
        yourTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    System.out.println(
                            e.getKeyCode() == KeyEvent.VK_DELETE ? "Delete key pressed" : "Backspace key pressed"); // Print the key pressed
                    controller.updateSelectedCells(""); // Pass an empty string to clear cells
                } else if (Character.isDigit(e.getKeyChar())) {
                    System.out.println("Digit key pressed: " + e.getKeyChar()); // Print the digit key pressed
                    controller.updateSelectedCells(String.valueOf(e.getKeyChar())); // Update selected cells with the digit key
                }
            }
        });
    }

    /**
     * Adds selection listener for row selection.
     */
    protected void listenForSelectionRow(){
        // Add selection listener for row selection
        yourTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] selectedRows = yourTable.getSelectedRows(); // Get the selected rows
                    int[] selectedColumns = yourTable.getSelectedColumns(); // Get the selected columns
                    controller.setSelectedCells(selectedRows, selectedColumns); // Set the selected cells in the controller
                }
            }
        });
    }

    /**
     * Adds selection listener for column selection.
     */
    protected void listenForSelectionCol(){
        // Add selection listener for column selection
        yourTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] selectedRows = yourTable.getSelectedRows(); // Get the selected rows
                    int[] selectedColumns = yourTable.getSelectedColumns(); // Get the selected columns
                    controller.setSelectedCells(selectedRows, selectedColumns); // Set the selected cells in the controller
                }
            }
        });
    }

    /**
     * Adds table model listener for data changes.
     */
    protected void listenForDataChanges(){
        // Add table model listener for data changes
        yourTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (!isUpdatingTable) {
                    int selRow = e.getFirstRow(); // Get the first row of the change
                    int selCol = e.getColumn(); // Get the column of the change
                    if (selRow != -1 && selCol != -1 && selCol != 0) { // Check if the row and column are valid
                        String val = String.valueOf(yourTable.getValueAt(selRow, selCol)); // Get the value at the cell
                        controller.changeSpreadSheetValueAt(selRow, selCol - 1, val); // Change the spreadsheet value
                    }
                }
            }
        });
    }

    /**
     * Adds the table to a scroll pane.
     */
    protected void addTableToScroll() {
        JScrollPane scrollPane = new JScrollPane(yourTable); // Create a new scroll pane with the table
        scrollPane.setPreferredSize(new Dimension(800, 600)); // Set the preferred size of the scroll pane
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); // Set the horizontal scroll bar policy
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Set the vertical scroll bar policy
        add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center of the layout
        build(); // Call the build method
    }

    /**
     * Creates a table model with the specified data and column names.
     *
     * @param data the data for the table.
     * @param columnNames the column names for the table.
     * @return the created table model.
     */
    public DefaultTableModel createTableModel(Object[][] data, String[] columnNames){
        // Create table model
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Make all columns except the first one editable
            }
        };
        return tableModel; // Return the table model
    }
}
