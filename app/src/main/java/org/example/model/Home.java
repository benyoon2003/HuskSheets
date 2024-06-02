package org.example.model;

import java.util.ArrayList;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.h2.tools.Server;
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
            return new Spreadsheet(cellArray, path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveSheet(IReadOnlySpreadSheet sheet, String path) {

    }

    //Get payload of a sheet from server
    public ISpreadsheet readPayload(IAppUser user, ServerEndpoint se, String sheetName){
        System.out.println("User: " + user.getUsername() + ", Sheet Name: " + sheetName);
        try {
            String response = se.getUpdatesForSubscription(user.getUsername(), sheetName, "0");
            System.out.println("Response from server: " + response);
    
            String payload = Result.getPayload(response, sheetName);
            System.out.println("Payload received: " + payload);
    
            if (payload != null && !payload.isEmpty()) {
                List<List<String>> data = convertStringTo2DArray(payload);
                ISpreadsheet ss = new Spreadsheet(sheetName);
                ArrayList<ArrayList<Cell>> grid = ss.getCells();
    
                for (List<String> ls : data) {
                    ss.setCellRawdata(Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)), ls.get(2));
                    ss.setCellValue(Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)), ls.get(2));
                }
                return ss;
            } else {
                System.out.println("Payload is null or empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Spreadsheet(sheetName);
    }
    
    

    @Override
    public void writeXML(IReadOnlySpreadSheet sheet, String path) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.newDocument();

            // root element (sheet)
            Element root = dom.createElement("sheet");
            String name = this.trimEnds(path);
            root.setAttribute("name", name);

            // adding the cell values
            String[][] values = sheet.getCellStringsObject();
            for (int i = 0; i < sheet.getRows(); i++) {
                for (int j = 0; j < sheet.getCols(); j++) {
                    if (values[i][j] == "") {
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

            if (!path.endsWith(".xml"))
                path += ".xml";
            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<List<String>> convertStringTo2DArray(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("Input to convertStringTo2DArray is null or empty");
            return new ArrayList<>();
        }
    
        // Parse input into lines
        String[] lines = input.split("\\r?\\n");
    
        // List to store the 2D array
        List<List<String>> result = new ArrayList<>();
    
        // Process each line
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
    
            String[] parts = line.split(" ", 2);
            if (parts.length < 2) {
                continue;
            }
    
            String ref = parts[0];
            String content = parts[1];
    
            // Extract row and column from the reference
            int[] rowCol = convertRefToRowCol(ref);
    
            // Create the nested list for this cell
            List<String> cellData = new ArrayList<>();
            cellData.add(String.valueOf(rowCol[0])); // Row
            cellData.add(String.valueOf(rowCol[1])); // Column
            cellData.add(content); // Content
    
            // Add to the result list
            result.add(cellData);
        }
    
        return result;
    }
    

    // Convert cell reference (e.g., $A1) to row and column indices
    private static int[] convertRefToRowCol(String ref) {
        ref = ref.substring(1); // Remove the leading $
        int row = 0;
        int col = 0;
        int i = 0;

        // Extract column part (letters)
        while (i < ref.length() && Character.isLetter(ref.charAt(i))) {
            col = col * 26 + (ref.charAt(i) - 'A' + 1);
            i++;
        }

        // Extract row part (digits)
        while (i < ref.length() && Character.isDigit(ref.charAt(i))) {
            row = row * 10 + (ref.charAt(i) - '0');
            i++;
        }

        return new int[]{row - 1, col - 1}; // Convert to 0-based index
    }


    private String trimEnds(String s) {
        String result = new StringBuilder(s).reverse().toString();

        if (result.contains("\\"))
            result = result.substring(0, result.indexOf('\\'));
        else
            result = result.substring(0, result.indexOf('/'));
        result = new StringBuilder(result).reverse().toString();
        if (result.endsWith(".xml"))
            result = result.substring(0, result.indexOf('.'));

        return result;
    }
}