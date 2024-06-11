package org.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SelectedCellsTest {
    @Test
    public void testGetters() {
        ISelectedCells cells = new SelectedCells(1, 1,1, 1);

        assertEquals(0, cells.getStartRow());
        assertEquals(0, cells.getStartCol());
        assertEquals(0, cells.getEndRow());
        assertEquals(0, cells.getEndCol());
    }
}
