package org.example.controller;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.List;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.model.ISelectedCells;
import org.example.model.ISpreadsheet;
import org.example.model.ReadOnlySpreadSheet;
import org.example.model.SelectedCells;
import org.example.model.Spreadsheet;
import org.example.view.IHomeView;
import org.example.view.ILoginView;
import org.example.view.ISheetView;
import org.example.view.LoginView;
import org.example.view.SheetView;

public class UserController implements IUserController {

    private ILoginView loginPage;
    private ISheetView sheetView;
    private IHomeView homeView;
    private IAppUser appUser;

    private ISpreadsheet spreadsheetModel;

    private ISelectedCells selectedCells;

    public UserController(ILoginView loginView, IHomeView homeView,
                          IAppUser appUser, ISpreadsheet spreadsheetModel) {
        this.loginPage = loginView;
        loginPage.addController(this);
        this.appUser = appUser;
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
            if (!path.endsWith(".xml")) {
                path += ".xml"; // Ensure the file has a .xml extension
            }
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            XMLEncoder encoder = new XMLEncoder(fos);
            encoder.writeObject(sheet);
            encoder.close();
            fos.close();
            System.out.println("Saved sheet to path: " + file.getAbsolutePath()); // Debug statement
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
//        //TODO: Need to create extra row or column if entirety is selected
//        if (selectedCells.getStartRow() != -1) {
//            switch (selectedStat) {
//                case "Median":
//                    this.spreadsheetModel.performMedianCalc(selectedCells);
//                    break;
//                case "Mean":
//                    this.spreadsheetModel.performMeanCalc(selectedCells);
//                    break;
//                case "Mode":
//                    this.spreadsheetModel.performModeCalc(selectedCells);
//                    break;
//                default:
//                    break;
//            }
//        }
//        else {
//            sheetView.displayMessage("Select cells to perform" +
//                    "statistical calculations");
//        }
    }

    @Override
    public void selectedCells(int[] selectedRows, int[] selectedColumns) {
        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            int startRow = selectedRows[0];
            int endRow = selectedRows[selectedRows.length - 1];
            int startColumn = selectedColumns[0];
            int endColumn = selectedColumns[selectedColumns.length - 1];

            System.out.println("Selected range: (" + (startRow+1) + ", " +
                    startColumn + ") to (" + (endRow+1)+ ", " + endColumn + ")");
            // Additional logic for handling cell selection range

            this.selectedCells = new SelectedCells(startRow+1,
                    endRow+1, startColumn, endColumn);
        }
        else {
            this.selectedCells = new SelectedCells(-1,
                    -1, -1, -1);
        }
    }

    @Override
    public void openSheet(String path) {
        try {
            if (!path.endsWith(".xml")) {
                path += ".xml"; // Ensure the file has a .xml extension
            }
            File file = new File(path);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                XMLDecoder decoder = new XMLDecoder(fis);
                Spreadsheet sheet = (Spreadsheet) decoder.readObject();
                decoder.close();
                fis.close();
    
                this.sheetView = new SheetView(sheet);
                this.sheetView.addController(this);
                this.sheetView.makeVisible();
                this.homeView.disposeHomePage();
                System.out.println("Opened sheet from path: " + file.getAbsolutePath()); // Debug statement
            } else {
                System.out.println("File not found: " + file.getAbsolutePath()); // Debug statement
            }
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
        this.spreadsheetModel.getCellsObject()[selRow][selCol].setValue(val);

    }

    private boolean validateInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }
}
