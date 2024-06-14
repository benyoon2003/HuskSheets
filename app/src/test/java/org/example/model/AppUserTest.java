package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

/**
 * Tests the methods within the AppUser class.
 */
public class AppUserTest {
    private AppUser user;

    /**
     * Initializes the AppUser instance before each test.
     */
    @BeforeEach
    public void init() {
        this.user = new AppUser("testUser", "testPassword");
    }

    /**
     * Tests the constructor, getters, and setters of the AppUser class.
     */
    @Test
    public void testConstructorAndGetterSetter() {
        assertEquals("testUser", this.user.getUsername());
        assertEquals("testPassword", this.user.getPassword());
    }

    /**
     * Tests the addSheet method of the AppUser class.
     */
    @Test
    public void testAddSheet() {
        assertEquals(0, this.user.getSheets().size());
    
        this.user.addSheet("Sheet1");

        assertEquals(1, this.user.getSheets().size());
        assertEquals("Sheet1", this.user.getSheets().get(0).getName());
    }

    /**
     * Tests the removeSheet method of the AppUser class.
     */
    @Test
    public void testRemoveSheet() {
        this.user.addSheet("Sheet1");
        this.user.addSheet("Sheet2");
        assertEquals(2, this.user.getSheets().size());
        assertEquals("Sheet1", this.user.getSheets().get(0).getName());

        this.user.removeSheet("Sheet1");
        assertEquals(1, this.user.getSheets().size());
        this.user.removeSheet("Sheet2");
        assertEquals(0, this.user.getSheets().size());
    }

    /**
     * Tests the removeSheet method when attempting to remove a non-existent sheet.
     */
    @Test
    public void testRemoveNonExistentSheet() {
        this.user.addSheet("Sheet1");
        this.user.addSheet("Sheet2");
        assertEquals(2, this.user.getSheets().size());

        this.user.removeSheet("Sheet3");
        assertEquals(2, this.user.getSheets().size());
        assertEquals("Sheet1", this.user.getSheets().get(0).getName());
        assertEquals("Sheet2", this.user.getSheets().get(1).getName());
    }

    /**
     * Tests the removeSheet method when the list of sheets is empty.
     */
    @Test
    public void testRemoveSheetFromEmptyList() {
        assertEquals(0, this.user.getSheets().size());

        this.user.removeSheet("Sheet1");
        assertEquals(0, this.user.getSheets().size());
    }

    /**
     * Tests the removeSheet method by removing all sheets one by one.
     */
    @Test
    public void testRemoveAllSheetsOneByOne() {
        this.user.addSheet("Sheet1");
        this.user.addSheet("Sheet2");
        this.user.addSheet("Sheet3");
        assertEquals(3, this.user.getSheets().size());

        this.user.removeSheet("Sheet1");
        assertEquals(2, this.user.getSheets().size());
        this.user.removeSheet("Sheet2");
        assertEquals(1, this.user.getSheets().size());
        this.user.removeSheet("Sheet3");
        assertEquals(0, this.user.getSheets().size());
    }
    
    /**
     * Tests the doesSheetExist method of the AppUser class.
     */
    @Test
    public void testDoesSheetExist() {
        assertFalse(this.user.doesSheetExist("Sheet1"));

        this.user.addSheet("Sheet1");

        assertTrue(this.user.doesSheetExist("Sheet1"));
        assertFalse(this.user.doesSheetExist("Sheet2"));
    }
}
