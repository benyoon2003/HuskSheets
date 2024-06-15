package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Accept class represents a button that accepts changes after getting an update.
 * It extends the Button class and is associated with a SheetView.
 */
public class Accept extends Button {

    /**
     * Constructs an Accept button with the specified SheetView.
     * When the button is clicked, it triggers the actionPerformed method to accept changes.
     *
     * @param view the SheetView that the button is associated with
     */
    public Accept(SheetView view) {
        super("Accept");
        this.addActionListener(new ActionListener() {
            /**
             * Invoked when the Accept button is clicked.
             * This method disposes of the view, saves the sheet to the server,
             * and then reopens the sheet from the server.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Accept changes logic here
                view.dispose();
                view.getController().saveSheetToServer(view.cells, view.cells.getName());
                view.getController().openServerSheet(view.cells.getName());
            }
        });
    }
}
