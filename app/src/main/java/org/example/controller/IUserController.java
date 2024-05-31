package org.example.controller;

import org.example.model.IReadOnlySpreadSheet;
import org.example.model.IReadOnlySpreadSheet;
import org.example.view.IHomeView;
import org.example.view.ISheetView;

import java.util.List;

public interface IUserController {
    boolean isUserAuthenticationComplete(String username, String password);

    boolean isUserCreatedSuccessfully(String username, String password);

    void setCurrentSheet(ISheetView sheetView);

    ISheetView getCurrentSheet();

    void createNewSheet(String name);

    void saveSheet(IReadOnlySpreadSheet sheet, String path);

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

    void getPercentile(int selRow, int selCol);

    void saveSheetToServer(IReadOnlySpreadSheet sheet, String name);

    void deleteSheetFromServer(String name);

    String handleReferencingCell(int row, int col, String data);    
    
    String getFormula(int row, int col); // New method declaration


    List<String> getServerSheets();

    void openServerSheet(String selectedSheet);

    int getSelectedRowZeroIndex();

    int getSelectedColZeroIndex();

}
