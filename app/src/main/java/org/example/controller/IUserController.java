package org.example.controller;

import org.example.model.AppUser;
import org.example.model.ISpreadsheet;
import org.example.model.ReadOnlySpreadSheet;
import org.example.model.SelectedCells;
import org.example.view.IHomeView;
import org.example.view.ILoginView;
import org.example.view.ISheetView;

import java.util.List;

public interface IUserController {
    boolean isUserAuthenticationComplete(String username, String password);

    boolean isUserCreatedSuccessfully(String username, String password);

    void setCurrentSheet(ISheetView sheetView);

    ISheetView getCurrentSheet();

    void createNewSheet();

    void saveSheet(ReadOnlySpreadSheet sheet, String path);

    void handleToolbar(String command);

    void handleStatsDropdown(String selectedStat);

    void selectedCells(int[] selectedRows, int[] selectedColumns);

    void openSheet(String path);

    List<String> getSavedSheets();

    void deleteSheet(String path);

    IHomeView getHomeView();

    void changeSpreadSheetValueAt(int selRow, int selCol, String val);

    String evaluateFormula(String formula);

    void cutCell(int selRow, int selCol);

    void copyCell(int selRow, int selCol);

    void pasteCell(int selRow, int selCol);

    void saveSheetToServer(ReadOnlySpreadSheet sheet, String name);

    void deleteSheetFromServer(String name);

    String handleReferencingCell(int row, int col, String data);
}
