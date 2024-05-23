package org.example.controller;

import org.example.view.IHomeView;
import org.example.view.ISheetView;
import org.example.model.Spreadsheet;

import java.util.List;

public interface IUserController {
    boolean isUserAuthenticationComplete(String username, String password);

    boolean isUserCreated(String username, String password);

    void setCurrentSheet(ISheetView sheetView);

    void createNewSheet();

    void saveSheet(Spreadsheet sheet, String path);

    void handleToolbar(String command);

    void handleStatsDropdown(String selectedStat);

    void selectedCells(int[] selectedRows, int[] selectedColumns);

    void openSheet(String path);

    List<String> getSavedSheets();

    IHomeView getHomeView(); // Add this method
}
