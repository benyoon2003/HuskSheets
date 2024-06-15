package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Deny class represents a button that denies changes and performs associated actions.
 * It extends the Button class and is associated with a SheetView.
 */
public class Deny extends Button {

    /**
     * Constructs a Deny button with the specified SheetView.
     * When the button is clicked, it triggers the actionPerformed method to deny changes.
     *
     * @param view the SheetView that the button is associated with
     */
    public Deny(SheetView view) {
        super("Deny");
        this.addActionListener(new ActionListener() {
            /**
             * Invoked when the Deny button is clicked.
             * This method disposes of the view and reopens the sheet from the server.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Deny changes logic here
                view.dispose();
                view.getController().openServerSheet(view.cells.getName());
            }
        });
    }
}
