package org.example.view.button;

import org.example.view.SheetView;

/**
 * The ISave interface defines a contract for handling save operations on a SheetView.
 */
public interface ISave {

    /**
     * Handles the save operation for the specified SheetView.
     *
     * @param view the SheetView instance to be saved
     */
    void handleSave(SheetView view);
}
