package org.example.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileOutputStream;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.model.Spreadsheet;
import org.example.view.ISheetView;
import org.example.view.SheetView;

// Tests for the UserController class
public class UserControllerTest {
    private MockUserController controller;

    @BeforeEach
    public void init() {
        controller = new MockUserController();
    }

    @Test
    public void testIsUserAuthenticationComplete() {
        String username = "user", password = "password";
        assertTrue(this.controller.isUserAuthenticationComplete(username, password));
    }

    @Test
    public void testIsUserAuthenticationCompleteEmptyUsername() {
        String username = "", password = "password";
        assertFalse(this.controller.isUserAuthenticationComplete(username, password));
    }

    @Test
    public void testIsUserAuthenticationCompleteEmptyPassword() {
        String username = "user", password = "";
        assertFalse(this.controller.isUserAuthenticationComplete(username, password));
    }

    @Test
    public void testIsUserCreated() {
        String username = "user", password = "password";
        assertTrue(this.controller.isUserCreated(username, password));
    }

    @Test
    public void testIsUserCreatedEmptyUsername() {
        String username = "", password = "password";
        assertFalse(this.controller.isUserCreated(username, password));
    }

    @Test
    public void testIsUserCreatedEmptyPassword() {
        String username = "user", password = "";
        assertFalse(this.controller.isUserCreated(username, password));
    }

    @Test
    public void testCreateNewSheet() {
        this.controller.createNewSheet();
        ISheetView sheetView = this.controller.getSheetView();

        assertNotNull(sheetView);
    }

    @Test
    public void testSaveSheet() {
        Spreadsheet sheet = new Spreadsheet();
        String path = "./test.xml";

        this.controller.saveSheet(sheet, path);

        File f = new File(path);
        assertTrue(f.exists());

        f.delete();
    }

    // A mock version of the UserController class used for testing
    private class MockUserController extends UserController {
        private IAppUser appUser;
        private ISheetView sheetView;

        public MockUserController() {
            this.appUser = new AppUser();
            this.sheetView = new SheetView();
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
            throw new UnsupportedOperationException("Unimplemented method 'setCurrentSheet'");
        }

        @Override
        public void createNewSheet() {
            this.sheetView = new SheetView();
            this.sheetView.addController(this);
        }

        private ISheetView getSheetView() {
            return this.sheetView;
        }

        @Override
        public void saveSheet(Spreadsheet sheet, String path) {
            throw new UnsupportedOperationException("Unimplemented method 'saveSheet'");
        }

        @Override
        public void handleToolbar(String command) {
            throw new UnsupportedOperationException("Unimplemented method 'handleToolbar'");
        }

        @Override
        public void handleStatsDropdown(String selectedStat) {
            throw new UnsupportedOperationException("Unimplemented method 'handleStatsDropdown'");
        }

        @Override
        public void selectedCells(int[] selectedRows, int[] selectedColumns) {
            throw new UnsupportedOperationException("Unimplemented method 'selectedCells'");
        }
    }
}
