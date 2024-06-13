//package org.example.view;
//
//import org.example.controller.IUserController;
//import org.example.controller.UserController;
//import org.example.model.AppUser;
//import org.example.model.Cell;
//import org.example.model.IAppUser;
//import org.example.model.ISpreadsheet;
//import org.example.model.Spreadsheet;
//import org.example.view.button.Accept;
//import org.example.view.button.AddConditionalFormat;
//import org.example.view.button.Back;
//import org.example.view.button.Copy;
//import org.example.view.button.Cut;
//import org.example.view.button.Deny;
//import org.example.view.button.Paste;
//import org.example.view.button.SaveSubscirber;
//import org.example.view.button.ZoomI;
//import org.example.view.button.ZoomO;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import javax.swing.*;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.*;
//
//public class ReviewChangesSheetViewTest {
//    private ISpreadsheet current;
//
//    private ISpreadsheet changes;
//
//    private ReviewChangesSheetView view;
//    private IAppUser user = new AppUser("John", "12345");
//
//    private IUserController controller;
//
//    @BeforeEach
//    public void init() {
//        current = new Spreadsheet("Current");
//        changes = new Spreadsheet("Changes");
//        view = new ReviewChangesSheetView(changes, current);
//        controller = new UserController(new LoginView());
//
//        //check if user is registered first.
//        try{
//            this.controller.registerUser(this.user.getUsername(), this.user.getPassword());
//        } catch (Exception e){
//            try {
//                this.controller.loginUser(this.user.getUsername(), this.user.getPassword());
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
//        view.addController(controller);
//    }
//    @Test
//    public void testFormulaTextField() {
//    }
//
//    @Test
//    public void testLoadChanges() {
//    }
//}
