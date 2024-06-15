package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * The HighlightedCellRenderer class customizes the rendering of table cells based on highlighted cells.
 * 
 * @author Vinay
 */
public class HighlightedCellRenderer extends AbstractCustomTableCellRenderer {

    private final Map<Point, Color> highlightedCells; // Map to store highlighted cells and their corresponding colors

    /**
     * Constructs a HighlightedCellRenderer with the specified highlighted cells.
     *
     * @param highlightedCells the highlighted cells.
     * @author Vinay
     */
    public HighlightedCellRenderer(Map<Point, Color> highlightedCells) {
        this.highlightedCells = highlightedCells; // Initialize the highlightedCells map
    }

    /**
     * Configured the Cell renderer and sets the background color of a cell
     * 
     * @author Vinay
     */
    @Override
    protected void configureCellRenderer(Component c, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Point cellLocation = new Point(row, column); // Create a Point object to represent the cell's location
        Color highlightColor = highlightedCells.get(cellLocation); // Get the highlight color for the cell's location
        if (highlightColor != null) {
            c.setBackground(highlightColor); // Set the background color of the cell to the highlight color
        } else {
            c.setBackground(Color.WHITE); // Set the background color of the cell to white if no highlight color is found
        }
        if (isSelected) {
            c.setBackground(Color.CYAN); // Set the background color to cyan if the cell is selected
        }
    }
}
