package org.example.model;

import java.util.ArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Spreadsheet implements ISpreadsheet {
    private ArrayList<ArrayList<Cell>> grid;

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
            // Handle IF operation
            if (formula.startsWith("IF(")) {
                return evaluateIF(formula.substring(3, formula.length() - 1));
            }

            // Handle SUM operation
            if (formula.startsWith("SUM(")) {
                return evaluateSUM(formula.substring(4, formula.length() - 1));
            }

            // Handle MIN operation
            if (formula.startsWith("MIN(")) {
                return evaluateMIN(formula.substring(4, formula.length() - 1));
            }

            // Handle MAX operation
            if (formula.startsWith("MAX(")) {
                return evaluateMAX(formula.substring(4, formula.length() - 1));
            }

            // Handle AVG operation
            if (formula.startsWith("AVG(")) {
                return evaluateAVG(formula.substring(4, formula.length() - 1));
            }

            // Handle CONCAT operation
            if (formula.startsWith("CONCAT(")) {
                return evaluateCONCAT(formula.substring(7, formula.length() - 1));
            }

            // Handle DEBUG operation
            if (formula.startsWith("DEBUG(")) {
                return evaluateDEBUG(formula.substring(6, formula.length() - 1));
            }

            // Handle special operations
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
            } else {
                // For simplicity, handle basic arithmetic operations
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                Object result = engine.eval(formula);
                return result.toString();
            }
        } catch (ScriptException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    private String evaluateIF(String args) {
        String[] parts = args.split(",");
        if (parts.length != 3) {
            return "Error";
        }

        String condition = parts[0].trim();
        String trueValue = parts[1].trim();
        String falseValue = parts[2].trim();

        try {
            double conditionValue = getNumericValue(condition);
            return conditionValue != 0 ? trueValue : falseValue;
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateSUM(String args) {
        String[] parts = args.split(",");
        double sum = 0;

        try {
            for (String part : parts) {
                double value = getNumericValue(part.trim());
                sum += value;
            }
            return String.valueOf(sum);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateMIN(String args) {
        String[] parts = args.split(",");
        double min = Double.MAX_VALUE;

        try {
            for (String part : parts) {
                double value = getNumericValue(part.trim());
                if (value < min) {
                    min = value;
                }
            }
            return String.valueOf(min);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateMAX(String args) {
        String[] parts = args.split(",");
        double max = Double.MIN_VALUE;

        try {
            for (String part : parts) {
                double value = getNumericValue(part.trim());
                if (value > max) {
                    max = value;
                }
            }
            return String.valueOf(max);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateAVG(String args) {
        String[] parts = args.split(",");
        double sum = 0;
        int count = 0;

        try {
            for (String part : parts) {
                double value = getNumericValue(part.trim());
                sum += value;
                count++;
            }
            return count > 0 ? String.valueOf(sum / count) : "Error";
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String evaluateCONCAT(String args) {
        String[] parts = args.split(",");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            String stringValue = getStringValue(part.trim());
            if (stringValue.startsWith("\"") && stringValue.endsWith("\"")) {
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }
            result.append(stringValue);
        }

        return result.toString();
    }

    private String evaluateDEBUG(String args) {
        return getStringValue(args.trim());
    }

    private double getNumericValue(String reference) throws NumberFormatException {
        if (reference.matches("[A-Za-z]+[0-9]+")) { // Check if it's a cell reference
            int row = getRow(reference);
            int col = getColumn(reference);
            String cellValue = getCellValue(row, col);
            return Double.parseDouble(cellValue);
        } else {
            return Double.parseDouble(reference); // Otherwise, it's a direct numeric value
        }
    }

    private String getStringValue(String reference) {
        if (reference.matches("[A-Za-z]+[0-9]+")) { // Check if it's a cell reference
            int row = getRow(reference);
            int col = getColumn(reference);
            return getCellValue(row, col);
        } else {
            return reference; // Otherwise, it's a direct string value
        }
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
        int startRow = getRow(startCell);
        int endRow = getRow(endCell);
        int startCol = getColumn(startCell);
        int endCol = getColumn(endCell);

        if (startRow == -1 || endRow == -1 || startCol == -1 || endCol == -1) {
            return "Error";
        }

        StringBuilder rangeResult = new StringBuilder();
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                rangeResult.append(getCellValue(row, col)).append(" ");
            }
        }
        System.out.println("Range Result: " + rangeResult.toString().trim());
        return rangeResult.toString().trim();
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

    @Override
    public void setCellValue(int row, int col, String value) {
        this.grid.get(row).get(col).setValue(value);
    }

    @Override
    public String getCellValue(int row, int col) {
        return this.grid.get(row).get(col).getValue();
    }
}
