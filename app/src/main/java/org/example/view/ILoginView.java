package org.example.view;

import org.example.controller.IUserController;

public interface ILoginView {

  void addController(IUserController controller);

  void displayErrorBox(String message);

  void disposeLoginPage();
}
