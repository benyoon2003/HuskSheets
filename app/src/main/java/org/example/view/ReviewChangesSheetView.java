package org.example.view;

import org.example.model.Cell;
import org.example.model.ISpreadsheet;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ReviewChangesSheetView extends SheetView {
    private ISpreadsheet current;
    private ISpreadsheet changes;

    public ReviewChangesSheetView(ISpreadsheet changes, ISpreadsheet current) {
        super(current);
        this.current = current;
        this.changes = changes;
    }

    public void makeToolbar() {
        // Create toolbar
        JToolBar toolbar = new JToolBar();
        JButton cutButton = new JButton("Cut");
        JButton copyButton = new JButton("Copy");
        JButton pasteButton = new JButton("Paste");
        JButton saveButton = new JButton("Save");
        JButton zoomInButton = new JButton("Zoom In");
        JButton zoomOutButton = new JButton("Zoom Out");
        JButton accept = new JButton("Accept Changes");
        JButton deny = new JButton("Deny Changes");
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
        toolbar.add(accept);
        toolbar.add(deny);
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

        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Accept changes logic here
            }
        });

        deny.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Deny changes logic here
            }
        });

        add(toolbar, BorderLayout.NORTH);
    }

    public void loadChanges() {
        ArrayList<ArrayList<Cell>> changedCells = this.changes.getCells();
        ArrayList<ArrayList<Cell>> currCells = this.current.getCells();

        for (int i = 0; i < changedCells.size(); i++) {
            for (int j = 0; j < changedCells.get(i).size(); j++) {
                Cell change = changedCells.get(i).get(j);
                Cell curr = currCells.get(i).get(j);

                if (!curr.getRawdata().equals(change.getRawdata())) {
                    controller.changeSpreadSheetValueAt(i, j, change.getRawdata());
                }
            }
        }
        applyCustomCellRenderer();
    }

    private void applyCustomCellRenderer() {
        JTable table = getTable();
        if (table != null) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(new CustomCellRenderer());
            }
        }
    }

    // Custom cell renderer to highlight changed cells
    private class CustomCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Adjust column index to account for row headers
            int modelColumn = table.convertColumnIndexToModel(column);
            if (modelColumn >= 0) {
                Cell currentCell = current.getCells().get(row).get(modelColumn);
                Cell changeCell = changes.getCells().get(row).get(modelColumn);

                if (!currentCell.getRawdata().equals(changeCell.getRawdata())) {
                    cellComponent.setBackground(Color.YELLOW); // Highlight changed cells
                } else {
                    cellComponent.setBackground(Color.WHITE); // Default color for unchanged cells
                }
            }

            return cellComponent;
        }
    }
}
