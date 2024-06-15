package org.example.view.button;

import org.example.view.IHomeView;
import org.example.view.SheetView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Back class represents a button that navigates back to the home view from the current sheet view.
 * It extends the Button class and is associated with a SheetView.
 */
public class Back extends Button {

    /**
     * Constructs a Back button with the specified SheetView.
     * When the button is clicked, it triggers the actionPerformed method to navigate back to the home view.
     *
     * @param view the SheetView that the button is associated with
     */
    public Back(SheetView view) {
        super("Back");

        this.addActionListener(new ActionListener() {
            /**
             * Invoked when the Back button is clicked.
             * This method disposes of the current view, updates the saved sheets in the home view,
             * and makes the home view visible.
             *
             * @param e the event to be processed
             */
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
