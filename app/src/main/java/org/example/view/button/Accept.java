package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Accept extends Button {
    public Accept(SheetView view) {
        super("Accept");
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Accept changes logic here
                view.dispose();
                view.getController().saveSheetToServer(view.cells, ( view.cells).getName());
                view.getController().openServerSheet(view.cells.getName());
            }
        });
    }
}
