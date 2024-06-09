package org.example.view;

import org.example.controller.IUserController;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
import org.example.model.SelectedCells;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

/**
 * The SheetViewFactory class is responsible for creating instances of different types of SheetViews.
 */
public abstract class SheetViewFactory<T extends SheetViewFactory> extends JFrame {

  protected JToolBar toolBar;

  public SheetViewFactory(){
    this.toolBar = new JToolBar();
  }
  protected abstract T returnView();

  protected abstract SheetView build();

  protected T addButton(JButton button){

  }

}
