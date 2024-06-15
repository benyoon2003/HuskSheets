package org.example.view.button;

import org.example.view.SheetView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * The SavePublisher class represents a button that allows saving of a sheet by a publisher.
 * It extends the Button class and implements the ISave interface.
 */
public class SavePublisher extends Button implements ISave {

    /**
     * Constructs a SavePublisher button with the specified SheetView.
     * When the button is clicked, it triggers the handleSave method to handle the save action.
     *
     * @param view the SheetView associated with this button
     */
    public SavePublisher(SheetView view) {
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
     * Handles the save action based on user selection (local or server).
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
                new Object[]{"Save Locally", "Save to Server"},
                "Save Locally");

        if (option == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                view.getController().saveSheetLocally(view.cells, selectedFile.getAbsolutePath());
            }
        } else if (option == JOptionPane.NO_OPTION) {
            view.getController().saveSheetToServer(view.cells, view.cells.getName());
            view.makeVisible();
        }
    }
}
