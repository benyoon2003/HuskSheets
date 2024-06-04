package org.example.view;

import org.example.controller.IUserController;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
import org.example.model.Spreadsheet;

import javax.swing.*;
import java.io.File;

public class SubscriberSheetView extends SheetView{
  private IUserController controller;
  private JButton backButton;
  JTable yourTable;
  private boolean isUpdatingTable = false;
  private JTextField formulaTextField;

  private static final int rowSize = 100;
  private static final int colSize = 100;
  String publisher;

  /**
   * Constructs a SheetView with the given spreadsheet.
   *
   * @param openSheet the spreadsheet to be displayed.
   */
  public SubscriberSheetView(String publisher, ISpreadsheet openSheet) {
    super(openSheet);
    this.publisher = publisher;
  }


  public void handleSave(){
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
        controller.saveSheetToServer(cells, selectedFile.getAbsolutePath());
      }
    } else if (option == JOptionPane.NO_OPTION) {
      controller.updateSubscribedSheet(this.publisher, cells, ((Spreadsheet) cells).getName());
      System.out.println(((Spreadsheet) cells).getName());
      makeVisible();
    }
  }

}
