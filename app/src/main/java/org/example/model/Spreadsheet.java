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

    private List<List<Cell>> grid;

    private String name;
    private int id_version;
    // used to retrieve version for GetUpdatesForSubscription
    private List<ISpreadsheet> publishVersions;

    // used to retrieve version for GetUpdatesPublished
    private List<ISpreadsheet> subscribeVersions;

    private String[] functions = new String[] { "IF", "SUM", "MIN", "MAX", "AVG", "CONCAT", "DEBUG", "STDDEV", "SORT" };
    private String[] arith = new String[] { "+", "-", "*", "/" };
    private String[] operations = new String[] { "<>", "<", ">", "=", "&", "|", ":" };

    /**
     * Constructs a new Spreadsheet with the specified name.
     *
     * @param name the name of the spreadsheet.
     */
    public Spreadsheet(String name) {
        grid = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < 100; j++) {
                row.add(new Cell(""));
            }
            grid.add(row);
        }

        this.name = name;
        this.publishVersions = new ArrayList<>();
        this.subscribeVersions = new ArrayList<>();

    }

    /**
     * Constructs a new Spreadsheet with the specified grid and name.
     *
     * @param grid the grid of cells.
     * @param name the name of the spreadsheet.
     */
    public Spreadsheet(List<List<Cell>> grid, String name) {
        this(name);
        for (int i = 0; i < 100; i++) {
            List<Cell> row = grid.get(i);
            for (int j = 0; j < 100; j++) {
                Cell cell = row.get(j);
                if (!cell.getValue().isEmpty()) {
                    this.grid.get(i).set(j, cell);
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
        StringBuilder payload = new StringBuilder();
        Cell[][] values = sheet.getCellsObject();
        for (int i = 0; i < sheet.getRows(); i++) {
            for (int j = 0; j < sheet.getCols(); j++) {
                if (values[i][j] != null && !values[i][j].getRawdata().isEmpty()) {
                    String cellValue = values[i][j].isFormula() ? values[i][j].getFormula() : values[i][j].getRawdata();
                    payload.append(String.format("$%s%s %s\\n", getColumnName(j + 1), i + 1, cellValue));
                }
            }
        }
        return payload.toString();
    }

    /**
     * Gets the column label using the given column number.
     * 
     * @param columnNumber a number that corresponds to a column in the spreadsheet
     * @return a column label (e.g A, D, F, G)
     */
    public static String getColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder();
        while (columnNumber > 0) {
            int remainder = (columnNumber - 1) % 26;
            columnName.insert(0, (char) (remainder + 'A'));
            columnNumber = (columnNumber - 1) / 26;
        }
        return columnName.toString();
    }

    public int getRows() {
        return this.grid.size();
    }

    /**
     * Gets the number of columns in the spreadsheet.
     *
     * @return the number of columns.
     */
    public int getCols() {
        return this.grid.get(0).size();
    }

    /**
     * Gets the grid of cells in the spreadsheet.
     *
     * @return the grid of cells.
     */
    public List<List<Cell>> getCells() {
        return this.grid;
    }

    /**
     * Gets the grid of cells as a 2D array of Cell objects.
     *
     * @return the 2D array of Cell objects.
     */
    public Cell[][] getCellsObject() {
        Cell[][] retObject = new Cell[this.getRows()][this.getCols()];
        for (int r = 0; r < this.getRows(); r++) {
            List<Cell> row = this.grid.get(r);
            for (int c = 0; c < this.getCols(); c++) {
                retObject[r][c] = row.get(c);
            }
        }
        return retObject;
    }

    /**
     * Gets the grid of cell values as a 2D array of strings.
     *
     * @return the 2D array of cell values.
     */
    public String[][] getCellStringsObject() {
        String[][] retObject = new String[this.getRows()][this.getCols()];
        for (int r = 0; r < this.getRows(); r++) {
            List<Cell> row = this.grid.get(r);
            for (int c = 0; c < this.getCols(); c++) {
                retObject[r][c] = row.get(c).getValue();
            }
        }
        return retObject;
    }

    /**
     * Gets the row index from the cell reference.
     *
     * @param cell the cell reference.
     * @return the row index.
     * @author Theo
     */
    public int getRow(String cell) {
        try {
            return Integer.parseInt(cell.replaceAll("[^0-9]", "")) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets the column index from the cell reference.
     *
     * @param cell the cell reference.
     * @return the column index.
     * @author Vinay
     */
    public int getColumn(String cell) {
        String col = cell.replaceAll("[^A-Z]", "").toUpperCase();
        int column = 0;
        for (int i = 0; i < col.length(); i++) {
            column = column * 26 + (col.charAt(i) - 'A' + 1);
        }
        return column - 1;
    }

    /**
     * Adds a published version of the spreadsheet.
     *
     * @param sheet the published version of the spreadsheet.
     */
    public void addPublished(ISpreadsheet sheet) {
        this.publishVersions.add(sheet);
        this.id_version++;
    }

    /**
     * Adds a subscribed version of the spreadsheet.
     *
     * @param sheet the subscribed version of the spreadsheet.
     */
    public void addSubscribed(ISpreadsheet sheet) {
        this.subscribeVersions.add(sheet);
    }

    /**
     * Gets the list of published versions of the spreadsheet.
     *
     * @return the list of published versions.
     */
    public List<ISpreadsheet> getPublishedVersions() {
        return this.publishVersions;
    }

    /**
     * Gets the list of subscribed modified versions of the spreadsheet
     * 
     * @return a list of subscribed modified versions of the spreadsheet
     */
    public List<ISpreadsheet> getSubscribedVersions() {
        return this.subscribeVersions;
    }

    @Override
    public void setGrid(List<List<Cell>> updatedGrid) {
        this.grid = updatedGrid;
    }

    public List<List<Cell>> getGrid() {
        return this.grid;
    }
    

    /**
     * Gets the name of the spreadsheet.
     *
     * @return the name of the spreadsheet.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the id of current sheet
     * 
     * @return the id of the sheet
     */
    public int getId_version() {
        return this.id_version;
    }

    /**
     * Sets the value of the cell at the specified row and column.
     *
     * @param row   the row index of the cell.
     * @param col   the column index of the cell.
     * @param value the value to set.
     */
    @Override
    public void setCellValue(int row, int col, String value) {
        this.grid.get(row).get(col).setValue(evaluateFormula(value));
    }

    /**
     * Gets the value of the cell at the specified row and column.
     *
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @return the value of the cell.
     */
    @Override
    public String getCellValue(int row, int col) {
        return this.grid.get(row).get(col).getValue();
    }

    /**
     * Sets the raw data of the cell at the specified row and column.
     *
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @param val the raw data to set.
     */
    @Override
    public void setCellRawdata(int row, int col, String val) {
        this.grid.get(row).get(col).setRawData(val);
    }

    /**
     * Gets the raw data of the cell at the specified row and column.
     *
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @return the raw data of the cell.
     */
    @Override
    public String getCellRawdata(int row, int col) {
        return this.grid.get(row).get(col).getRawdata();
    }

    /**
     * Evaluates the given formula and returns the result.
     *
     * @param formula the formula to evaluate.
     * @return the result of evaluating the formula.
     */
    @Override
    public String evaluateFormula(String formula) {
        System.out.println(formula);
        if (!formula.startsWith("=")) {
            return formula;
        }

        // Remove the initial "="
        formula = formula.substring(1).stripLeading();

        try {
            boolean evaluated = false;
            Object result = formula;
            // Handle operations that need to be parsed before evaluation
            formula = parseOperations(formula);
            // Replace cell references with their values
            formula = replaceCellReferences(formula);
            System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
            // For simplicity, handle basic arithmetic operations using JavaScript engine
            if (!((String) result).equals(formula)) {
                result = formula;
                evaluated = true;
            }
            if (containsArith((String) result) && !evaluated) {
                Context context = Context.create("js");
                result = context.eval("js", formula);
            }
            return result.toString();
        } catch (Exception e) {
            return "Error";
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
        // Handle operations
        String operation = getOperation(formula);
        if (operation != "") {
            String[] parts = formula.replaceAll(" ", "").split(operation);

            if (formula.contains("<>")) {
                return compareNotEqual(parts[0].trim(), parts[1].trim());
            } else if (formula.contains("<") && !formula.contains("=")) {
                return compareLess(parts[0].trim(), parts[1].trim());
            } else if (formula.contains(">") && !formula.contains("=")) {
                return compareGreater(parts[0].trim(), parts[1].trim());
            } else if (formula.contains("=") && !formula.contains("<") && !formula.contains(">")) {
                return compareEqual(parts[0].trim(), parts[1].trim());
            } else if (formula.contains("&")) {
                return andOperation(parts[0].trim(), parts[1].trim());
            } else if (formula.contains("|")) {
                return orOperation(parts[0].trim(), parts[2].trim());
            } else if (formula.contains(":")) {
                return rangeOperation(parts[0].trim(), parts[1].trim());
            }
        }

        // Handle functions
        String function = getFunction(formula);
        if (function != "") {
            int start = function.length() + 1;
            int end = formula.length() - 1;
            String args = formula.substring(start, end);

            if (formula.startsWith("IF(")) {
                return evaluateIF(args);
            } else if (formula.startsWith("SUM(")) {
                return evaluateSUM(args);
            } else if (formula.startsWith("MIN(")) {
                return evaluateMIN(args);
            } else if (formula.startsWith("MAX(")) {
                return evaluateMAX(args);
            } else if (formula.startsWith("AVG(")) {
                return evaluateAVG(args);
            } else if (formula.startsWith("CONCAT(")) {
                return evaluateCONCAT(args);
            } else if (formula.startsWith("DEBUG(")) {
                return evaluateDEBUG(args);
            } else if (formula.startsWith("STDDEV(")) {
                return evaluateSTDDEV(args);
            } else if (formula.startsWith("SORT(")) {
                return evaluateSORT(args);
            }
        }

        return formula;
    }

    /**
     * Replaces cell references in the formula with their actual values.
     *
     * @param formula the formula with cell references.
     * @return the formula with cell references replaced by values.
     * @author Vinay
     */
    private String replaceCellReferences(String formula) {
        Pattern pattern = Pattern.compile("\\$[A-Z]+[0-9]+");
        Matcher matcher = pattern.matcher(formula);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String cellReference = matcher.group();
            int row = getRow(cellReference);
            int col = getColumn(cellReference);
            String cellValue = getCellValue(row, col);
            matcher.appendReplacement(result, cellValue);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Checks if the cell contains a function.
     *
     * @param cell the cell content.
     * @return the function if present, otherwise an empty string.
     * @author Theo
     */
    private String getFunction(String cell) {
        for (String func : functions) {
            if (cell.contains(func)) {
                return func;
            }
        }

        return "";
    }

    /**
     * Checks if the cell contains a basic arithmetic operation.
     *
     * @param cell the cell content.
     * @return true if it contains an arithmetic operation, otherwise false.
     * @author Vinay
     */
    private boolean containsArith(String cell) {
        for (String op : arith) {
            if (cell.contains(op)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the cell contains an operation.
     *
     * @param cell the cell content.
     * @return the operation if present, otherwise an empty string.
     * @author Theo
     */
    private String getOperation(String cell) {
        for (String op : operations) {
            if (cell.contains(op)) {
                return op;
            }
        }

        return "";
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
        x = replaceCellReferences(x);
        y = replaceCellReferences(y);
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a < b ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
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
        x = replaceCellReferences(x);
        y = replaceCellReferences(y);
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a > b ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
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
        x = replaceCellReferences(x);
        y = replaceCellReferences(y);
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a == b ? "1" : "0";
        } catch (NumberFormatException e) {
            if (x.equals(y)) {
                return "1";
            } else {
                return "0";
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
        x = replaceCellReferences(x);
        y = replaceCellReferences(y);
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a != b ? "1" : "0";
        } catch (NumberFormatException e) {
            if (!x.equals(y)) {
                return "1";
            } else {
                return "0";
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
        x = replaceCellReferences(x);
        y = replaceCellReferences(y);
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return (a != 0 && b != 0) ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
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
        x = replaceCellReferences(x);
        y = replaceCellReferences(y);
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return (a != 0 || b != 0) ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
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
        // Check if this cell has a function value
        String func = getFunction(startCell);
        if (func != "") {
            startCell = startCell.substring(startCell.indexOf('(') + 1);
        }

        int startRow = getRow(startCell);
        int endRow = getRow(endCell);
        int startCol = getColumn(startCell);
        int endCol = getColumn(endCell);

        // Check if the range is valid
        if (startRow > endRow || startCol > endCol || startRow == -1 || endRow == -1 || startCol == -1
                || endCol == -1) {
            return "Error";
        }

        // Append the cells' values
        StringBuilder rangeResult = new StringBuilder();
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                String cellValue = getCellValue(row, col);
                if (!cellValue.isEmpty()) {
                    rangeResult.append(cellValue).append(",");
                }
            }
        }

        if (rangeResult.length() == 0) {
            return "";
        }

        String result = rangeResult.substring(0, rangeResult.length() - 1);
        // Add function if needed
        if (func != "") {
            result = func + "(" + result + ")";
            return parseOperations(result);
        }
        return result;
    }

    /**
     * Evaluates the IF function with the given parameters.
     *
     * @param parameters the parameters for the IF function.
     * @return the result of the IF function.
     * @author Theo
     */
    private String evaluateIF(String parameters) {
        String[] parts = parameters.split(",");
        if (parts.length != 3) {
            return "Error";
        }

        String condition = replaceCellReferences(parts[0].trim());
        String trueResult = replaceCellReferences(parts[1].trim());
        String falseResult = replaceCellReferences(parts[2].trim());

        try {
            double conditionValue = Double.parseDouble(condition);
            return conditionValue != 0 ? trueResult : falseResult;
        } catch (NumberFormatException e) {
            return "Error";
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
        String[] parts = parameters.split(",");
        System.out.println("Formula SUM: " + parameters);
        double sum = 0;
        try {
            for (String part : parts) {
                // check for any nested functions
                String func = getFunction(part);
                part = parseNestedOperations(func, part, parameters);
                if (part != "") {
                    sum += Double.parseDouble(replaceCellReferences(part.trim()));
                    // shift to next parameters
                    int index = parameters.indexOf(")");
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1);
                    }
                }
            }
            return String.valueOf(sum);
        } catch (NumberFormatException e) {
            return "Error";
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
        String[] parts = parameters.split(",");
        double min = Double.MAX_VALUE;
        try {
            for (String part : parts) {
                // check for any nested functions
                String func = getFunction(part);
                part = parseNestedOperations(func, part, parameters);
                if (part != "") {
                    double value = Double.parseDouble(replaceCellReferences(part.trim()));
                    // shift to next parameters
                    int index = parameters.indexOf(")");
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1);
                    }

                    if (value < min) {
                        min = value;
                    }
                }
            }
            return String.valueOf(min);
        } catch (NumberFormatException e) {
            return "Error";
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
        String[] parts = parameters.split(",");
        double max = Double.MIN_VALUE;
        try {
            for (String part : parts) {
                // check for any nested functions
                String func = getFunction(part);
                part = parseNestedOperations(func, part, parameters);
                if (part != "") {
                    double value = Double.parseDouble(replaceCellReferences(part.trim()));
                    // shift to next parameters
                    int index = parameters.indexOf(")");
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1);
                    }

                    if (value > max) {
                        max = value;
                    }
                }
            }
            return String.valueOf(max);
        } catch (NumberFormatException e) {
            return "Error";
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
        String[] parts = parameters.split(",");
        double sum = 0;
        try {
            for (String part : parts) {
                // check for any nested functions
                String func = getFunction(part);
                part = parseNestedOperations(func, part, parameters);
                if (part != "") {
                    sum += Double.parseDouble(replaceCellReferences(part.trim()));
                    // shift to next parameters
                    int index = parameters.indexOf(")");
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1);
                    }
                }
            }
            return String.valueOf(sum / parts.length);
        } catch (NumberFormatException e) {
            return "Error";
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
        String[] parts = parameters.split(",");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            // Remove double quotes from around the string parts
            String trimmedPart = part.trim();
            if (trimmedPart.startsWith("\"") && trimmedPart.endsWith("\"")) {
                trimmedPart = trimmedPart.substring(1, trimmedPart.length() - 1);
            }
            result.append(trimmedPart);
        }
        return result.toString();
    }

    /**
     * Evaluates the DEBUG function with the given parameter.
     *
     * @param parameter the parameter for the DEBUG function.
     * @return the result of the DEBUG function.
     * @author Theo
     */
    private String evaluateDEBUG(String parameter) {
        return parameter.trim();
    }

    /**
     * Evaluates the STDDEV function with the given parameter.
     *
     * @param parameters the parameter for the STDDEV function.
     * @return the result of the STDDEV function.
     * @author Theo
     */
    private String evaluateSTDDEV(String parameters) {
        String[] parts = parameters.split(",");
        double sum = 0;

        try {
            double avg = Double.parseDouble(evaluateAVG(parameters));
            for (String part : parts) {
                // check for any nested functions
                String func = getFunction(part);
                part = parseNestedOperations(func, part, parameters);
                if (part != "") {
                    sum += Math.pow(Double.parseDouble(replaceCellReferences(part)) - avg, 2);
                    // shift to next parameters
                    int index = parameters.indexOf(")");
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1);
                    }
                }
            }
        } catch (NumberFormatException e) {
            return "Error";
        }

        double result = Math.pow(sum / parts.length, 0.5);
        return "" + (double) Math.round(result * 1000) / 1000;
    }

    /**
     * Evaluates the SORT function with the given parameter.
     *
     * @param parameters the parameter for the SORT function.
     * @return the result of the SORT function.
     * @author Theo
     */
    private String evaluateSORT(String parameters) {
        String[] parts = parameters.split(",");
        double[] nums = new double[parts.length];
        try {
            for (int i = 0; i < nums.length; i++) {
                String part = parts[i];
                // check for any nested functions
                String func = getFunction(part);
                part = parseNestedOperations(func, part, parameters);
                if (part != "") {
                    nums[i] = Double.parseDouble(replaceCellReferences(part));
                    // shift to next parameters
                    int index = parameters.indexOf(")");
                    if (func != "" && index != -1) {
                        parameters = parameters.substring(index + 1);
                    }
                }
            }
        } catch (NumberFormatException e) {
            return "Error";
        }

        Arrays.sort(nums);
        StringBuilder result = new StringBuilder();

        for (double num : nums) {
            result.append(num).append(",");
        }

        return result.substring(0, result.length() - 1);
    }

    /**
     * Parses and evaluated any nested functions within the formula parameters.
     *
     * @param func the nested function within the given part.
     * @param part the section of the parameters being evaluated
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
            part = parseOperations(func);
            return part;
        // for the ends of already evaluated functions
        } else if (!part.contains("(") && part.contains(")")) {
            return "";
        // for parts that don't need evaluation
        } else {
            return part;
        }
    }
}
