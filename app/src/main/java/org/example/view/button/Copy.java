package org.example.view.button;

import org.example.view.SheetView;

public class Copy extends Button {

    public Copy(SheetView view) {
        super("copy");
        this.addActionListener(new ToolbarButtonListener(view));
    }
}
