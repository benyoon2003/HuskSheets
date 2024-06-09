package org.example.view.button;

import org.example.view.SheetView;

public class SavePublisher extends Button{


    public SavePublisher(SheetView view) {
        super("Save");

        this.addActionListener(new ToolbarButtonListener(view));

    }
}
