package org.example.view.button;

import org.example.view.SheetView;

public class Paste extends Button {
    public Paste(SheetView view) {
        super("Paste");
        this.addActionListener(new ToolbarButtonListener(view));
    }
}
