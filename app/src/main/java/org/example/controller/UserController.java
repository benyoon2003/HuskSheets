package org.example.controller;

import com.sun.tools.javac.Main;

import org.example.model.AppUser;
import org.example.model.IAppUser;
import org.example.service.UserService;
import org.example.view.ILoginView;
import org.example.view.IMainGUI;
import org.example.view.LoginView;
import org.example.view.MainGUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class UserController implements IUserController {

    private ILoginView loginPage;

    private IMainGUI mainPage;

    private IAppUser appUser;

    public UserController() {
        loginPage = new LoginView();
        loginPage.addController(this);
        mainPage = new MainGUI();
        mainPage.addController(this);
        appUser = new AppUser();
    }

    @Override
    public boolean isUserAuthenticationComplete(String username, String password) {
        if (validateInput(username, password)) {
            String message = this.appUser.authenticateUser(username, password);
            this.loginPage.displayErrorBox(message);
            if (message.equals("Login successful!")) {
                this.mainPage.makeVisible();
                this.loginPage.disposeLoginPage();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isUserCreated(String username, String password) {
        if (validateInput(username, password)) {
            String message = this.appUser.createAccount(username, password);
            this.loginPage.displayErrorBox(message);
            return true;
        } else {
            return false;
        }
    }


    private boolean validateInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }
}
