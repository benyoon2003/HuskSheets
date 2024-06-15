package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The ZoomO class represents a button that allows zooming out on a table view.
 * It extends the Button class.
 */
public class ZoomO extends Button {

    /**
     * Constructs a ZoomO button with the specified SheetView.
     * When the button is clicked, it zooms out on the associated SheetView's table.
     *
     * @param view the SheetView associated with this button
     */
    public ZoomO(SheetView view) {
        super("Zoom Out");

        this.addActionListener(new ActionListener() {
            /**
             * Invoked when the Zoom Out button is clicked.
             * Calls the zoomTable method of the associated SheetView to zoom out.
             *
             * @param e the action event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                view.zoomTable(0.9); // Zoom out by a factor of 0.9
            }
        });
    }
}
