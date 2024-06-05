package org.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SelectedCellsTest {
    private ISelectedCells cells = new SelectedCells(0, 0, 0, 0);

    @Test
    public void testGetters() {
        assertEquals(0, this.cells.getStartRow());
        assertEquals(0, this.cells.getStartCol());
        assertEquals(0, this.cells.getEndRow());
        assertEquals(0, this.cells.getEndCol());
    }
}
