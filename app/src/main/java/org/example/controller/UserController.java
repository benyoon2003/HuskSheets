package org.example.controller;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import org.example.model.IAppUser;
import org.example.model.IHome;
import org.example.model.ISelectedCells;
import org.example.model.ISpreadsheet;
import org.example.model.ReadOnlySpreadSheet;
import org.example.model.SelectedCells;
import org.example.model.Spreadsheet;
import org.example.view.IHomeView;
import org.example.view.ILoginView;
import org.example.view.ISheetView;
import org.example.view.SheetView;

public class UserController implements IUserController {

    private ILoginView loginPage;
    private ISheetView sheetView;
    private IHomeView homeView;
    private IAppUser appUser;
    private IHome home;

    private ISpreadsheet spreadsheetModel;

    private ISelectedCells selectedCells;

    private String clipboardContent = "";
    private boolean isCutOperation = false;

    public UserController(ILoginView loginView, IHomeView homeView,
            IAppUser appUser, ISpreadsheet spreadsheetModel, IHome home) {
        this.loginPage = loginView;
        loginView.addController(this);
        this.appUser = appUser;
        this.home = home;
        this.homeView = homeView;
        homeView.addController(this);
        this.spreadsheetModel = spreadsheetModel;
    }

    @Override
    public boolean isUserAuthenticationComplete(String username, String password) {
        if (validateInput(username, password)) {
            String message = this.appUser.authenticateUser(username, password);
            this.loginPage.displayErrorBox(message);
            if (message.equals("Login successful!")) {
                this.homeView.makeVisible();
                this.loginPage.disposeLoginPage();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isUserCreatedSuccessfully(String username, String password) {
        if (validateInput(username, password)) {
            String message = this.appUser.createAccount(username, password);
            this.loginPage.displayErrorBox(message);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setCurrentSheet(ISheetView sheetView) {
        this.sheetView = sheetView;
        this.sheetView.addController(this);
    }

    public ISheetView getCurrentSheet() {
        return this.sheetView;
    }

    @Override
    public void createNewSheet() {
        this.spreadsheetModel = new Spreadsheet();
        this.sheetView = new SheetView(this.spreadsheetModel);
        this.setCurrentSheet(sheetView);
        this.sheetView.makeVisible();
    }

    @Override
    public void saveSheet(ReadOnlySpreadSheet sheet, String path) {
        try {
            this.home.saveSheet(sheet, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleToolbar(String command) {
        this.sheetView.displayMessage(command + " button clicked");
    }

    @Override
    public void handleStatsDropdown(String selectedStat) {
        // TODO: Implement statistical calculations if needed
    }

    @Override
    public void selectedCells(int[] selectedRows, int[] selectedColumns) {
        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            int startRow = selectedRows[0];
            int endRow = selectedRows[selectedRows.length - 1];
            int startColumn = selectedColumns[0];
            int endColumn = selectedColumns[selectedColumns.length - 1];

            System.out.println("Selected range: (" + (startRow + 1) + ", " +
                    startColumn + ") to (" + (endRow + 1) + ", " + endColumn + ")");
            // Additional logic for handling cell selection range

            this.selectedCells = new SelectedCells(startRow + 1,
                    endRow + 1, startColumn, endColumn);
        } else {
            this.selectedCells = new SelectedCells(-1,
                    -1, -1, -1);
        }
    }

    @Override
    public void openSheet(String path) {
        try {
            this.spreadsheetModel = this.home.readXML(path);
            this.sheetView = new SheetView(spreadsheetModel);
            this.sheetView.makeVisible();
            this.setCurrentSheet(sheetView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getSavedSheets() {
        List<String> sheets = new ArrayList<>();
        File folder = new File("sheets");
        if (!folder.exists()) {
            folder.mkdirs(); // Ensure the directory exists
        }
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    sheets.add(file.getName());
                }
            }
        }
        System.out.println("Found saved sheets: " + sheets); // Debug statement
        return sheets;
    }

    @Override
    public IHomeView getHomeView() {
        return this.homeView;
    }

    @Override
    public void changeSpreadSheetValueAt(int selRow, int selCol, String val) {
        // this.spreadsheetModel.setCellValue(selRow, selCol, val);
        if (val.startsWith("=")) {
            val = this.spreadsheetModel.evaluateFormula(val);
        }
        this.spreadsheetModel.setCellValue(selRow, selCol, val);
        this.sheetView.updateTable(); // Update the table view after changing the value
    }

    @Override
    public String evaluateFormula(String formula) {
        return this.spreadsheetModel.evaluateFormula(formula);
    }

    @Override
    public void cutCell(int selRow, int selCol) {
        this.clipboardContent = this.spreadsheetModel.getCellValue(selRow, selCol);
        this.spreadsheetModel.setCellValue(selRow, selCol, "");
        this.sheetView.updateTable();
        this.isCutOperation = true;
    }

    @Override
    public void copyCell(int selRow, int selCol) {
        this.clipboardContent = this.spreadsheetModel.getCellValue(selRow, selCol);
        this.isCutOperation = false;
    }

    @Override
    public void pasteCell(int selRow, int selCol) {
        if (!clipboardContent.isEmpty()) {
            this.spreadsheetModel.setCellValue(selRow, selCol, clipboardContent);
            if (isCutOperation) {
                clipboardContent = "";
                isCutOperation = false;
            }
            this.sheetView.updateTable();
        }
    }

    private boolean validateInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }
}
