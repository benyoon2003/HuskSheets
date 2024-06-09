package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RightClickButtonListener implements ActionListener {
    private SheetView view;

    RightClickButtonListener(SheetView view) {
        this.view = view;
    }

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

