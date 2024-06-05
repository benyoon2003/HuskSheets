package org.example.view;

import java.io.StringWriter;

import org.example.controller.IUserController;
import org.example.model.Spreadsheet;

public class MockSheetView extends SheetView {
    private StringWriter out;
    private IUserController controller;

    public MockSheetView() {
        super(new Spreadsheet("Test"));
        this.out = new StringWriter();
    }

    @Override
    public void addController(IUserController controller) {
        this.out.append("Controller added\n");
        super.addController(controller);
    }

    @Override
    public void makeVisible() {
        this.out.append("Sheet is now visible\n");
    }

    @Override
    public void displayMessage(String s) {
        this.out.append(s).append("\n");
    }

    @Override
    public void updateTable() {
        this.out.append("Table updated with latest changes\n");
        super.updateTable();
    }

    @Override
    public void changeFormulaTextField(String rawdata) {
        this.out.append("Formula text field: ").append(rawdata).append("\n");
    }

    @Override
    public String toString() {
        return this.out.toString();
    }
}
