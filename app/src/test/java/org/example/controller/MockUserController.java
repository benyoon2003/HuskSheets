package org.example.controller;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.view.ISheetView;
import org.example.view.MockSheetView;

// A mock version of the UserController class used for testing
public class MockUserController extends UserController {
    private IAppUser appUser;
    private ISheetView sheetView;

    public MockUserController() {
        this.appUser = new AppUser();
        this.sheetView = new MockSheetView();
    }

    @Override
    public boolean isUserAuthenticationComplete(String username, String password) {
        if (validateInput(username, password)) {
            String result = this.appUser.authenticateUser(username, password);
            return result != null;
        }
        return false;
    }

    @Override
    public boolean isUserCreated(String username, String password) {
        if (validateInput(username, password)) {
            String result = this.appUser.createAccount(username, password);
            return result != null;
        }

        return false;
    }

    @Override
    public void setCurrentSheet(ISheetView sheetView) {
        return;
    }

    @Override
    public void createNewSheet() {
        this.sheetView = new MockSheetView();
        this.sheetView.addController(this);
    }

    @Override
    public void handleToolbar(String command) {
        return;
    }

    @Override
    public void handleStatsDropdown(String selectedStat) {
        return;
    }

    @Override
    public void selectedCells(int[] selectedRows, int[] selectedColumns) {
        return;
    }

    ISheetView getSheetView() {
        return this.sheetView;
    }
}