package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * The HighlightedCellRenderer class customizes the rendering of table cells based on highlighted cells.
 */
public class HighlightedCellRenderer extends AbstractCustomTableCellRenderer {

    private final Map<Point, Color> highlightedCells;

    /**
     * Constructs a HighlightedCellRenderer with the specified highlighted cells.
     *
     * @param highlightedCells the highlighted cells.
     */
    public HighlightedCellRenderer(Map<Point, Color> highlightedCells) {
        this.highlightedCells = highlightedCells;
    }

    @Override
    protected void configureCellRenderer(Component c, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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
    }
}
