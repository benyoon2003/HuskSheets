package org.example.controller;

import org.example.model.AppUser;
import org.example.model.SelectedCells;
import org.example.model.Spreadsheet;
import org.example.view.ILoginView;
import org.example.view.ISheetView;
import org.example.view.SheetView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserController {

  boolean isUserAuthenticationComplete(String username, String password);

  boolean isUserCreatedSuccessfully(String username, String password);

  void setCurrentSheet(ISheetView sheetView);
  ISheetView getCurrentSheet();

  void createNewSheet(ISheetView sheetView);

  void saveSheet(Spreadsheet sheet, String path);
  
  void handleToolbar(String command);

  void handleStatsDropdown(String selectedStat);

  SelectedCells selectedCells(int[] selectedRows, int[] selectedColumns);
}
