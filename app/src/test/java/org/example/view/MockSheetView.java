package org.example.view;

import java.io.StringWriter;

import org.example.controller.IUserController;

public class MockSheetView extends SheetView {
    private StringWriter out;

    public MockSheetView() {
        this.out = new StringWriter();
    }

    @Override
    public void addController(IUserController controller) {
        this.out.append("Controller added\n");
    }

    @Override
    public void makeVisible() {
        this.out.append("Sheet is now visible\n");
    }

    @Override
    public void displayMessage(String s) {
        this.out.append(s + "\n");
    }

    public String toString() {
        return this.out.toString();
    }
}
