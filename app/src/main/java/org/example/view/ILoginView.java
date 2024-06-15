package org.example.view;

import org.example.controller.IUserController;

/**
 * Interface representing the login view in the application.
 * Provides methods for interaction between the view and the user controller.
 */
public interface ILoginView {

  /**
   * Adds a controller to the login view.
   *
   * @param controller the IUserController instance to add.
   * @author Vinay
   */
  void addController(IUserController controller);

  /**
   * Displays an error message in a dialog box.
   *
   * @param message the error message to display.
   * @author Theo
   */
  void displayErrorBox(String message);
  
  /**
   * Disposes of the login page, closing the view.
   * @author Theo
   */
  void disposeLoginPage();
}
