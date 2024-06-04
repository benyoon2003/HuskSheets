package org.example.model;

import org.example.controller.UserController;

import java.util.ArrayList;

import java.util.List;

import java.util.Arrays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Represents a spreadsheet with various functionalities such as evaluating formulas,
 * managing cells, and handling subscriptions and publications.
 */

public class Spreadsheet implements ISpreadsheet {
    private ArrayList<ArrayList<Cell>> grid;

    private String name;

    // used to retrieve version for GetUpdatesForSubscription
    private List<ISpreadsheet> publishVersions;

    // used to retrieve version for GetUpdatesPublished
    private List<ISpreadsheet> subscribeVersions;

    private String[] functions = new String[] { "IF", "SUM", "MIN", "MAX", "AVG", "CONCAT", "DEBUG", "STDDEV", "SORT" };
    private String[] arith = new String[] { "+", "-", "*", "/" };

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
    public Spreadsheet(ArrayList<ArrayList<Cell>> grid, String name) {
        this(name);
        this.grid = grid;
    }

    /**
     * Gets the number of rows in the spreadsheet.
     *
     * @return the number of rows.
     */
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
    public ArrayList<ArrayList<Cell>> getCells() {
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
            ArrayList<Cell> row = this.grid.get(r);
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
            ArrayList<Cell> row = this.grid.get(r);
            for (int c = 0; c < this.getCols(); c++) {
                retObject[r][c] = row.get(c).getValue();
            }
        }
        return retObject;
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
        formula = formula.substring(1);

        try {
            if (formula.contains("SORT")) {
                return sort(formula);
            }
            // Handle operations that need to be parsed before evaluation
            formula = parseOperations(formula);
            // Replace cell references with their values
            formula = replaceCellReferences(formula);

            // For simplicity, handle basic arithmetic operations using JavaScript engine
            Object result = formula;
            if (containsArith((String) result)) {
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                result = engine.eval(formula);
            }
            return result.toString();
        } catch (ScriptException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    /**
     * Replaces cell references in the formula with their actual values.
     *
     * @param formula the formula with cell references.
     * @return the formula with cell references replaced by values.
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
     * Gets the row index from the cell reference.
     *
     * @param cell the cell reference.
     * @return the row index.
     */
    private int getRow(String cell) {
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
     */
    private int getColumn(String cell) {
        String col = cell.replaceAll("[^A-Z]", "").toUpperCase();
        int column = 0;
        for (int i = 0; i < col.length(); i++) {
            column = column * 26 + (col.charAt(i) - 'A' + 1);
        }
        return column - 1;
    }

    /**
     * Checks if the cell contains a function.
     *
     * @param cell the cell content.
     * @return the function if present, otherwise an empty string.
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
     * Adds a published version of the spreadsheet.
     *
     * @param sheet the published version of the spreadsheet.
     */
    public void addPublished(ISpreadsheet sheet) {
        this.publishVersions.add(sheet);
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
     * Sets the value of the cell at the specified row and column.
     *
     * @param row   the row index of the cell.
     * @param col   the column index of the cell.
     * @param value the value to set.
     */
    @Override
    public void setCellValue(int row, int col, String value) {
        this.grid.get(row).get(col).setValue(value);
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
     * Gets the name of the spreadsheet.
     *
     * @return the name of the spreadsheet.
     */
    @Override
    public String getName() {
        return this.name;
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
     * Gets the formula of the cell at the specified row and column.
     *
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @return the formula of the cell.
     */
    @Override
    public String getCellFormula(int row, int col) {
        return this.grid.get(row).get(col).getFormula();
    }

    /**
     * Parses and handles various operations in the formula.
     *
     * @param formula the formula to parse.
     * @return the result of parsing the formula.
     */
    private String parseOperations(String formula) {
        if (formula.contains("<>")) {
            String[] parts = formula.split("<>");
            return compareNotEqual(parts[0].trim(), parts[1].trim());
        } else if (formula.contains("<") && !formula.contains("=")) {
            String[] parts = formula.split("<");
            return compareLess(parts[0].trim(), parts[1].trim());
        } else if (formula.contains(">") && !formula.contains("=")) {
            String[] parts = formula.split(">");
            return compareGreater(parts[0].trim(), parts[1].trim());
        } else if (formula.contains("=") && !formula.contains("<") && !formula.contains(">")) {
            String[] parts = formula.split("=");
            return compareEqual(parts[0].trim(), parts[1].trim());
        } else if (formula.contains("&")) {
            String[] parts = formula.split("&");
            return andOperation(parts[0].trim(), parts[1].trim());
        } else if (formula.contains("|")) {
            String[] parts = formula.split("\\|");
            return orOperation(parts[0].trim(), parts[1].trim());
        } else if (formula.contains(":")) {
            String[] parts = formula.split(":");
            return rangeOperation(parts[0].trim(), parts[1].trim());
        } else if (formula.startsWith("IF(")) {
            return evaluateIF(formula.substring(3, formula.length() - 1));
        } else if (formula.startsWith("SUM(")) {
            return evaluateSUM(formula.substring(4, formula.length() - 1));
        } else if (formula.startsWith("MIN(")) {
            return evaluateMIN(formula.substring(4, formula.length() - 1));
        } else if (formula.startsWith("MAX(")) {
            return evaluateMAX(formula.substring(4, formula.length() - 1));
        } else if (formula.startsWith("AVG(")) {
            return evaluateAVG(formula.substring(4, formula.length() - 1));
        } else if (formula.startsWith("CONCAT(")) {
            return evaluateCONCAT(formula.substring(7, formula.length() - 1));
        } else if (formula.startsWith("DEBUG(")) {
            return evaluateDEBUG(formula.substring(6, formula.length() - 1));
        } else if (formula.startsWith("STDDEV(")) {
            return evaluateSTDDEV(formula.substring(7, formula.length() - 1));
        } else if (formula.startsWith("SORT(")) {
            return evaluateSORT(formula.substring(5, formula.length() - 1));
        }

        return formula;
    }

    /**
     * Compares if the first value is less than the second value.
     *
     * @param x the first value.
     * @param y the second value.
     * @return "1" if x is less than y, otherwise "0".
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
     */
    private String rangeOperation(String startCell, String endCell) {
        // check if this cell has a function value
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
     */
    private String evaluateSUM(String parameters) {
        String[] parts = parameters.split(",");
        double sum = 0;
        try {
            for (String part : parts) {
                sum += Double.parseDouble(replaceCellReferences(part.trim()));
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
     */
    private String evaluateMIN(String parameters) {
        String[] parts = parameters.split(",");
        double min = Double.MAX_VALUE;
        try {
            for (String part : parts) {
                double value = Double.parseDouble(replaceCellReferences(part.trim()));
                if (value < min) {
                    min = value;
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
     */
    private String evaluateMAX(String parameters) {
        String[] parts = parameters.split(",");
        double max = Double.MIN_VALUE;
        try {
            for (String part : parts) {
                double value = Double.parseDouble(replaceCellReferences(part.trim()));
                if (value > max) {
                    max = value;
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
     */
    private String evaluateAVG(String parameters) {
        String[] parts = parameters.split(",");
        double sum = 0;
        try {
            for (String part : parts) {
                sum += Double.parseDouble(replaceCellReferences(part.trim()));
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
     */
    private String evaluateDEBUG(String parameter) {
        return parameter.trim();
    }

    /**
     * Evaluates the STDDEV function with the given parameter.
     *
     * @param parameter the parameter for the STDDEV function.
     * @return the result of the STDDEV function.
     */
    private String evaluateSTDDEV(String parameter) {
        String[] nums = parameter.split(",");
        double avg = Double.parseDouble(evaluateAVG(parameter));
        double sum = 0;

        try {
            for (String num : nums) {
                sum += Math.pow(Double.parseDouble(replaceCellReferences(num)) - avg, 2);
            }
        } catch (NumberFormatException e) {
            return "Error";
        }

        double result = Math.pow(sum / nums.length, 0.5);
        return "" + (double) Math.round(result * 1000) / 1000;
    }

    /**
     * Evaluates the SORT function with the given parameter.
     *
     * @param parameter the parameter for the SORT function.
     * @return the result of the SORT function.
     */
    private String evaluateSORT(String parameter) {
        String[] s = parameter.split(",");
        double[] nums = new double[s.length];
        try {
            for (int i = 0; i < nums.length; i++) {
                nums[i] = Double.parseDouble(replaceCellReferences(s[i]));
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
     * Sorts the cells based on the formula.
     *
     * @param formula the formula containing the SORT function.
     * @return the sorted value.
     */
    private String sort(String formula) {
        String[] sorted = parseOperations(formula).split(",");
        if (sorted.length > 1) {
            String cells = formula.substring(5, formula.length() - 1);
            String endCell;
            if (cells.contains(":")) {
                endCell = cells.split(":")[1];
            } else {
                endCell = cells.split(",")[sorted.length - 1];
            }
            int r = getRow(endCell);
            int c = getColumn(endCell);

            for (int i = 0; i < sorted.length; i++) {
                Cell cell = this.grid.get(r + i + 1).get(c);
                cell.setValue(sorted[i]);
                cell.setFormula(sorted[i]);
            }

            this.grid.get(r + 1).get(c).setFormula("=" + formula);
        }
        return sorted[0];
    }
}
