package org.example.view;

import org.example.model.Cell;
import org.example.model.ISpreadsheet;
import org.example.model.Spreadsheet;
import org.example.model.Cell;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
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
        toolbar.add(zoomInButton);
        toolbar.add(zoomOutButton);
        toolbar.add(saveButton);
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
                handleSave();
                controller.openServerSheet(current.getName());
            }
        });

        deny.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Deny changes logic here
                dispose();
                controller.openServerSheet(current.getName());
            }
        });

        add(toolbar, BorderLayout.NORTH);
    }

    public void handleSave(){
        dispose();
        controller.saveSheetToServer(cells, ((Spreadsheet) cells).getName());
        System.out.println(((Spreadsheet) cells).getName());
        makeVisible();
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
                table.getColumnModel().getColumn(i).setCellRenderer(new CustomCellRenderer());
            }
        }
    }

    private class CustomCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column > 0) { // Skip the row header
                int modelColumn = column - 1; // Adjust for row header
                Cell currentCell = originalCells.get(row).get(modelColumn);
                Cell changeCell = changes.getCells().get(row).get(modelColumn);

                System.out.print("Current: " + currentCell.getRawdata());
                System.out.println(",       Change: " + changeCell.getRawdata());
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
