package org.example.view.button;

import org.example.model.Spreadsheet;
import org.example.view.SheetView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SaveSubscirber extends Button implements ISave{

    public SaveSubscirber(SheetView view) {
        super("Save");
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave(view);
            }
        });
    }

    public void handleSave(SheetView view){
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
                view.save(selectedFile.getAbsolutePath());
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
