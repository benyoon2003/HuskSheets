package org.example.view.button;

import org.example.view.SheetView;

/**
 * The Cut class represents a button that triggers a cut action in the associated SheetView.
 * It extends the Button class and is associated with a SheetView.
 */
public class Cut extends Button {

    /**
     * Constructs a Cut button with the specified SheetView.
     * When the button is clicked, it triggers the actionPerformed method in the ToolbarButtonListener.
     *
     * @param view the SheetView that the button is associated with
     */
    public Cut(SheetView view) {
        super("Cut");
        this.addActionListener(new ToolbarButtonListener(view));
    }
}
