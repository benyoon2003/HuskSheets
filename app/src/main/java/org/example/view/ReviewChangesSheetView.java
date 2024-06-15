package org.example.view;

import org.example.model.Cell;
import org.example.model.ISpreadsheet;
import org.example.view.button.*;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ReviewChangesSheetView extends SheetView {
    private ISpreadsheet current;
    private ISpreadsheet changes;
    private ArrayList<ArrayList<Cell>> originalCells;

    /**
     * Constructor for ReviewChangesSheetView.
     *
     * @param changes the changes to be reviewed
     * @param current the current spreadsheet
     * @author Tony
     */
    public ReviewChangesSheetView(ISpreadsheet changes, ISpreadsheet current) {
        super(current);
        this.current = current;
        this.changes = changes;
        originalCells = new ArrayList<>();
        applyCustomCellRenderer(); // Apply the renderer in the constructor
    }

    /**
     * Creates the toolbar with various components and buttons.
     * 
     * @author Vinay
     */
    public void makeToolbar() {
        // Create toolbar
        formulaTextField = new JTextField(20); // Create a text field for formulas
        formulaTextField.setEditable(true); // Make the text field editable
        this.addComponent(new JLabel("Formula")) // Add a label for the formula text field
                .addComponent(formulaTextField) // Add the formula text field to the toolbar
                .addComponent(new Cut(this)) // Add Cut button to the toolbar
                .addComponent(new Copy(this)) // Add Copy button to the toolbar
                .addComponent(new Paste(this)) // Add Paste button to the toolbar
                .addComponent(new Accept(this)) // Add Accept button to the toolbar
                .addComponent(new Deny(this)) // Add Deny button to the toolbar
                .addComponent(new ZoomI(this)) // Add Zoom In button to the toolbar
                .addComponent(new ZoomO(this)) // Add Zoom Out button to the toolbar
                .addComponent(new saveSubscriber(this)) // Add Save Subscriber button to the toolbar
                .addComponent(new AddConditionalFormat(this)) // Add Add Conditional Format button to the toolbar
                .addComponent(new Back(this));  // Add Back button to the toolbar
        formulaTextField.addActionListener(new ActionListener() { // Add action listener to the formula text field
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeSpreadSheetValueAt(controller.getSelectedStartRow(),
                        controller.getSelectedStartCol(), formulaTextField.getText()); // Change the spreadsheet value when action is performed
            }
        });
    }

    /**
     * Loads the changes and updates the current spreadsheet accordingly.
     *
     * @throws Exception if no changes are found
     * @author Tony
     */
    public void loadChanges() throws Exception {
        java.util.List<java.util.List<Cell>> changedCells = this.changes.getCells(); // Get the changed cells
        java.util.List<java.util.List<Cell>> currCells = this.current.getCells(); // Get the current cells

        boolean changed = false; // Flag to track if any changes are found
        for (int i = 0; i < changedCells.size(); i++) { // Iterate through the rows
            ArrayList<Cell> addRow = new ArrayList<>(); // Create a new row for original cells
            for (int j = 0; j < changedCells.get(i).size(); j++) { // Iterate through the columns
                Cell change = changedCells.get(i).get(j); // Get the changed cell
                Cell curr = currCells.get(i).get(j); // Get the current cell

                Cell addCell = new Cell(); // Create a new cell for original data
                addCell.setRawData(curr.getRawdata()); // Set raw data from current cell
                addCell.setValue(curr.getValue()); // Set value from current cell
                addRow.add(addCell); // Add the cell to the row
                if (!curr.getRawdata().equals(change.getRawdata())) { // Check if the cell data has changed
                    changed = true; // Set the flag to true if a change is found
                    controller.changeSpreadSheetValueAt(i, j, change.getRawdata()); // Update the spreadsheet with the change
                }
            }
            originalCells.add(addRow); // Add the row to the list of original cells
        }

        if(!changed){
            dispose(); // Dispose the view if no changes are found
            controller.openServerSheet(current.getName()); // Reopen the current sheet
            throw new Exception("No changes found"); // Throw an exception indicating no changes
        }

        applyCustomCellRenderer(); // Apply the custom cell renderer
        getTable().repaint(); // Refresh table to ensure changes are reflected
    }

    /**
     * Applies the custom cell renderer to the table.
     * 
     * @author Vinay
     */
    private void applyCustomCellRenderer() {
        JTable table = getTable(); // Get the table
        if (table != null) {
            for (int i = 0; i < table.getColumnCount(); i++) { // Iterate through the columns
                table.getColumnModel().getColumn(i).setCellRenderer(new ReviewChangesRenderer(originalCells, changes)); // Set the custom cell renderer for each column
            }
        }
    }
}
