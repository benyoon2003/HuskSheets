package org.example.view;

import org.example.model.Cell;
import org.example.model.ISpreadsheet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The ReviewChangesRenderer class customizes the rendering of table cells for review changes view.
 */
public class ReviewChangesRenderer extends AbstractCustomTableCellRenderer {

    private final ArrayList<ArrayList<Cell>> originalCells;
    private final ISpreadsheet changes;

    /**
     * Constructs a ReviewChangesRenderer with the specified original and changed cells.
     *
     * @param originalCells the original cells
     * @param changes the changes to be reviewed
     */
    public ReviewChangesRenderer(ArrayList<ArrayList<Cell>> originalCells, ISpreadsheet changes) {
        this.originalCells = originalCells; // Initialize original cells
        this.changes = changes; // Initialize changes
    }

    @Override
    protected void configureCellRenderer(Component c, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column > 0) { // Skip the row header
            int modelColumn = column - 1; // Adjust for row header
            Cell currentCell = originalCells.get(row).get(modelColumn); // Get the current cell from original cells
            Cell changeCell = changes.getCells().get(row).get(modelColumn); // Get the corresponding cell from changes


            if (!currentCell.getRawdata().equals(changeCell.getRawdata())) { // Check if the cell has changed
                c.setBackground(Color.YELLOW); // Highlight changed cells
            } else {
                c.setBackground(Color.WHITE); // Default color for unchanged cells
            }
        }

        if (isSelected) { // Check if the cell is selected
            c.setBackground(Color.CYAN); // Highlight selected cells
        }
    }
}
