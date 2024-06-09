package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ZoomO extends Button {
    public ZoomO(SheetView view) {
        super("Zoom Out");
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.zoomTable(0.9);
            }
        });
    }
}
