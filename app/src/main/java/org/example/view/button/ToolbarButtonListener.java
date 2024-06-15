package org.example.view.button;

import org.example.view.SheetView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The ToolbarButtonListener class handles toolbar button actions in a SheetView.
 * It implements the ActionListener interface to respond to button clicks.
 */
public class ToolbarButtonListener implements ActionListener {
    private SheetView view;

    /**
     * Constructs a ToolbarButtonListener with the specified SheetView.
     *
     * @param view the SheetView associated with this listener
     */
    public ToolbarButtonListener(SheetView view) {
        this.view = view;
    }

    /**
     * Invoked when a toolbar button action is performed.
     *
     * @param e the action event representing the button click
     */
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
