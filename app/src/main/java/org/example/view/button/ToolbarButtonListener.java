package org.example.view.button;

import org.example.model.Spreadsheet;
import org.example.view.SheetView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * The ToolbarButtonListener class handles toolbar button actions.
 */
public class ToolbarButtonListener implements ActionListener {
    private SheetView view;

    public ToolbarButtonListener(SheetView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Cut")) {
            int selRow = view.yourTable.getSelectedRow();
            int selCol = view.yourTable.getSelectedColumn();
            if (selRow != -1 && selCol != -1 && selCol != 0) {
                view.getController().cutCell(selRow, selCol - 1);
            }
        } else if (command.equals("Copy")) {
            int selRow = view.yourTable.getSelectedRow();
            int selCol = view.yourTable.getSelectedColumn();
            if (selRow != -1 && selCol != -1 && selCol != 0) {
                view.getController().copyCell(selRow, selCol - 1);
            }
        } else if (command.equals("Paste")) {
            int selRow = view.yourTable.getSelectedRow();
            int selCol = view.yourTable.getSelectedColumn();
            if (selRow != -1 && selCol != -1 && selCol != 0) {
                view.getController().pasteCell(selRow, selCol - 1);
            }
        } else if (command.equals("Save")) {
            int option = JOptionPane.showOptionDialog(
                    null,
                    "Choose where to save the sheet:",
                    "Save Option",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[]{"Save Locally", "Save to Server"},
                    "Save Locally");

            if (option == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    this.view.save(selectedFile.getAbsolutePath());
                }
            } else if (option == JOptionPane.NO_OPTION) {
                this.view.getController().saveSheetToServer(this.view.cells,
                        ((Spreadsheet) this.view.cells).getName());
            }
        }
    }
}