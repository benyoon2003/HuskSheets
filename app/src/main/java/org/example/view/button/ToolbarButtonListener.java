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
        }
    }
}