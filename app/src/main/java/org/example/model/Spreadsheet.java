package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
                row.add(new Cell("test"));
            }
            grid.add(row);
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

    // Method to evaluate formulas
public String evaluateFormula(String formula) {
    if (!formula.startsWith("=")) {
        return formula;
    }

    // Remove the initial "="
    formula = formula.substring(1);

    // For simplicity, handle basic arithmetic operations
    try {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Object result = engine.eval(formula);
        return result.toString();
    } catch (ScriptException e) {
        e.printStackTrace();
        return "Error";
    }
}


    private String evaluateArithmetic(String formula) {
        // This is a very basic evaluator and doesn't handle precedence or complex expressions
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < formula.length(); i++) {
            char ch = formula.charAt(i);
            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < formula.length() && (Character.isDigit(formula.charAt(i)) || formula.charAt(i) == '.')) {
                    sb.append(formula.charAt(i++));
                }
                values.push(Double.parseDouble(sb.toString()));
                i--;
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!ops.isEmpty() && hasPrecedence(ch, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(ch);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return String.valueOf(values.pop());
    }

    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
        }
        return 0;
    }

    private String evaluateSumFunction(String formula) {
        // Extract the cell range from the SUM function
        int startIndex = formula.indexOf('(') + 1;
        int endIndex = formula.indexOf(')');
        String range = formula.substring(startIndex, endIndex);
        String[] cells = range.split(":");

        if (cells.length != 2) {
            throw new IllegalArgumentException("Invalid range for SUM function");
        }

        String startCell = cells[0];
        String endCell = cells[1];

        int startRow = getRowIndex(startCell);
        int startCol = getColIndex(startCell);
        int endRow = getRowIndex(endCell);
        int endCol = getColIndex(endCell);

        double sum = 0;
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                String cellValue = grid.get(i).get(j).getValue();
                if (!cellValue.startsWith("=")) {
                    try {
                        sum += Double.parseDouble(cellValue);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return String.valueOf(sum);
    }

    private int getRowIndex(String cellRef) {
        // Assuming cellRef format is like $A1
        return Integer.parseInt(cellRef.replaceAll("[^0-9]", "")) - 1;
    }

    private int getColIndex(String cellRef) {
        // Assuming cellRef format is like $A1
        String colRef = cellRef.replaceAll("[^A-Z]", "").toUpperCase();
        int colIndex = 0;
        for (int i = 0; i < colRef.length(); i++) {
            colIndex = colIndex * 26 + (colRef.charAt(i) - 'A' + 1);
        }
        return colIndex - 1;
    }
}
