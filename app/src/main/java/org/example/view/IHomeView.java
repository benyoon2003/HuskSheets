package org.example.view;

import org.example.controller.IUserController;

/**
 * Interface representing the home view in the application.
 * Provides methods for interaction between the view and the user controller.
 */
public interface IHomeView {
    /**
     * Adds a controller to the home view.
     *
     * @param controller the IUserController instance to add.
     * @author Theo
     */
    void addController(IUserController controller);

    /**
     * Makes the home view visible.
     * 
     * @author Tony
     */
    void makeVisible();

    /**
     * Disposes of the home page, closing the view.
     * 
     * @author Vinay
     */
    void disposeHomePage();

    /**
     * Updates the list of saved sheets displayed in the home view.
     * 
     * @author Ben
     */
    void updateSavedSheets();

    /**
     * Displays an error message in a dialog box.
     *
     * @param message the error message to display.
     * 
     * @author Theo
     */
    void displayErrorBox(String message);
}
