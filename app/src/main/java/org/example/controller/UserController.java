package org.example.controller;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.model.Spreadsheet;
import org.example.view.HomeView;
import org.example.view.IHomeView;
import org.example.view.ILoginView;
import org.example.view.ISheetView;
import org.example.view.LoginView;
import org.example.view.SheetView;

import javax.swing.*;

public class UserController implements IUserController {

    private ILoginView loginPage;

    private ISheetView sheetView;

    private IHomeView homeView;
    private IAppUser appUser;

    public UserController(ILoginView loginView, IHomeView homeView, IAppUser appUser) {
        this.loginPage = loginView;
        loginPage.addController(this);
        this.appUser = appUser;
        this.homeView = homeView;
        homeView.addController(this);
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
    public void createNewSheet(ISheetView sheetView) {
        this.setCurrentSheet(sheetView);
        this.sheetView.makeVisible();
    }

    @Override
    public void saveSheet(Spreadsheet sheet, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            XMLEncoder encoder = new XMLEncoder(fos);
            encoder.writeObject(sheet);
            encoder.close();
            fos.close();
        } catch (Exception e) {

        }

        return;
    }
  
    @Override
    public void handleToolbar(String command) {
        this.sheetView.displayMessage(command + " button clicked");
    }

    @Override
    public void handleStatsDropdown(String selectedStat) {
        this.sheetView.displayMessage(selectedStat + " selected");
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
            return List.of(startRow + 1, startColumn, endRow+1, endColumn);
        }
    }


    private boolean validateInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }
}
