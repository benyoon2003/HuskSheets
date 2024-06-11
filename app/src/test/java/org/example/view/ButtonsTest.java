package org.example.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.controller.IUserController;
import org.example.controller.UserController;
import org.example.model.*;
import org.example.view.button.Accept;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Set;


public class ButtonsTest {

    private SheetView sheetView;
    private IUserController controller;
    private ISpreadsheet testSpreadSheet;
    private IAppUser user = new AppUser("John", "12345");
    private ILoginView login;
    private Accept acceptButton;

    @BeforeEach
    public void init() {
        this.login = new LoginView();
        this.testSpreadSheet = new Spreadsheet("test");
        this.sheetView = new SheetView(testSpreadSheet);
        this.controller = new UserController(login);
        this.controller.loginUser(this.user.getUsername(), this.user.getPassword());
        this.sheetView.addController(this.controller);
        this.acceptButton = new Accept(this.sheetView);
    }

    @Test
    public void testAccept(){
        System.out.println(this.sheetView);
//        this.acceptButton.doClick();
//        this.controller.openServerSheet(this.testSpreadSheet.getName());
//
//        assertEquals(this.testSpreadSheet.getCellsObject(), this.sheetView.cells.getCellsObject());
    }

}
