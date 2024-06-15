package org.example.view.button;

import org.example.view.SheetView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GetUpdatesFromPublisher class represents a button that allows a subscriber
 * to pull updates for a sheet from its publisher.
 * It extends the Button class and is specific to the SubscriberSheetView.
 */
public class GetUpdatesFromPublisher extends Button {

  /**
   * Constructs a button that updates the subscriber's sheet with a version from
   * the publisher when clicked.
   *
   * @param view the SheetView associated with this button
   */
  public GetUpdatesFromPublisher(SheetView view) {
    super("Get Updates");

    this.addActionListener(new ActionListener() {
      /**
       * Invoked when the Get Updates button is clicked.
       * This method disposes of the view and retrieves updates for the subscribed sheet.
       *
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          view.dispose();
          view.getController().getUpdatesForSubscribed(view.cells.getName(),
                  view.cells.getId_version());
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, ex.getMessage());
        }
      }
    });
  }
}
