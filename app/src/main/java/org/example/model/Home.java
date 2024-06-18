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

    public ISpreadsheet readXML(String path) {
        try {
            File xmlFile = new File(path); // Create a File object for the XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance(); // Create a DocumentBuilderFactory
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder(); // Create a DocumentBuilder
            Document doc = dBuilder.parse(xmlFile); // Parse the XML file into a Document
            doc.getDocumentElement().normalize(); // Normalize the document
            NodeList cellNodes = doc.getElementsByTagName("cell"); // Get all the cell elements
            int maxCol = 0; // Initialize max column
            int maxRow = 0; // Initialize max row

            // Find maximum column and row numbers
            for (int i = 0; i < cellNodes.getLength(); i++) {
                Element cellElement = (Element) cellNodes.item(i); // Get each cell element
                int col = Integer.parseInt(cellElement.getAttribute("col")); // Get the column number
                int row = Integer.parseInt(cellElement.getAttribute("row")); // Get the row number
                maxCol = Math.max(maxCol, col); // Update max column
                maxRow = Math.max(maxRow, row); // Update max row
            }
            
            // Generate and return the spreadsheet
            return generateSpreadsheet(cellNodes, maxRow, maxCol, path);
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace if an exception occurs
            return null; // Return null if an error occurs
        }
    }

    /**
     * Generates a spreadsheet using the given NodeList, max rows, cols, and path.
     *
     * @param cellNodes NodeList from XML
     * @param maxRow    num rows
     * @param maxCol    num columns
     * @param path      a path
     * @return a ISpreadsheet
     * @author Ben
     */
    private ISpreadsheet generateSpreadsheet(NodeList cellNodes, int maxRow,
                                             int maxCol, String path) {
        // Create 2D cell array
        List<List<Cell>> cellArray = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < 100; j++) {
                Cell c = new Cell(); // Create a new cell
                c.setRow(i); // Set the row index
                c.setCol(j); // Set the column index
                row.add(c); // Add the cell to the row
            }
            cellArray.add(row); // Add the row to the cell array
        }

        // Fill cell values
        for (int i = 0; i < cellNodes.getLength(); i++) {
            Element cellElement = (Element) cellNodes.item(i); // Get each cell element
            int col = Integer.parseInt(cellElement.getAttribute("col")); // Get the column number
            int row = Integer.parseInt(cellElement.getAttribute("row")); // Get the row number
            String value = cellElement.getTextContent(); // Get the cell value
            cellArray.get(row).get(col).setValue(value); // Set the cell value in the array
        }
        return new Spreadsheet(cellArray, trimEnds(path)); // Return the generated spreadsheet
    }

    public ISpreadsheet readPayload(String payload, String sheetName) {
        List<List<String>> data = convertStringTo2DArray(payload); // Convert payload to 2D array
        ISpreadsheet spreadsheet = new Spreadsheet(sheetName); // Create a new spreadsheet
        for (List<String> ls : data) {
            spreadsheet.setCellRawdata(Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)), ls.get(2)); // Set cell raw data
            spreadsheet.setCellValue(Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)), ls.get(2)); // Set cell value
        }
        return spreadsheet; // Return the spreadsheet
    }

    @Override
    public void writeXML(IReadOnlySpreadSheet sheet, String path) {
        try {
            path = path.trim(); // Trim the path string
            Document dom = createDocument(); // Create a new document
            Element root = createRootElement(dom, path); // Create the root element
            populateDocumentWithSheetData(dom, root, sheet); // Populate the document with sheet data
            writeDocumentToFile(dom, path); // Write the document to a file
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace if an exception occurs
        }
    }

    /**
     * Creates a new document.
     *
     * @return a Document
     * @throws Exception
     * @author Theo
     */
    private Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance(); // Create a DocumentBuilderFactory
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder(); // Create a DocumentBuilder
        return docBuilder.newDocument(); // Create and return a new document
    }

    /**
     * Creates a root element for the XML.
     *
     * @param dom  a Document
     * @param path a path
     * @return an Element
     * @author Theo
     */
    private Element createRootElement(Document dom, String path) {
        Element root = dom.createElement("sheet"); // Create a new element for the sheet
        String name = trimEnds(path); // Trim the path to get the sheet name
        root.setAttribute("name", name); // Set the name attribute of the root element
        dom.appendChild(root); // Append the root element to the document
        return root; // Return the root element
    }

    /**
     * Places sheet data within the XML.
     *
     * @param dom   a Document
     * @param root  an Element in the XML
     * @param sheet a IReadOnlySpreadSheet
     * @author Theo
     */
    private void populateDocumentWithSheetData(Document dom, Element root, IReadOnlySpreadSheet sheet) {
        String[][] values = sheet.getCellStringsObject(); // Get the cell values from the sheet
        for (int i = 0; i < sheet.getRows(); i++) {
            for (int j = 0; j < sheet.getCols(); j++) {
                if (!values[i][j].equals("")) { // If the cell is not empty
                    Element cellElement = createCellElement(dom, i, j, values[i][j]); // Create a cell element
                    root.appendChild(cellElement); // Append the cell element to the root
                }
            }
        }
    }

    /**
     * Creates a cell Element for the XML.
     *
     * @param dom   a Document
     * @param row   a row index
     * @param col   a column index
     * @param value the Cell content
     * @return an XML Element
     * @author Theo
     */
    private Element createCellElement(Document dom, int row, int col, String value) {
        Element cellElement = dom.createElement("cell"); // Create a new cell element
        cellElement.setAttribute("row", Integer.toString(row)); // Set the row attribute
        cellElement.setAttribute("col", Integer.toString(col)); // Set the column attribute
        cellElement.appendChild(dom.createTextNode(value)); // Set the cell's value
        return cellElement; // Return the cell element
    }

    /**
     * Writes the Document to a XML file.
     *
     * @param dom  a Document
     * @param path a path
     * @throws Exception a FileNotFoundException, SecurityException or
     *                   TransformerConfigurationException
     * @author Theo
     */
    private void writeDocumentToFile(Document dom, String path) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer(); // Create a Transformer
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Set output properties
        transformer.setOutputProperty(OutputKeys.METHOD, "xml"); // Set the method to XML
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // Set the encoding to UTF-8

        if (!path.endsWith(".xml")) {
            path += ".xml"; // Append ".xml" if the path does not end with it
        }
        transformer.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(path))); // Transform the document to a file
    }

    /**
     * Converts a string representation of a 2D array to an actual 2D array.
     *
     * @param input the string representation of the 2D array
     * @return a list of lists representing the 2D array
     * @author Vinay
     */
    public static List<List<String>> convertStringTo2DArray(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("Input to convertStringTo2DArray is null or empty");
            return new ArrayList<>(); // Return an empty list if the input is null or empty
        }
        // Replace literal "\n" with actual newline characters if needed
        if (input.contains("\\n")) {
            input = input.replace("\\n", "\n"); // Replace literal "\n" with actual newline characters if needed
        }
        String[] lines = input.split("\\r?\\n"); // Split the input into lines
        List<List<String>> result = new ArrayList<>();

        // Process each line that was split
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue; // Skip empty lines
            }
            String[] parts = line.split(" ", 2); // Split the line into two parts
            String content;
            if (parts.length < 2) {
                content = ""; // Get the cell content
            }
            else {
                content = parts[1]; // Get the cell content
            }
            String ref = parts[0]; // Get the cell reference

            // Extract row and column from the reference
            int[] rowCol = convertRefToRowCol(ref); // Convert the reference to row and column indices
            List<String> cellData = new ArrayList<>();
            cellData.add(String.valueOf(rowCol[0])); // Add the row index to the list
            cellData.add(String.valueOf(rowCol[1])); // Add the column index to the list
            cellData.add(content); // Add the cell content to the list
            result.add(cellData); // Add the list to the result
        }
        return result; // Return the result
    }

    /**
     * Converts a cell reference (e.g., $A1, $AA4) to row and column indices.
     *
     * @param ref the cell reference
     * @return an array with the row and column indices
     * @author Tony
     */

    private static int[] convertRefToRowCol(String ref) {
        ref = ref.substring(1); // Remove the leading $
        int row = 0;
        int col = 0;
        int i = 0;

        // Extract column part (letters)
        while (i < ref.length() && Character.isLetter(ref.charAt(i))) {
            col = col * 26 + (ref.charAt(i) - 'A' + 1); // Convert letters to column index
            i++;
        }

        // Extract row part (digits)
        while (i < ref.length() && Character.isDigit(ref.charAt(i))) {
            row = row * 10 + (ref.charAt(i) - '0'); // Convert digits to row index
            i++;
        }

        return new int[]{row - 1, col - 1}; // Convert to 0-based index
    }

    /**
     * Trims the ends of a string, removing any directory path and file extension.
     *
     * @param s the string to trim
     * @return the trimmed string
     * @author Theo
     */
    private String trimEnds(String s) {
        String result = new StringBuilder(s).reverse().toString(); // Reverse the string

        if (result.contains("\\")) {
            result = result.substring(0, result.indexOf('\\')); // Remove the directory path if it contains a backslash
        } else if (result.contains("/")) {
            result = result.substring(0, result.indexOf('/')); // Remove the directory path if it contains a forward slash
        }
        result = new StringBuilder(result).reverse().toString(); // Reverse the string back to its original order
        if (result.endsWith(".xml")) {
            result = result.substring(0, result.lastIndexOf('.')); // Remove the file extension if it ends with ".xml"
        }
        return result; // Return the trimmed string
    }
}