package org.example.model;

import java.util.ArrayList;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

/**
 * The Home class provides methods to read and write spreadsheet data from and
 * to XML files.
 */
public class Home implements IHome {

    /**
     * Reads a spreadsheet from an XML file.
     *
     * @param path the path of the XML file
     * @return a Spreadsheet object representing the data in the XML file
     */
    public ISpreadsheet readXML(String path) {
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

            return generateSpreadsheet(cellNodes, maxRow, maxCol, path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a spreadsheet using the given NodeList, max rows, cols, and path.
     * @param cellNodes NodeList from XML
     * @param maxRow num rows
     * @param maxCol num columns
     * @param path a path
     * @return a ISpreadsheet
     */
    private ISpreadsheet generateSpreadsheet(NodeList cellNodes, int maxRow,
                                             int maxCol, String path) {
        // Create 2D cell array
        ArrayList<ArrayList<Cell>> cellArray = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < 100; j++) {
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
        return new Spreadsheet(cellArray, trimEnds(path));
    }

    /**
     * Reads the payload of a sheet from the server.
     * @param payload payload to be parsed.
     * @param sheetName name of sheet
     * @return a ISpreadSheet
     */
    public ISpreadsheet readPayload(String payload, String sheetName) {
        List<List<String>> data = convertStringTo2DArray(payload);
        ISpreadsheet spreadsheet = new Spreadsheet(sheetName);
        for (List<String> ls : data) {
            spreadsheet.setCellRawdata(Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)), ls.get(2));
            spreadsheet.setCellValue(Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)), ls.get(2));
        }
        return spreadsheet;
    }

    /**
     * Writes the spreadsheet data to an XML file.
     *
     * @param sheet the spreadsheet data to write
     * @param path  the path of the XML file to write to
     */
    @Override
    public void writeXML(IReadOnlySpreadSheet sheet, String path) {
        try {
            path = path.trim();
            Document dom = createDocument();
            Element root = createRootElement(dom, path);
            populateDocumentWithSheetData(dom, root, sheet);
            writeDocumentToFile(dom, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new document.
     * @return a Document
     * @throws Exception
     */
    private Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    /**
     * Creates a root element for the XML.
     * @param dom a Document
     * @param path a path
     * @return an Element
     */
    private Element createRootElement(Document dom, String path) {
        Element root = dom.createElement("sheet");
        String name = trimEnds(path);
        root.setAttribute("name", name);
        dom.appendChild(root);
        return root;
    }

    /**
     * Places sheet data within the XML.
     * @param dom a Document
     * @param root an Element in the XML
     * @param sheet a IReadOnlySpreadSheet
     */
    private void populateDocumentWithSheetData(Document dom, Element root, IReadOnlySpreadSheet sheet) {
        String[][] values = sheet.getCellStringsObject();
        for (int i = 0; i < sheet.getRows(); i++) {
            for (int j = 0; j < sheet.getCols(); j++) {
                if (!values[i][j].equals("")) {
                    Element cellElement = createCellElement(dom, i, j, values[i][j]);
                    root.appendChild(cellElement);
                }
            }
        }
    }

    /**
     * Creates a cell Element for the XML.
     * @param dom a Document
     * @param row a row index
     * @param col a column index
     * @param value the Cell content
     * @return an XML Element
     */
    private Element createCellElement(Document dom, int row, int col, String value) {
        Element cellElement = dom.createElement("cell");
        cellElement.setAttribute("row", Integer.toString(row));
        cellElement.setAttribute("col", Integer.toString(col));
        cellElement.appendChild(dom.createTextNode(value));
        return cellElement;
    }

    /**
     * Writes the Document to a XML file.
     * @param dom a Document
     * @param path a path
     * @throws Exception a FileNotFoundException, SecurityException or
     * TransformerConfigurationException
     */
    private void writeDocumentToFile(Document dom, String path) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        if (!path.endsWith(".xml")) {
            path += ".xml";
        }
        transformer.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(path)));
    }

    /**
     * Converts a string representation of a 2D array to an actual 2D array.
     *
     * @param input the string representation of the 2D array
     * @return a list of lists representing the 2D array
     */
    public static List<List<String>> convertStringTo2DArray(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("Input to convertStringTo2DArray is null or empty");
            return new ArrayList<>();
        }
        // Replace literal "\n" with actual newline characters if needed
        if (input.contains("\\n")) {
            input = input.replace("\\n", "\n");
        }
        String[] lines = input.split("\\r?\\n");
        List<List<String>> result = new ArrayList<>();

        // Process each line that was split
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
            List<String> cellData = new ArrayList<>();
            cellData.add(String.valueOf(rowCol[0])); // Row
            cellData.add(String.valueOf(rowCol[1])); // Column
            cellData.add(content); // Content
            result.add(cellData);
        }
        return result;
    }

    /**
     * Converts a cell reference (e.g., $A1, $AA4) to row and column indices.
     *
     * @param ref the cell reference
     * @return an array with the row and column indices
     */

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

        return new int[] { row - 1, col - 1 }; // Convert to 0-based index
    }

    /**
     * Trims the ends of a string, removing any directory path and file extension.
     *
     * @param s the string to trim
     * @return the trimmed string
     */
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