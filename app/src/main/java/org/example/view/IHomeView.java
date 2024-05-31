package org.example.view;

import org.example.controller.IUserController;

public interface IHomeView {
    void addController(IUserController controller);

    void makeVisible();

    void disposeHomePage();
    
    void updateSavedSheets(); // Add this method

    void openSheet(String absolutePath);
    
    void openSheetFromServer(String absolutePath);
}
