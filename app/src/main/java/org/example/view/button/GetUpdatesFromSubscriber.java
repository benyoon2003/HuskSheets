package org.example.view.button;

import org.example.view.SheetView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GetUpdatesFromSubscriber class represents a button that allows a publisher
 * to pull updates for their sheet from its subscriber.
 * It extends the Button class and is associated with a SheetView.
 */
public class GetUpdatesFromSubscriber extends Button {

    /**
     * Constructs a button that allows the publisher to get updates from the subscriber
     * when clicked.
     *
     * @param view the SheetView associated with this button
     */
    public GetUpdatesFromSubscriber(SheetView view) {
        super("Get Updates");

        this.addActionListener(new ActionListener() {
            /**
             * Invoked when the Get Updates button is clicked.
             * This method disposes of the view and retrieves updates for the published sheet.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    view.dispose();
                    view.getController().getUpdatesForPublished(view.cells.getName(), view.cells.getId_version());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
    }
}
