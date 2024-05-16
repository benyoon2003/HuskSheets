package org.example.view;

import org.example.controller.IUserController;

public interface IMainGUI {

  void addController(IUserController controller);

  void makeVisible();
}
