package org.example.model;

public class ReadOnlySpreadSheet {
    private final Cell[][] cells;

    public ReadOnlySpreadSheet(Cell[][] cells) {
        this.cells = cells;
    }

    public int getRows() {
        return cells.length;
    }

    public int getCols() {
        return cells[0].length;
    }

    public Cell[][] getCellsObject() {
        return cells;
    }

    public String[][] getCellStringsObject() {
        String[][] cellStrings = new String[cells.length][cells[0].length];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cellStrings[i][j] = cells[i][j].getValue();
            }
        }
        return cellStrings;
    }
}
