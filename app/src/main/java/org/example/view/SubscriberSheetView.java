package org.example.view;

import org.example.controller.IUserController;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
import org.example.view.button.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * The SubscriberSheetView class represents a view for a spreadsheet that is subscribed to a publisher's updates.
 * @author Tony
 */
public class SubscriberSheetView extends SheetView {
    final IReadOnlySpreadSheet cells;
    final Map<Point, Color> highlightedCells = new HashMap<>();
    private String author;
    /**
     * Constructs a SubscriberSheetView with the given spreadsheet and publisher.
     *
     * @param publisher the publisher of the spreadsheet.
     * @param openSheet the spreadsheet to be displayed.
     * @author Tony
     */
    public SubscriberSheetView(String publisher, ISpreadsheet openSheet) {
        super(openSheet);
        this.cells = openSheet;
        this.author = publisher;
    }
    /**
     * Creates and sets up the toolbar with various buttons and their action listeners.
     * @author Tony
     */
    @Override
    public void makeToolbar() {
        formulaTextField = new JTextField(20);
        formulaTextField.setEditable(true);
        this.addComponent(new JLabel("Formula"))
                .addComponent(formulaTextField)
                .addComponent(new Cut(this))
                .addComponent(new Copy(this))
                .addComponent(new Paste(this))
                .addComponent(new ZoomI(this))
                .addComponent(new ZoomO(this))
                .addComponent(new GetUpdatesFromPublisher(this))
                .addComponent(new SaveSubscriber(this))
                .addComponent(new AddConditionalFormat(this))
                .addComponent(new Back(this));
        formulaTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeSpreadSheetValueAt(controller.getSelectedStartRow(),
                        controller.getSelectedStartCol(), formulaTextField.getText());
            }
        });
        revalidate();
        repaint();
    }
    @Override
    public void addController(IUserController controller) {
        super.addController(controller);
        this.publisher = this.author;
    }
}
