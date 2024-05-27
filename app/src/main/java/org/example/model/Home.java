package org.example.model;

import java.util.ArrayList;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

public class Home implements IHome {

    public Spreadsheet readXML(String path) {

        try {
            File xmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList cellNodes = doc.getElementsByTagName("cell");
            int maxCol = 0;
            int maxRow = 0;

            // Find maximum column and row numbers
            for (int i = 0; i < cellNodes.getLength(); i++) {
                Element cellElement = (Element) cellNodes.item(i);
                int col = Integer.parseInt(cellElement.getAttribute("col"));
                int row = Integer.parseInt(cellElement.getAttribute("row"));
                maxCol = Math.max(maxCol, col);
                maxRow = Math.max(maxRow, row);
            }

            // Create 2D cell array
            ArrayList<ArrayList<Cell>> cellArray = new ArrayList<>();

            // Initialize cells
            for (int i = 0; i <= maxRow; i++) {
                ArrayList<Cell> row = new ArrayList<>();
                for (int j = 0; j <= maxCol; j++) {
                    Cell c = new Cell();
                    c.setRow(i);
                    c.setCol(j);
                    row.add(c);
                }
                cellArray.add(row);
            }

            // Fill cell values
            for (int i = 0; i < cellNodes.getLength(); i++) {
                Element cellElement = (Element) cellNodes.item(i);
                int col = Integer.parseInt(cellElement.getAttribute("col"));
                int row = Integer.parseInt(cellElement.getAttribute("row"));
                String value = cellElement.getTextContent();
                cellArray.get(row).get(col).setValue(value);
            }

            return new Spreadsheet(cellArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveSheet(ReadOnlySpreadSheet sheet, String path) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.newDocument();

            // root element (sheet)
            Element root = dom.createElement("sheet");

            // adding the cell values
            String[][] values = sheet.getCellStringsObject();
            for (int i = 0; i < sheet.getRows(); i++) {
                for (int j = 0; j < sheet.getCols(); j++) {
                    if (values[i][j] == "" || values[i][j] == null) {
                        continue;
                    } else {
                        Element e = dom.createElement("cell");
                        e.setAttribute("row", Integer.toString(i));
                        e.setAttribute("col", Integer.toString(j));
                        e.appendChild(dom.createTextNode(values[i][j]));
                        root.appendChild(e);
                    }
                }
            }

            dom.appendChild(root);

            // save file
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            if (!path.endsWith(".xml")) {
                path += ".xml";
            }
            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}