package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ZoomI extends Button {
    public ZoomI(SheetView view) {
        super("Zoom In");
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.zoomTable(1.1);
            }
        });
    }
}
