package org.example.view.button;

import org.example.view.SheetView;

/**
 * The Paste class represents a button that triggers a paste action in the associated SheetView.
 * It extends the Button class and is associated with a SheetView.
 */
public class Paste extends Button {

    /**
     * Constructs a Paste button with the specified SheetView.
     * When the button is clicked, it triggers the actionPerformed method in the ToolbarButtonListener.
     *
     * @param view the SheetView that the button is associated with
     */
    public Paste(SheetView view) {
        super("Paste");
        this.addActionListener(new ToolbarButtonListener(view));
    }
}
