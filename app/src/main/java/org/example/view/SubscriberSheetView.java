package org.example.view;

import org.example.controller.IUserController;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
import org.example.model.Spreadsheet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SubscriberSheetView extends SheetView{
  final IReadOnlySpreadSheet cells;

  String publisher;

  /**
   * Constructs a SheetView with the given spreadsheet.
   *
   * @param openSheet the spreadsheet to be displayed.
   */
  public SubscriberSheetView(String publisher, ISpreadsheet openSheet) {
    super(openSheet);
    this.publisher = publisher;
    this.cells = openSheet;
  }


  public void makeToolbar(){
    // Create toolbar
    JToolBar toolbar = new JToolBar();
    JButton cutButton = new JButton("Cut");
    JButton copyButton = new JButton("Copy");
    JButton pasteButton = new JButton("Paste");
    JButton saveButton = new JButton("Save");
    JButton zoomInButton = new JButton("Zoom In");
    JButton zoomOutButton = new JButton("Zoom Out");
    backButton = new JButton("Back");
    formulaTextField = new JTextField(20);
    formulaTextField.setEditable(true);
    toolbar.add(new JLabel("Formula:"));
    toolbar.add(formulaTextField);
    formulaTextField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controller.changeSpreadSheetValueAt(controller.getSelectedRowZeroIndex(),
                controller.getSelectedColZeroIndex(), formulaTextField.getText());
      }
    });

    toolbar.add(cutButton);
    toolbar.add(copyButton);
    toolbar.add(pasteButton);
    toolbar.add(zoomInButton);
    toolbar.add(zoomOutButton);
    toolbar.add(saveButton);
    toolbar.add(backButton);

    // Add action listeners for buttons
    cutButton.addActionListener(new ToolbarButtonListener(this));
    copyButton.addActionListener(new ToolbarButtonListener(this));
    pasteButton.addActionListener(new ToolbarButtonListener(this));
    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleSave();
      }
    });
    backButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
        IHomeView homeView = controller.getHomeView();
        homeView.updateSavedSheets(); // Update the dropdown before making it visible
        homeView.makeVisible();
      }
    });


    zoomInButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        zoomTable(1.1); //Zoom in by 10%
      }
    });

    zoomOutButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        zoomTable(0.9); //Zoom out by 10%
      }
    });

    add(toolbar, BorderLayout.NORTH);
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
//        File selectedFile = fileChooser.getSelectedFile();
//        controller.saveSheetToServer(cells, selectedFile.getAbsolutePath());
      }
    } else if (option == JOptionPane.NO_OPTION) {
      if(this.controller == null){
        System.out.println("Cells is null");
      }
      this.controller.updateSubscribedSheet(this.publisher, cells, ((Spreadsheet) cells).getName());
      System.out.println(((Spreadsheet) cells).getName());
      makeVisible();
    }
  }

}
