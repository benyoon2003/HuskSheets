package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddConditionalFormat extends Button{
    public AddConditionalFormat(SheetView view) {
        super("Add Conditional Formatting");
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getController().applyConditionalFormatting();
            }
        });
    }
}
