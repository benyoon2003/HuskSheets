package org.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerEndpointTest {
    private ServerEndpoint se;
    private IAppUser user;
    private Result result;

    @BeforeEach
    // initialize fields, log in user
    public void init() {
        this.se = new ServerEndpoint();
        this.user = new AppUser("TestUser", "password");

        try {
            this.result = this.se.login(this.user);
        } catch (Exception i) {
            try {
                this.result = this.se.register(this.user);
                this.result = this.se.login(this.user);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Test
    // make sure we logged in successfully
    public void testLogin() {
        assertTrue(result.getSuccess());
        assertEquals("Publisher logged in successfully", result.getMessage());
    }

    @Test
    public void testGetPublishers() {
        try {
            result = this.se.getPublishers();
            assertTrue(result.getSuccess());
            Argument arg = result.getValue().getLast();
            // make sure this is the correct publisher
            assertEquals("TestUser", arg.getPublisher());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCreateSheet() {
        try {
            result = this.se.createSheet(this.randomString());
            assertTrue(result.getSuccess());
            assertEquals("Sheet created successfully", result.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetSheets() {
        try {
            result = this.se.getSheets("TestUser");

            assertTrue(result.getSuccess());
            assertEquals("Sheets retrieved successfully", result.getMessage());
            // make sure there is an argument value
            assertNotNull(result.getValue());
            assertFalse(result.getValue().isEmpty());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testDeleteSheet() {
        try {
            result = this.se.createSheet("DELETE");
            assertTrue(result.getSuccess());

            Result sheets = this.se.getSheets("TestUser");
            assertTrue(sheets.getSuccess());
            List<Argument> args = sheets.getValue();
            // store how many sheets there are currently
            int sizeBefore = args.size();
            // make sure the most recent sheet is the one we just created
            assertEquals("DELETE", args.getLast().getSheet());

            this.se.deleteSheet("TestUser", "DELETE");
            sheets = this.se.getSheets("TestUser");
            args = sheets.getValue();
            // make sure there is one less sheet
            assertEquals(sizeBefore - 1, args.size());
            // make sure the most recent sheet is not the DELETE sheet
            assertNotEquals("DELETE", args.getLast().getSheet());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetUpdatesForSubscription() {
        // try {
        //     String sheetName = this.randomString();
        //     result = this.se.getUpdatesForSubscription("TestUser", sheetName, null);
        // } catch (Exception e) {
        //     System.out.println(e.getMessage());
        // }
    }

    @Test
    public void testGetUpdatesForPublished() {
    }

    @Test
    public void testUpdatePublished() {
    }

    @Test
    public void testUpdateSubscription() {
    }

    private String randomString() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        String result = "";

        while (result.length() < 6) {
            int index = (int) (rnd.nextFloat() * chars.length());
            result += chars.charAt(index);
        }

        return result;
    }
}
