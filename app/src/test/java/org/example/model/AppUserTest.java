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
     * @author Theo
     */
    @BeforeEach
    public void init() {
        this.user = new AppUser("testUser", "testPassword"); // Initializes the AppUser with test data.
    }

    /**
     * Tests the constructor, getters, and setters of the AppUser class.
     * @author Theo
     */
    @Test
    public void testConstructorAndGetterSetter() {
        assertEquals("testUser", this.user.getUsername()); // Checks if the username is set correctly.
        assertEquals("testPassword", this.user.getPassword()); // Checks if the password is set correctly.
    }

    /**
     * Tests the addSheet method of the AppUser class.
     * @author Tony
     */
    @Test
    public void testAddSheet() {
        assertEquals(0, this.user.getSheets().size()); // Verifies that the sheets list is initially empty.
    
        this.user.addSheet("Sheet1"); // Adds a sheet named "Sheet1".

        assertEquals(1, this.user.getSheets().size()); // Checks if the sheet was added.
        assertEquals("Sheet1", this.user.getSheets().get(0).getName()); // Verifies the name of the added sheet.
    }

    /**
     * Tests the removeSheet method of the AppUser class.
     * @author Tony
     */
    @Test
    public void testRemoveSheet() {
        this.user.addSheet("Sheet1"); // Adds a sheet named "Sheet1".
        this.user.addSheet("Sheet2"); // Adds a second sheet named "Sheet2".
        assertEquals(2, this.user.getSheets().size()); // Checks if both sheets were added.
        assertEquals("Sheet1", this.user.getSheets().get(0).getName()); // Verifies the name of the first sheet.

        this.user.removeSheet("Sheet1"); // Removes the first sheet.
        assertEquals(1, this.user.getSheets().size()); // Checks if the sheet was removed.
        this.user.removeSheet("Sheet2"); // Removes the second sheet.
        assertEquals(0, this.user.getSheets().size()); // Verifies that all sheets are removed.
    }

    /**
     * Tests the removeSheet method when attempting to remove a non-existent sheet.
     * @author Tony
     */
    @Test
    public void testRemoveNonExistentSheet() {
        this.user.addSheet("Sheet1"); // Adds a sheet named "Sheet1".
        this.user.addSheet("Sheet2"); // Adds a second sheet named "Sheet2".
        assertEquals(2, this.user.getSheets().size()); // Checks if both sheets were added.

        this.user.removeSheet("Sheet3"); // Attempts to remove a non-existent sheet named "Sheet3".
        assertEquals(2, this.user.getSheets().size()); // Verifies that no sheets were removed.
        assertEquals("Sheet1", this.user.getSheets().get(0).getName()); // Verifies the name of the first sheet.
        assertEquals("Sheet2", this.user.getSheets().get(1).getName()); // Verifies the name of the second sheet.
    }

    /**
     * Tests the removeSheet method when the list of sheets is empty.
     * @author Tony
     */
    @Test
    public void testRemoveSheetFromEmptyList() {
        assertEquals(0, this.user.getSheets().size()); // Verifies that the sheets list is initially empty.

        this.user.removeSheet("Sheet1"); // Attempts to remove a sheet from an empty list.
        assertEquals(0, this.user.getSheets().size()); // Verifies that the list remains empty.
    }

    /**
     * Tests the removeSheet method by removing all sheets one by one.
     * @author Tony
     */
    @Test
    public void testRemoveAllSheetsOneByOne() {
        this.user.addSheet("Sheet1"); // Adds a sheet named "Sheet1".
        this.user.addSheet("Sheet2"); // Adds a second sheet named "Sheet2".
        this.user.addSheet("Sheet3"); // Adds a third sheet named "Sheet3".
        assertEquals(3, this.user.getSheets().size()); // Checks if all sheets were added.

        this.user.removeSheet("Sheet1"); // Removes the first sheet.
        assertEquals(2, this.user.getSheets().size()); // Verifies that one sheet was removed.
        this.user.removeSheet("Sheet2"); // Removes the second sheet.
        assertEquals(1, this.user.getSheets().size()); // Verifies that another sheet was removed.
        this.user.removeSheet("Sheet3"); // Removes the third sheet.
        assertEquals(0, this.user.getSheets().size()); // Verifies that all sheets are removed.
    }
    
    /**
     * Tests the doesSheetExist method of the AppUser class
     * @author Ben
     */
    @Test
    public void testDoesSheetExist() {
        assertFalse(this.user.doesSheetExist("Sheet1")); // Checks if the sheet "Sheet1" does not exist initially.

        this.user.addSheet("Sheet1"); // Adds a sheet named "Sheet1".

        assertTrue(this.user.doesSheetExist("Sheet1")); // Verifies that the sheet "Sheet1" now exists.
        assertFalse(this.user.doesSheetExist("Sheet2")); // Checks that a non-existent sheet "Sheet2" is not found.
    }
}
