package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The RightClickButtonListener class handles right-click button actions in a SheetView.
 * It implements the ActionListener interface to respond to button clicks.
 */
public class RightClickButtonListener implements ActionListener {
    private SheetView view;

    /**
     * Constructs a RightClickButtonListener with the specified SheetView.
     *
     * @param view the SheetView associated with this listener
     */
    public RightClickButtonListener(SheetView view) {
        this.view = view;
    }

    /**
     * Invoked when a right-click button action is performed.
     *
     * @param e the action event representing the button click
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        int row = this.view.yourTable.getSelectedRow();
        int col = this.view.yourTable.getSelectedColumn() - 1;

        if (command.equals("Percentile")) {
            this.view.getController().getPercentile(row, col);
        }

        this.view.updateTable();
    }
}
