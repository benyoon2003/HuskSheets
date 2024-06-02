package org.example.view;

import org.example.controller.IUserController;

public interface ISheetView {
    void addController(IUserController controller);

    void makeVisible();

    void displayMessage(String s);

    void updateTable();

    void changeFormulaTextField(String rawdata);

    String getExcelColumnName(int columnNumber);
}
