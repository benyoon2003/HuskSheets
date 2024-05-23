package org.example.controller;


import org.example.model.AppUser;
import org.example.model.ISpreadsheet;
import org.example.model.ReadOnlySpreadSheet;
import org.example.model.SelectedCells;
import org.example.model.Spreadsheet;
import org.example.view.IHomeView;
import org.example.view.ILoginView;
import org.example.view.ISheetView;
import org.example.model.Spreadsheet;

import java.util.List;

public interface IUserController {
    boolean isUserAuthenticationComplete(String username, String password);

    void saveSheet(ReadOnlySpreadSheet sheet, String path);
  boolean isUserCreatedSuccessfully(String username, String password);

  void setCurrentSheet(ISheetView sheetView);
  ISheetView getCurrentSheet();

  void createNewSheet();

    void handleToolbar(String command);

    void handleStatsDropdown(String selectedStat);

    void selectedCells(int[] selectedRows, int[] selectedColumns);

    void openSheet(String path);

    List<String> getSavedSheets();

    IHomeView getHomeView(); // Add this method

  void changeSpreadSheetValueAt(int selRow, int selCol, String val);
}
