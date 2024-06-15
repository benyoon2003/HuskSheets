package org.example.view.button;

import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The ZoomI class represents a button that allows zooming in on a table view.
 * It extends the Button class.
 */
public class ZoomI extends Button {

    /**
     * Constructs a ZoomI button with the specified SheetView.
     * When the button is clicked, it zooms in on the associated SheetView's table.
     *
     * @param view the SheetView associated with this button
     */
    public ZoomI(SheetView view) {
        super("Zoom In");

        this.addActionListener(new ActionListener() {
            /**
             * Invoked when the Zoom In button is clicked.
             * Calls the zoomTable method of the associated SheetView to zoom in.
             *
             * @param e the action event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                view.zoomTable(1.1); // Zoom in by a factor of 1.1
            }
        });
    }
}
