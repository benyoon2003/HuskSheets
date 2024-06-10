package org.example.view.button;

import org.example.view.IHomeView;
import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Back extends Button {
    public Back(SheetView view) {
        super("Back");

        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose(); // Close the current view
                IHomeView homeView = view.getController().getHomeView();
                homeView.updateSavedSheets();
                homeView.makeVisible();
            }
        });
    }
}
