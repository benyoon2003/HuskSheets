package org.example.view;

import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
import org.example.model.Spreadsheet;
import org.example.view.button.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The SubscriberSheetView class represents a view for a spreadsheet that is subscribed to a publisher's updates.
 */
public class SubscriberSheetView extends SheetView {
    final IReadOnlySpreadSheet cells;
    String publisher;
    private final Map<Point, Color> highlightedCells = new HashMap<>();

    /**
     * Constructs a SubscriberSheetView with the given spreadsheet and publisher.
     *
     * @param publisher the publisher of the spreadsheet.
     * @param openSheet the spreadsheet to be displayed.
     */
    public SubscriberSheetView(String publisher, ISpreadsheet openSheet) {
        super(openSheet);
        this.publisher = publisher;
        this.cells = openSheet;
        setup();
    }

    /**
     * Sets up the view by adding key listeners and selection listeners.
     */
    @Override
    public void setup() {
        super.setup();

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

        yourTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] selectedRows = yourTable.getSelectedRows();
                    int[] selectedColumns = yourTable.getSelectedColumns();
                    controller.setSelectedCells(selectedRows, selectedColumns);
                }
            }
        });

        yourTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] selectedRows = yourTable.getSelectedRows();
                    int[] selectedColumns = yourTable.getSelectedColumns();
                    controller.setSelectedCells(selectedRows, selectedColumns);
                }
            }
        });

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
    }

    /**
     * Creates and sets up the toolbar with various buttons and their action listeners.
     */
    @Override
    public void makeToolbar() {
        formulaTextField = new JTextField(20);
        formulaTextField.setEditable(true);
        this.addComponent(new JLabel("Formula"))
                .addComponent(formulaTextField)
                .addComponent(new Cut(this))
                .addComponent(new Copy(this))
                .addComponent(new Paste(this))
                .addComponent(new ZoomI(this))
                .addComponent(new ZoomO(this))
                .addComponent(new SaveSubscirber(this))
                .addComponent(new AddConditionalFormat(this))
                .addComponent(new Back(this));
        formulaTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeSpreadSheetValueAt(controller.getSelectedRow(),
                        controller.getSelectedCol(), formulaTextField.getText());
            }
        });
        build();
        revalidate();
        repaint();
    }

    /**
     * Handles saving the spreadsheet either locally or by updating the subscription.
     */
    @Override
    public void handleSave() {
        System.out.println("handleSave Method Called");
        int option = JOptionPane.showOptionDialog(
                null,
                "Choose where to save the sheet:",
                "Save Option",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Save Locally", "Update Subscription"},
                "Save Locally");

        if (option == JOptionPane.YES_OPTION) {
            System.out.println("Save Locally Option Selected");
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                System.out.println("File Selected to Save Locally");
                // File selectedFile = fileChooser.getSelectedFile();
                // controller.saveSheetToServer(cells, selectedFile.getAbsolutePath());
            }
        } else if (option == JOptionPane.NO_OPTION) {
            System.out.println("Update Subscription Option Selected");
            if (this.controller == null) {
                System.out.println("Error: Controller is null");
            } else {
                this.controller.updateSubscribedSheet(this.publisher, cells, cells.getName());
                System.out.println("Spreadsheet Name: " + ((Spreadsheet) cells).getName());
                makeVisible();
            }
        }
    }

    /**
     * Highlights a cell with the specified color.
     *
     * @param row   the row of the cell to highlight.
     * @param col   the column of the cell to highlight.
     * @param color the color to highlight the cell with.
     */
    @Override
    public void highlightCell(int row, int col, Color color) {
        if (color.equals(SheetView.GREEN) || color.equals(SheetView.PINK)) {
            System.out.println("Highlight Cell at row: " + row + " col: " + col + " with color: " + color);
        }
        highlightedCells.put(new Point(row, col + 1), color); // Adjusted for correct column indexing
        yourTable.repaint();
    }

    /**
     * Updates the table by re-rendering its contents and applying the custom cell renderer.
     */
    @Override
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
                model.setValueAt(controller.handleReevaluatingCellFormula(row, col, data[row][col]), row, col + 1);
            }
        }
        model.fireTableDataChanged();

        // Re-apply the custom renderer after data changes
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer(highlightedCells));
        
        isUpdatingTable = false;
    }

    /**
     * Custom TableCellRenderer to highlight cells based on specified colors.
     */
    class CustomTableCellRenderer extends DefaultTableCellRenderer {
        private final Map<Point, Color> highlightedCells;

        /**
         * Constructs a CustomTableCellRenderer with the specified highlighted cells.
         *
         * @param highlightedCells a map of cell locations to their highlight colors.
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
                if (highlightColor.equals(Color.GREEN) || highlightColor.equals(Color.PINK)) {
                    System.out.println("Highlighting cell at row: " + row + ", col: " + column + " with color: " + highlightColor);
                }
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
