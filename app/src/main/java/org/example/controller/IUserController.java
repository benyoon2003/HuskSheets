package org.example.controller;

import org.example.model.AppUser;
import org.example.view.ISheetView;
import org.example.view.SheetView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserController {

  boolean isUserAuthenticationComplete(String username, String password);

  boolean isUserCreated(String username, String password);

  void setCurrentSheet(ISheetView sheetView);
}
