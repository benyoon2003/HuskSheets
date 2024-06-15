package org.example.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * The AbstractCustomTableCellRenderer class provides a base implementation for custom table cell rendering.
 * 
 * @author Vinay
 */
public abstract class AbstractCustomTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); // Call the superclass method to get the default renderer component
        configureCellRenderer(c, table, value, isSelected, hasFocus, row, column); // Call the abstract method to configure the cell renderer with custom logic
        return c; // Return the configured component
    }

    /**
     * Configures the cell renderer with custom logic.
     *
     * @param c the cell renderer component
     * @param table the table
     * @param value the value of the cell
     * @param isSelected whether the cell is selected
     * @param hasFocus whether the cell has focus
     * @param row the row of the cell
     * @param column the column of the cell
     * @author Vinay
     */
    protected abstract void configureCellRenderer(Component c, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column); // Abstract method to be implemented by subclasses to provide custom cell rendering logic
}
