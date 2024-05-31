package org.example.model;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Spreadsheet implements ISpreadsheet {
    private ArrayList<ArrayList<Cell>> grid;
    private String[] functions = new String[] { "IF", "SUM", "MIN", "MAX", "AVG", "CONCAT", "DEBUG", "STDDEV" };
    private String[] arith = new String[] { "+", "-", "*", "/" };

    public Spreadsheet() {
        grid = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < 100; j++) {
                row.add(new Cell(""));
            }
            grid.add(row);
        }
    }

    public Spreadsheet(ArrayList<ArrayList<Cell>> grid) {
        this();
        for (ArrayList<Cell> row : grid) {
            for (Cell c : row) {
                this.grid.get(c.getRow()).get(c.getCol()).setValue(c.getValue());
            }
        }
    }

    public int getRows() {
        return this.grid.size();
    }

    public int getCols() {
        return this.grid.get(0).size();
    }

    public ArrayList<ArrayList<Cell>> getCells() {
        return this.grid;
    }

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

    @Override
    public String evaluateFormula(String formula) {
        if (!formula.startsWith("=")) {
            return formula;
        }

        // Remove the initial "="
        formula = formula.substring(1);

        try {
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

    // Replace cell references with their values
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

    private int getRow(String cell) {
        try {
            return Integer.parseInt(cell.replaceAll("[^0-9]", "")) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int getColumn(String cell) {
        String col = cell.replaceAll("[^A-Z]", "").toUpperCase();
        int column = 0;
        for (int i = 0; i < col.length(); i++) {
            column = column * 26 + (col.charAt(i) - 'A' + 1);
        }
        return column - 1;
    }

    // If this cell contains a function value, return it
    private String getFunction(String cell) {
        for (String func : functions) {
            if (cell.contains(func)) {
                return func;
            }
        }

        return "";
    }

    // Returns whether or not this cell contains a basic arithmetic operation
    private boolean containsArith(String cell) {
        for (String op : arith) {
            if (cell.contains(op)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setCellValue(int row, int col, String value) {
        this.grid.get(row).get(col).setValue(value);
    }

    @Override
    public String getCellValue(int row, int col) {
        return this.grid.get(row).get(col).getValue();
    }

    @Override
    public String getCellRawdata(int row, int col) {
        return this.grid.get(row).get(col).getRawdata();
    }

    @Override
    public void setCellRawdata(int row, int col, String val) {
        this.grid.get(row).get(col).setRawData(val);
    }

    @Override
    public String getCellFormula(int row, int col) {
        return this.grid.get(row).get(col).getFormula();
    }

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
        }

        return formula;
    }

    private String compareLess(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a < b ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String compareGreater(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return a > b ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String compareEqual(String x, String y) {
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

    private String compareNotEqual(String x, String y) {
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

    private String andOperation(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return (a != 0 && b != 0) ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String orOperation(String x, String y) {
        try {
            double a = Double.parseDouble(x);
            double b = Double.parseDouble(y);
            return (a != 0 || b != 0) ? "1" : "0";
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String rangeOperation(String startCell, String endCell) {
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

    private String evaluateIF(String parameters) {
        String[] parts = parameters.split(",");
        if (parts.length != 3) {
            return "Error";
        }
        String condition = parts[0].trim();
        String trueResult = parts[1].trim();
        String falseResult = parts[2].trim();

        try {
            double conditionValue = Double.parseDouble(condition);
            return conditionValue != 0 ? trueResult : falseResult;
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateSUM(String parameters) {
        if (parameters.contains(":")) {
            String[] rangeParts = parameters.split(":");
            String rangeValues = rangeOperation(rangeParts[0].trim(), rangeParts[1].trim());
            if (rangeValues.equals("Error")) {
                return "Error";
            }
            parameters = rangeValues;
        }
        String[] parts = parameters.split(",");
        double sum = 0;
        try {
            for (String part : parts) {
                sum += Double.parseDouble(part.trim());
            }
            return String.valueOf(sum);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateMIN(String parameters) {
        String[] parts = parameters.split(",");
        double min = Double.MAX_VALUE;
        try {
            for (String part : parts) {
                double value = Double.parseDouble(part.trim());
                if (value < min) {
                    min = value;
                }
            }
            return String.valueOf(min);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateMAX(String parameters) {
        String[] parts = parameters.split(",");
        double max = Double.MIN_VALUE;
        try {
            for (String part : parts) {
                double value = Double.parseDouble(part.trim());
                if (value > max) {
                    max = value;
                }
            }
            return String.valueOf(max);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateAVG(String parameters) {
        String[] parts = parameters.split(",");
        double sum = 0;
        try {
            for (String part : parts) {
                sum += Double.parseDouble(part.trim());
            }
            return String.valueOf(sum / parts.length);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

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

    private String evaluateDEBUG(String parameter) {
        return parameter.trim();
    }

    private String evaluateSTDDEV(String parameter) {
        String[] nums = parameter.split(",");
        double avg = Double.parseDouble(evaluateAVG(parameter));
        double sum = 0;

        try {
            for (String num : nums) {
                sum += Math.pow(Double.parseDouble(num) - avg, 2);
            }
        } catch (NumberFormatException e) {
            return "Error";
        }

        double result = Math.pow(sum / nums.length, 0.5);
        return "" + (double) Math.round(result * 1000) / 1000;
    }
}
