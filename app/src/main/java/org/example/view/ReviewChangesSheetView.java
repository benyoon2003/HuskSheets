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
    public ReviewChangesSheetView(ISpreadsheet changes, ISpreadsheet current) {
        super(current);
        this.current = current;
        this.changes = changes;
        originalCells = new ArrayList<>();
        applyCustomCellRenderer(); // Apply the renderer in the constructor
    }

    public void makeToolbar() {
        // Create toolbar
        formulaTextField = new JTextField(20);
        formulaTextField.setEditable(true);
        this.addComponent(new JLabel("Formula"))
                .addComponent(formulaTextField)
                .addComponent(new Cut(this))
                .addComponent(new Copy(this))
                .addComponent(new Paste(this))
                .addComponent(new Accept(this))
                .addComponent(new Deny(this))
                .addComponent(new ZoomI(this))
                .addComponent(new ZoomO(this))
                .addComponent(new SaveSubscirber(this))
                .addComponent(new AddConditionalFormat(this))
                .addComponent(new Back(this));
        formulaTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeSpreadSheetValueAt(controller.getSelectedStartRow(),
                        controller.getSelectedStartCol(), formulaTextField.getText());
            }
        });
    }

    public void loadChanges() throws Exception {
        java.util.List<java.util.List<Cell>> changedCells = this.changes.getCells();
        java.util.List<java.util.List<Cell>> currCells = this.current.getCells();

        boolean changed = false;
        for (int i = 0; i < changedCells.size(); i++) {
            ArrayList<Cell> addRow = new ArrayList<>();
            for (int j = 0; j < changedCells.get(i).size(); j++) {
                Cell change = changedCells.get(i).get(j);
                Cell curr = currCells.get(i).get(j);

                Cell addCell = new Cell();
                addCell.setRawData(curr.getRawdata());
                addCell.setValue(curr.getValue());
                addRow.add(addCell);
                if (!curr.getRawdata().equals(change.getRawdata())) {
                    changed = true;
                    controller.changeSpreadSheetValueAt(i, j, change.getRawdata());
                }
            }
            originalCells.add(addRow);
        }

        if(!changed){
            dispose();
            controller.openServerSheet(current.getName());
            throw new Exception("No changes found");
        }

        applyCustomCellRenderer();
        getTable().repaint(); // Refresh table to ensure changes are reflected
    }

    private void applyCustomCellRenderer() {
        JTable table = getTable();
        if (table != null) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(new ReviewChangesRenderer(originalCells, changes));
            }
        }
    }
}
