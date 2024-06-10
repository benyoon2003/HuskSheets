package org.example.view;

import org.example.controller.IUserController;
import org.example.model.IReadOnlySpreadSheet;
import org.example.model.ISpreadsheet;
import org.example.model.Spreadsheet;
import org.example.view.button.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
/**
 * The SubscriberSheetView class represents a view for a spreadsheet that is subscribed to a publisher's updates.
 */
public class SubscriberSheetView extends SheetView {
    final IReadOnlySpreadSheet cells;
    private final Map<Point, Color> highlightedCells = new HashMap<>();
    private String author;
    /**
     * Constructs a SubscriberSheetView with the given spreadsheet and publisher.
     *
     * @param publisher the publisher of the spreadsheet.
     * @param openSheet the spreadsheet to be displayed.
     */
    public SubscriberSheetView(String publisher, ISpreadsheet openSheet) {
        super(openSheet);
        this.cells = openSheet;
        this.author = publisher;
    }
    /**
     * Creates and sets up the toolbar with various buttons and their action listeners.
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
                .addComponent(new SaveSubscirber(this))
                .addComponent(new AddConditionalFormat(this))
                .addComponent(new Back(this));
        formulaTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeSpreadSheetValueAt(controller.getSelectedRow(),
                        controller.getSelectedCol(), formulaTextField.getText());
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
