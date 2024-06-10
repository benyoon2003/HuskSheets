package org.example.view.button;

import org.example.model.ISpreadsheet;
import org.example.view.ISheetView;
import org.example.view.SheetView;

public class Cut extends Button {

    public Cut(SheetView view){
        super("Cut");
        this.addActionListener(new ToolbarButtonListener(view));
    }


}
