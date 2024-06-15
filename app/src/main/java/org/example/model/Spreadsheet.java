package org.example.model;

import java.util.ArrayList;

import java.util.List;

import java.util.Arrays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graalvm.polyglot.Context;

/**
 * Represents a spreadsheet with various functionalities such as evaluating
 * formulas,
 * managing cells, and handling subscriptions and publications.
 */

public class Spreadsheet implements ISpreadsheet {

    private List<List<Cell>> grid; // The grid representing the spreadsheet

    private String name; // The name of the spreadsheet
    private int id_version; // Version ID for tracking updates
    // used to retrieve version for GetUpdatesForSubscription
    private List<ISpreadsheet> publishVersions;

    // used to retrieve version for GetUpdatesPublished
    private List<ISpreadsheet> subscribeVersions;

    private String[] functions = new String[]{"IF", "SUM", "MIN", "MAX", "AVG", "CONCAT", "DEBUG", "STDDEV", "SORT"};
    private String[] arith = new String[]{"+", "-", "*", "/"};
    private String[] operations = new String[]{"<>", "<", ">", "=", "&", "|", ":"};

    /**
     * Constructs a new Spreadsheet with the specified name.
     *
     * @param name the name of the spreadsheet.
     */
    public Spreadsheet(String name) {
        grid = new ArrayList<>(); // Initialize the grid
        for (int i = 0; i < 100; i++) { // Loop to create 100 rows
            ArrayList<Cell> row = new ArrayList<>(); // Initialize a new row
            for (int j = 0; j < 100; j++) { // Loop to create 100 columns in each row
                row.add(new Cell("")); // Add a new empty cell to the row
            }
            grid.add(row); // Add the row to the grid
        }

        this.name = name; // Set the name of the spreadsheet
        this.publishVersions = new ArrayList<>(); // Initialize the publish versions list
        this.subscribeVersions = new ArrayList<>(); // Initialize the subscribe versions list

    }

    /**
     * Constructs a new Spreadsheet with the specified grid and name.
     *
     * @param grid the grid of cells.
     * @param name the name of the spreadsheet.
     */
    public Spreadsheet(List<List<Cell>> grid, String name) {
        this(name); // Call the other constructor to initialize the object
        for (int i = 0; i < 100; i++) { // Loop to process the grid
            List<Cell> row = grid.get(i); // Get a row from the input grid
            for (int j = 0; j < 100; j++) { // Loop to process each cell in the row
                Cell cell = row.get(j); // Get a cell from the input row
                if (!cell.getValue().isEmpty()) { // Check if the cell value is not empty
                    this.grid.get(i).set(j, cell); // Set the cell in the grid
                }
            }
        }
    }

    /**
     * Converts the given IReadOnlySpreadSheet into a valid String payload for
     * transmission
     * as part of JSON
     *
     * @param sheet the sheet to convert
     * @return a payload (e.g $A1 4\n)
     */
    public static String convertSheetToPayload(IReadOnlySpreadSheet sheet) {
        StringBuilder payload = new StringBuilder(); // Initialize a StringBuilder for the payload
        Cell[][] values = sheet.getCellsObject(); // Get the cell values as a 2D array
        for (int i = 0; i < sheet.getRows(); i++) { // Loop through the rows
            for (int j = 0; j < sheet.getCols(); j++) { // Loop through the columns
                if (values[i][j] != null && !values[i][j].getRawdata().isEmpty()) { // Check if the cell is not null and has raw data
                    String cellValue = values[i][j].isFormula() ? values[i][j].getFormula() : values[i][j].getRawdata();
                    payload.append(String.format("$%s%s %s\\n", getColumnName(j + 1), i + 1, cellValue)); // Append the cell data to the payload
                }
            }
        }
        return payload.toString(); // Return the payload as a string
    }

    /**
     * Gets the column label using the given column number.
     *
     * @param columnNumber a number that corresponds to a column in the spreadsheet
     * @return a column label (e.g A, D, F, G)
     */
    public static String getColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder(); // Initialize a StringBuilder for the column name
        while (columnNumber > 0) { // Loop to convert the column number to a column name
            int remainder = (columnNumber - 1) % 26; // Calculate the remainder
            columnName.insert(0, (char) (remainder + 'A')); // Insert the character at the beginning
            columnNumber = (columnNumber - 1) / 26; // Update the column number
        }
        return columnName.toString(); // Return the column name
    }

    @Override
    public int getRows() {
        return this.grid.size(); // Return the number of rows in the grid
    }

    @Override
    public int getCols() {
        return this.grid.get(0).size(); // Return the number of columns in the grid
    }

    @Override
    public List<List<Cell>> getCells() {
        return this.grid; // Return the grid
    }

    @Override
    public Cell[][] getCellsObject() {
        Cell[][] retObject = new Cell[this.getRows()][this.getCols()]; // Initialize a 2D array for the cells
        for (int r = 0; r < this.getRows(); r++) { // Loop through the rows
            List<Cell> row = this.grid.get(r); // Get a row from the grid
            for (int c = 0; c < this.getCols(); c++) { // Loop through the columns
                retObject[r][c] = row.get(c); // Set the cell in the 2D array
            }
        }
        return retObject; // Return the 2D array
    }

    @Override
    public String[][] getCellStringsObject() {
        String[][] retObject = new String[this.getRows()][this.getCols()]; // Initialize a 2D array for the cell values
        for (int r = 0; r < this.getRows(); r++) { // Loop through the rows
            List<Cell> row = this.grid.get(r); // Get a row from the grid
            for (int c = 0; c < this.getCols(); c++) { // Loop through the columns
                retObject[r][c] = row.get(c).getValue(); // Set the cell value in the 2D array
            }
        }
        return retObject;
    }

    @Override
    public int getRow(String cell) {
        try {
            return Integer.parseInt(cell.replaceAll("[^0-9]", "")) - 1; // Extract the row number from the cell reference
        } catch (NumberFormatException e) {
            return -1; // Return -1 if there is a number format exception
        }
    }

    @Override
    public int getColumn(String cell) {
        String col = cell.replaceAll("[^A-Z]", "").toUpperCase(); // Extract the column part from the cell reference
        int column = 0; // Initialize the column number
        for (int i = 0; i < col.length(); i++) { // Loop through the characters in the column part
            column = column * 26 + (col.charAt(i) - 'A' + 1); // Convert the character to a number
        }
        return column - 1; // Return the column number (0-based index)
    }

    @Override
    public void addPublished(ISpreadsheet sheet) {
        this.publishVersions.add(sheet); // Add the sheet to the publish versions list
        this.id_version++; // Increment the version ID
    }

    @Override
    public void addSubscribed(ISpreadsheet sheet) {
        this.subscribeVersions.add(sheet); // Add the sheet to the subscribe versions list
    }

    @Override
    public List<ISpreadsheet> getPublishedVersions() {
        return this.publishVersions; // Return the publish versions list
    }

    @Override
    public List<ISpreadsheet> getSubscribedVersions() {
        return this.subscribeVersions; // Return the subscribe versions list
    }

    @Override
    public void setGrid(List<List<Cell>> updatedGrid) {
        this.grid = updatedGrid; // Set the grid to the updated grid
    }

    @Override
    public List<List<Cell>> getGrid() {
        return this.grid; // Return the grid
    }

    @Override
    public String getName() {
        return this.name; // Return the name of the spreadsheet
    }

    @Override
    public int getId_version() {
        return this.id_version; // Return the version ID
    }

    @Override
    public void setCellValue(int row, int col, String value) {
        this.grid.get(row).get(col).setValue(evaluateFormula(value)); // Set the cell value after evaluating the formula
    }

    @Override
    public String getCellValue(int row, int col) {
        return this.grid.get(row).get(col).getValue(); // Return the cell value
    }

    @Override
    public void setCellRawdata(int row, int col, String val) {
        this.grid.get(row).get(col).setRawData(val); // Set the raw data of the cell
    }

    @Override
    public String getCellRawdata(int row, int col) {
        return this.grid.get(row).get(col).getRawdata(); // Return the raw data of the cell

    }

    @Override
    public String evaluateFormula(String formula) {
        System.out.println(formula); // Print the formula for debugging
        if (!formula.startsWith("=")) {
            return formula; // Return the formula if it does not start with "="
        }

        // Remove the initial "="
        formula = formula.substring(1).stripLeading();

        try {
            boolean evaluated = false; // Initialize evaluated flag
            Object result = formula; // Initialize result with the formula
            formula = parseOperations(formula); // Parse operations in the formula
            formula = replaceCellReferences(formula); // Replace cell references with their values
            System.setProperty("polyglot.engine.WarnInterpreterOnly", "false"); // Set system property to avoid warnings
            if (!((String) result).equals(formula)) { // Check if formula has been modified
                result = formula; // Update result with the modified formula
                evaluated = true; // Set evaluated flag to true
            }
            if (containsArith((String) result) && !evaluated) { // Check if formula contains arithmetic operations
                Context context = Context.create("js"); // Create a JavaScript context
                result = context.eval("js", formula); // Evaluate the formula using JavaScript
            }
            return result.toString(); // Return the result as a string
        } catch (Exception e) {
            return "Error"; // Return "Error" if an exception occurs
        }
    }

    /**
     * Parses and handles various operations in the formula.
     *
     * @param formula the formula to parse.
     * @return the result of parsing the formula.
     * @author Vinay
     */
    private String parseOperations(String formula) {
        String operation = getOperation(formula); // Get the operation in the formula
        if (operation != "") {
            String[] parts = formula.replaceAll(" ", "").split(operation);  // Split the formula into parts

            if (formula.contains("<>")) {
                return compareNotEqual(parts[0].trim(), parts[1].trim()); // Compare not equal
            } else if (formula.contains("<") && !formula.contains("=")) {
                return compareLess(parts[0].trim(), parts[1].trim()); // Compare less than
            } else if (formula.contains(">") && !formula.contains("=")) {
                return compareGreater(parts[0].trim(), parts[1].trim()); // Compare greater than
            } else if (formula.contains("=") && !formula.contains("<") && !formula.contains(">")) {
                return compareEqual(parts[0].trim(), parts[1].trim()); // Compare equal
            } else if (formula.contains("&")) {
                return andOperation(parts[0].trim(), parts[1].trim());  // AND operation
            } else if (formula.contains("|")) {
                return orOperation(parts[0].trim(), parts[2].trim()); // OR operation
            } else if (formula.contains(":")) {
                return rangeOperation(parts[0].trim(), parts[1].trim()); // Range operation
            }
        }

        String function = getFunction(formula); // Get the function in the formula
        if (function != "") {
            int start = function.length() + 1; // Calculate start index of the arguments
            int end = formula.length() - 1; // Calculate end index of the arguments
            String args = formula.substring(start, end); // Extract the arguments

            if (formula.startsWith("IF(")) {
                return evaluateIF(args); // Evaluate IF function
            } else if (formula.startsWith("SUM(")) {
                return evaluateSUM(args); // Evaluate SUM function
            } else if (formula.startsWith("MIN(")) {
                return evaluateMIN(args); // Evaluate MIN function
            } else if (formula.startsWith("MAX(")) {
                return evaluateMAX(args); // Evaluate MAX function
            } else if (formula.startsWith("AVG(")) {
                return evaluateAVG(args); // Evaluate AVG function
            } else if (formula.startsWith("CONCAT(")) {
                return evaluateCONCAT(args); // Evaluate CONCAT function
            } else if (formula.startsWith("DEBUG(")) {
                return evaluateDEBUG(args); // Evaluate DEBUG function
            } else if (formula.startsWith("STDDEV(")) {
                return evaluateSTDDEV(args); // Evaluate STDDEV function
            } else if (formula.startsWith("SORT(")) {
                return evaluateSORT(args); // Evaluate SORT function
            }
        }

        return formula; // Return the formula if no operations or functions are found
    }

    /**
     * Replaces cell references in the formula with their actual values.
     *
     * @param formula the formula with cell references.
     * @return the formula with cell references replaced by values.
     * @author Vinay
     */
    private String replaceCellReferences(String formula) {
        Pattern pattern = Pattern.compile("\\$[A-Z]+[0-9]+"); // Define pattern for cell references
        Matcher matcher = pattern.matcher(formula); // Create matcher for the formula
        StringBuffer result = new StringBuffer(); // Initialize result buffer

        while (matcher.find()) {
            String cellReference = matcher.group(); // Get the cell reference
            int row = getRow(cellReference); // Get the row index
            int col = getColumn(cellReference); // Get the column index
            String cellValue = getCellValue(row, col); // Get the cell value
            matcher.appendReplacement(result, cellValue); // Replace the cell reference with its value
        }
        matcher.appendTail(result); // Append the remaining part of the formula

        return result.toString(); // Return the modified formula
    }

    /**
     * Checks if the cell contains a function.
     *
     * @param cell the cell content.
     * @return the function if present, otherwise an empty string.
     * @author Theo
     */
    private String getFunction(String cell) {
        for (String func : functions) { // Loop through the functions
            if (cell.contains(func)) {
                return func; // Return the function if found
            }
        }

        return ""; // Return empty string if no function is found
    }

    /**
     * Checks if the cell contains a basic arithmetic operation.
     *
     * @param cell the cell content.
     * @return true if it contains an arithmetic operation, otherwise false.
     * @author Vinay
     */
    private boolean containsArith(String cell) {
        for (String op : arith) { // Loop through the arithmetic operations
            if (cell.contains(op)) {
                return true; // Return true if arithmetic operation is found
            }
        }

        return false; // Return false if no arithmetic operation is found
    }

    /**
     * Checks if the cell contains an operation.
     *
     * @param cell the cell content.
     * @return the operation if present, otherwise an empty string.
     * @author Theo
     */
    private String getOperation(String cell) {
        for (String op : operations) { // Loop through the logical operations
            if (cell.contains(op)) {
                return op; // Return the operation if found
            }
        }

        return ""; // Return empty string if no operation is found
    }

    /**
     * Compares if the first value is less than the second value.
     *
     * @param x the first value.
     * @param y the second value.
     * @return "1" if x is less than y, otherwise "0".
     * @author Vinay
     */
    private String compareLess(String x, String y) {
        x = replaceCellReferences(x); // Replace cell references in x
        y = replaceCellReferences(y); // Replace cell references in y
        try {
            double a = Double.parseDouble(x); // Parse x as double
            double b = Double.parseDouble(y); // Parse y as double
            return a < b ? "1" : "0"; // Return "1" if a is less than b, otherwise "0"
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }
    }

    /**
     * Compares if the first value is greater than the second value.
     *
     * @param x the first value.
     * @param y the second value.
     * @return "1" if x is greater than y, otherwise "0".
     * @author Vinay
     */
    private String compareGreater(String x, String y) {
        x = replaceCellReferences(x); // Replace cell references in x
        y = replaceCellReferences(y); // Replace cell references in y
        try {
            double a = Double.parseDouble(x); // Parse x as double
            double b = Double.parseDouble(y); // Parse y as double
            return a > b ? "1" : "0"; // Return "1" if a is greater than b, otherwise "0"
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }
    }

    /**
     * Compares if the first value is equal to the second value.
     *
     * @param x the first value.
     * @param y the second value.
     * @return "1" if x is equal to y, otherwise "0".
     * @author Vinay
     */
    private String compareEqual(String x, String y) {
        x = replaceCellReferences(x); // Replace cell references in x
        y = replaceCellReferences(y); // Replace cell references in y
        try {
            double a = Double.parseDouble(x); // Parse x as double
            double b = Double.parseDouble(y); // Parse y as double
            return a == b ? "1" : "0"; // Return "1" if a is equal to b, otherwise "0"
        } catch (NumberFormatException e) {
            if (x.equals(y)) {
                return "1"; // Return "1" if x equals y as strings
            } else {
                return "0"; // Return "0" if x does not equal y as strings
            }
        }
    }

    /**
     * Compares if the first value is not equal to the second value.
     *
     * @param x the first value.
     * @param y the second value.
     * @return "1" if x is not equal to y, otherwise "0".
     * @author Theo
     */
    private String compareNotEqual(String x, String y) {
        x = replaceCellReferences(x); // Replace cell references in x
        y = replaceCellReferences(y); // Replace cell references in y
        try {
            double a = Double.parseDouble(x); // Parse x as double
            double b = Double.parseDouble(y); // Parse y as double
            return a != b ? "1" : "0"; // Return "1" if a is not equal to b, otherwise "0"
        } catch (NumberFormatException e) {
            if (!x.equals(y)) {
                return "1"; // Return "1" if x does not equal y as strings
            } else {
                return "0"; // Return "0" if x equals y as strings
            }
        }
    }

    /**
     * Performs the AND operation between two values.
     *
     * @param x the first value.
     * @param y the second value.
     * @return "1" if both values are non-zero, otherwise "0".
     * @author Vinay
     */
    private String andOperation(String x, String y) {
        x = replaceCellReferences(x); // Replace cell references in x
        y = replaceCellReferences(y); // Replace cell references in y
        try {
            double a = Double.parseDouble(x); // Parse x as double
            double b = Double.parseDouble(y); // Parse y as double
            return (a != 0 && b != 0) ? "1" : "0"; // Return "1" if both a and b are non-zero, otherwise "0"
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }
    }

    /**
     * Performs the OR operation between two values.
     *
     * @param x the first value.
     * @param y the second value.
     * @return "1" if either value is non-zero, otherwise "0".
     * @author Vinay
     */
    private String orOperation(String x, String y) {
        x = replaceCellReferences(x); // Replace cell references in x
        y = replaceCellReferences(y); // Replace cell references in y
        try {
            double a = Double.parseDouble(x); // Parse x as double
            double b = Double.parseDouble(y); // Parse y as double
            return (a != 0 || b != 0) ? "1" : "0"; // Return "1" if either a or b is non-zero, otherwise "0"
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }
    }

    /**
     * Performs a range operation between two cells.
     *
     * @param startCell the start cell.
     * @param endCell   the end cell.
     * @return the result of the range operation.
     * @author Theo
     */
    private String rangeOperation(String startCell, String endCell) {
        String func = getFunction(startCell); // Get function from startCell
        if (func != "") {
            startCell = startCell.substring(startCell.indexOf('(', startCell.indexOf(func)) + 1); // Remove function part from startCell
        }

        int startRow = getRow(startCell); // Get start row
        int endRow = getRow(endCell); // Get end row
        int startCol = getColumn(startCell); // Get start column
        int endCol = getColumn(endCell); // Get end column

        // Check if the range is valid
        if (startRow > endRow || startCol > endCol || startRow == -1 || endRow == -1 || startCol == -1
                || endCol == -1) {
            return "Error"; // Return "Error" if range is invalid
        }

        // Append the cells' values
        StringBuilder rangeResult = new StringBuilder(); // Initialize range result
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                String cellValue = getCellValue(row, col); // Get cell value
                if (!cellValue.isEmpty()) {
                    rangeResult.append(cellValue).append(","); // Append cell value to range result
                }
            }
        }

        if (rangeResult.length() == 0) {
            return ""; // Return empty string if range result is empty
        }

        String result = rangeResult.substring(0, rangeResult.length() - 1); // Remove trailing comma
        // Add function if needed
        if (func != "") {
            result = func + "(" + result + ")"; // Add function to result
            return parseOperations(result); // Parse operations in result
        }
        return result; // Return range result
    }

    /**
     * Evaluates the IF function with the given parameters.
     *
     * @param parameters the parameters for the IF function.
     * @return the result of the IF function.
     * @author Vinay
     */
    private String evaluateIF(String parameters) {
        String[] parts = parameters.split(","); // Split parameters
        if (parts.length != 3) {
            return "Error"; // Return "Error" if parameter count is not 3
        }

        String condition = replaceCellReferences(parts[0].trim()); // Replace cell references in condition
        String trueResult = replaceCellReferences(parts[1].trim()); // Replace cell references in trueResult
        String falseResult = replaceCellReferences(parts[2].trim()); // Replace cell references in falseResult

        try {
            double conditionValue = Double.parseDouble(condition); // Parse condition as double
            return conditionValue != 0 ? trueResult : falseResult; // Return trueResult if condition is non-zero, otherwise falseResult
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }
    }

    /**
     * Evaluates the SUM function with the given parameters.
     *
     * @param parameters the parameters for the SUM function.
     * @return the result of the SUM function.
     * @author Theo
     */
    private String evaluateSUM(String parameters) {
        String[] parts = parameters.split(","); // Split parameters
        System.out.println("Formula SUM: " + parameters);
        double sum = 0;
        try {
            for (String part : parts) {
                String func = getFunction(part); // Get function from part
                part = replaceCellReferences(part.trim());
                part = parseNestedOperations(func, part, parameters); // Parse nested operations
                if (part != "") {
                    sum += Double.parseDouble(part); // Add part value to sum
                    int index = parameters.indexOf(")"); // Find index of closing parenthesis
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1); // Update parameters
                    }
                }
            }
            return String.valueOf(sum); // Return sum as string
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }
    }

    /**
     * Evaluates the MIN function with the given parameters.
     *
     * @param parameters the parameters for the MIN function.
     * @return the result of the MIN function.
     * @author Vinay
     */
    private String evaluateMIN(String parameters) {
        String[] parts = parameters.split(","); // Split parameters
        double min = Double.MAX_VALUE;
        try {
            for (String part : parts) {
                String func = getFunction(part); // Get function from part
                part = replaceCellReferences(part.trim());
                part = parseNestedOperations(func, part, parameters); // Parse nested operations
                if (part != "") {
                    double value = Double.parseDouble(part); // Parse part as double
                    int index = parameters.indexOf(")"); // Find index of closing parenthesis
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1); // Update parameters
                    }
                    if (value < min) {
                        min = value; // Update min if value is smaller
                    }
                }
            }
            return String.valueOf(min); // Return min as string
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }
    }

    /**
     * Evaluates the MAX function with the given parameters.
     *
     * @param parameters the parameters for the MAX function.
     * @return the result of the MAX function.
     * @author Vinay
     */
    private String evaluateMAX(String parameters) {
        String[] parts = parameters.split(","); // Split parameters
        double max = Double.MIN_VALUE;
        try {
            for (String part : parts) {
                String func = getFunction(part); // Get function from part
                part = replaceCellReferences(part.trim());
                part = parseNestedOperations(func, part, parameters); // Parse nested operations
                if (part != "") {
                    double value = Double.parseDouble(part); // Parse part as double
                    int index = parameters.indexOf(")"); // Find index of closing parenthesis
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1); // Update parameters
                    }

                    if (value > max) {
                        max = value; // Update max if value is larger
                    }
                }
            }
            return String.valueOf(max); // Return max as string
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }
    }

    /**
     * Evaluates the AVG function with the given parameters.
     *
     * @param parameters the parameters for the AVG function.
     * @return the result of the AVG function.
     * @author Vinay
     */
    private String evaluateAVG(String parameters) {
        String[] parts = parameters.split(","); // Split parameters
        double sum = 0;
        try {
            for (String part : parts) {
                String func = getFunction(part); // Get function from part
                part = replaceCellReferences(part.trim());
                part = parseNestedOperations(func, part, parameters); // Parse nested operations
                if (part != "") {
                    sum += Double.parseDouble(part); // Add part value to sum
                    int index = parameters.indexOf(")"); // Find index of closing parenthesis
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1); // Update parameters
                    }
                }
            }
            return String.valueOf(sum / parts.length); // Return average as string
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }
    }

    /**
     * Evaluates the CONCAT function with the given parameters.
     *
     * @param parameters the parameters for the CONCAT function.
     * @return the result of the CONCAT function.
     * @author Theo
     */
    private String evaluateCONCAT(String parameters) {
        String[] parts = parameters.split(","); // Split parameters
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            // Remove double quotes from around the string parts
            String trimmedPart = part.trim();
            if (trimmedPart.startsWith("\"") && trimmedPart.endsWith("\"")) {
                trimmedPart = trimmedPart.substring(1, trimmedPart.length() - 1); // Remove quotes
            }
            result.append(trimmedPart); // Append part to result
        }
        return result.toString(); // Return concatenated string
    }

    /**
     * Evaluates the DEBUG function with the given parameter.
     *
     * @param parameter the parameter for the DEBUG function.
     * @return the result of the DEBUG function.
     * @author Theo
     */
    private String evaluateDEBUG(String parameter) {
        return parameter.trim(); // Return trimmed parameter
    }

    /**
     * Evaluates the STDDEV function with the given parameter.
     *
     * @param parameters the parameter for the STDDEV function.
     * @return the result of the STDDEV function.
     * @author Theo
     */
    private String evaluateSTDDEV(String parameters) {
        String[] parts = parameters.split(","); // Split parameters
        double sum = 0;

        try {
            double avg = Double.parseDouble(evaluateAVG(parameters)); // Calculate average
            for (String part : parts) {
                String func = getFunction(part); // Get function from part
                part = replaceCellReferences(part.trim());
                part = parseNestedOperations(func, part, parameters); // Parse nested operations
                if (part != "") {
                    sum += Math.pow(Double.parseDouble(replaceCellReferences(part)) - avg, 2); // Calculate squared difference
                    int index = parameters.indexOf(")"); // Find index of closing parenthesis
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1); // Update parameters
                    }
                }
            }
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }

        double result = Math.pow(sum / parts.length, 0.5); // Calculate standard deviation
        return "" + (double) Math.round(result * 1000) / 1000; // Return rounded result
    }

    /**
     * Evaluates the SORT function with the given parameter.
     *
     * @param parameters the parameter for the SORT function.
     * @return the result of the SORT function.
     * @author Theo
     */
    private String evaluateSORT(String parameters) {
        String[] parts = parameters.split(","); // Split parameters
        double[] nums = new double[parts.length];
        try {
            for (int i = 0; i < nums.length; i++) {
                String part = parts[i];
                String func = getFunction(part); // Get function from part
                part = replaceCellReferences(part.trim());
                part = parseNestedOperations(func, part, parameters); // Parse nested operations
                if (part != "") {
                    nums[i] = Double.parseDouble(part); // Parse part as double
                    int index = parameters.indexOf(")"); // Find index of closing parenthesis
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1); // Update parameters
                    }
                }
            }
        } catch (NumberFormatException e) {
            return "Error"; // Return "Error" if parsing fails
        }

        Arrays.sort(nums); // Sort numbers
        StringBuilder result = new StringBuilder();

        for (double num : nums) {
            result.append(num).append(","); // Append sorted numbers to result
        }

        return result.substring(0, result.length() - 1); // Return sorted numbers as string
    }

    /**
     * Parses and evaluated any nested functions within the formula parameters.
     *
     * @param func       the nested function within the given part.
     * @param part       the section of the parameters being evaluated
     * @param parameters the entire parameter.
     * @return the result of the nested function, or an empty string for the ends of functions.
     * @author Theo
     */
    private String parseNestedOperations(String func, String part, String parameters) {
        // see if there is a nested function to be evaluated
        if (func != "" && part.contains("(")) {
            int index = parameters.indexOf("(", parameters.indexOf(part));
            // parse the nested parameters
            String nestedParameters = "";
            while (!nestedParameters.contains(")")) {
                nestedParameters += parameters.charAt(index);
                index += 1;
            }
            // evaluate the function
            func += nestedParameters;
            part = parseOperations(func); // Parse nested function
            return part;
            // for the ends of already evaluated functions
        } else if (!part.contains("(") && part.contains(")")) {
            return ""; // Return empty string for ends of functions
            // for parts that don't need evaluation
        } else {
            return part; // Return part if no evaluation is needed
        }
    }
}
