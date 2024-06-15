package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The AddConditionalFormat class represents a button that adds conditional formatting to a sheet.
 * It extends the Button class and is associated with a SheetView.
 */
public class AddConditionalFormat extends Button {

    /**
     * Constructs an AddConditionalFormat button with the specified SheetView.
     * When the button is clicked, it triggers the actionPerformed method to add conditional formatting.
     *
     * @param view the SheetView that the button is associated with
     */
    public AddConditionalFormat(SheetView view) {
        super("Add Conditional Formatting");
        this.addActionListener(new ActionListener() {
            /**
             * Invoked when the Add Conditional Formatting button is clicked.
             * This method calls the applyConditionalFormatting method on the controller.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getController().applyConditionalFormatting();
            }
        });
    }
}
