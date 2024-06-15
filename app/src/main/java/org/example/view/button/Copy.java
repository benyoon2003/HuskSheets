package org.example.view.button;

import org.example.view.SheetView;

/**
 * The Copy class represents a button that triggers a copy action in the associated SheetView.
 * It extends the Button class and is associated with a SheetView.
 */
public class Copy extends Button {

    /**
     * Constructs a Copy button with the specified SheetView.
     * When the button is clicked, it triggers the actionPerformed method in the ToolbarButtonListener.
     *
     * @param view the SheetView that the button is associated with
     */
    public Copy(SheetView view) {
        super("Copy");
        this.addActionListener(new ToolbarButtonListener(view));
    }
}
