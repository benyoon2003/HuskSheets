package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

public class AppUserTest {
    private AppUser user;

    @BeforeEach
    public void init() {
        this.user = new AppUser();
        this.user.setUsername("testUser");
        this.user.setPassword("testPassword");
    }

    @Test
    public void testConstructorAndGetterSetter() {
        assertEquals("testUser", this.user.getUsername());
        assertEquals("testPassword", this.user.getPassword());
    }

    @Test
    public void testAddSheet() {
        assertEquals(0, this.user.getSheets().size());
    
        this.user.addSheet("Sheet1");

        assertEquals(1, this.user.getSheets().size());
        assertEquals("Sheet1", this.user.getSheets().get(0).getName());
    }

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

    @Test
    public void testDoesSheetExist() {
        assertFalse(this.user.doesSheetExist("Sheet1"));

        this.user.addSheet("Sheet1");

        assertTrue(this.user.doesSheetExist("Sheet1"));
        assertFalse(this.user.doesSheetExist("Sheet2"));
    }
}
