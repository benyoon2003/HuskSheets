package org.example.view.button;

import org.example.view.SheetView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * The SaveSubscriber class represents a button that allows saving of a subscribed sheet.
 * It extends the Button class and implements the ISave interface.
 */
public class SaveSubscriber extends Button implements ISave {

    /**
     * Constructs a SaveSubscriber button with the specified SheetView.
     * When the button is clicked, it triggers the handleSave method to handle the save action.
     *
     * @param view the SheetView associated with this button
     */
    public SaveSubscriber(SheetView view) {
        super("Save");

        this.addActionListener(new ActionListener() {
            /**
             * Invoked when the Save button is clicked.
             * Calls the handleSave method to handle the save action.
             *
             * @param e the action event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave(view);
            }
        });
    }

    /**
     * Handles the save action based on user selection (local or subscription update).
     *
     * @param view the SheetView instance associated with this save action
     */
    @Override
    public void handleSave(SheetView view) {
        int option = JOptionPane.showOptionDialog(
                null,
                "Choose where to save the sheet:",
                "Save Option",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Save Locally", "Update Subscription"},
                "Save Locally");

        if (option == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                view.getController().saveSheetLocally(view.cells, selectedFile.getAbsolutePath());
            }
        } else if (option == JOptionPane.NO_OPTION) {
            if (view.getController() == null) {
                System.out.println("Error: Controller is null");
            } else {
                view.getController().updateSubscribedSheet(view.publisher, view.cells, view.cells.getName());
                System.out.println("Publisher: " + view.publisher);
                view.makeVisible();
            }
        }
    }
}
