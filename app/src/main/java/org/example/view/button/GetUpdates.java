package org.example.view.button;

import org.example.view.SheetView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GetUpdates extends Button{
    public GetUpdates(SheetView view) {
        super("Get Updates");

        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    view.dispose();
                    view.getController().getUpdatesForPublished(view.cells.getName(), view.cells.getId_version());
                } catch (Exception j) {
                    JOptionPane.showMessageDialog(null, j.getMessage());
                }
            }
        });
    }
}
