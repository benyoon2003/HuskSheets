package org.example.view.button;

import org.example.view.SheetView;

public class SaveSubscirber extends Button{

    public SaveSubscirber(SheetView view) {
        super("Save");

        this.addActionListener(new ToolbarButtonListener(view));

    }
}
