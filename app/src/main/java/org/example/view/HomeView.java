package org.example.view;


import org.example.controller.IUserController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



public class HomeView extends JFrame implements IHomeView {

  private JButton createSheet;

  private IUserController controller;

  public HomeView() {
    setTitle("Main GUI");
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel panel = new JPanel();

    placeComponents(panel);

    add(panel);
  }

  private void placeComponents(JPanel panel) {
    panel.setLayout(null);

    JLabel spreadheetsLabel = new JLabel("Spreadsheets:");
    spreadheetsLabel.setBounds(50, 50, 100, 25);
    panel.add(spreadheetsLabel);

    createSheet = new JButton("Create Spreadsheet");
    createSheet.setBounds(50, 70, 200, 25);
    panel.add(createSheet);

    createSheet.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
       controller.createNewSheet();
      }
    });
  }

  @Override
  public void addController(IUserController controller) {
    this.controller = controller;
  }

  @Override
  public void makeVisible() {
    this.setVisible(true);
  }

  @Override
  public void disposeHomePage() {
    this.dispose();
  }
}
