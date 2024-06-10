package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Deny extends Button {

    public Deny(SheetView view) {
        super("Deny");
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Deny changes logic here
                view.dispose();
                view.getController().openServerSheet(view.cells.getName());
            }
        });
    }
}
