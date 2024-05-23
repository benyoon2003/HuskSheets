package org.example.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.example.model.Spreadsheet;
import org.example.view.ISheetView;

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
}
