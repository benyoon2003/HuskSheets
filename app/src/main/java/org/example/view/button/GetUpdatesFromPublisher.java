package org.example.view.button;

import org.example.view.SheetView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GetUpdatesFromPublisher is used for the toolbar and is specific to the SubscriberSheetView.
 * It allows the subscriber of a sheet to pull a version from the publisher of that sheet.
 */
public class GetUpdatesFromPublisher extends Button{

  /**
   * Constructs a button that updates the subscriber's sheet with a version from
   * the publisher on a button press.
   * @param view a ISheetView
   * @Author Ben
   */
  public GetUpdatesFromPublisher(SheetView view) {
    super("Get Updates");

    this.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          view.dispose();
          view.getController().getUpdatesForSubscribed(view.cells.getName(),
                  view.cells.getId_version());
        } catch (Exception j) {
          JOptionPane.showMessageDialog(null, j.getMessage());
        }
      }
    });
  }
}