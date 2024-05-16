package org.example.view;

import org.example.controller.IUserController;

public interface ILoginView {

  void addController(IUserController controller);

  void displayErrorBox(Object message);

  void disposeLoginPage();
}
