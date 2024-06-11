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
    private String username = "TestUser";
    private Result result;

    @BeforeEach
    // initialize fields, log in user
    public void init() {
        this.se = new ServerEndpoint();
        this.user = new AppUser(this.username, "password");

        try {
            result = this.se.login(this.user);
        } catch (Exception i) {
            try {
                result = this.se.register(this.user);
                result = this.se.login(this.user);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Test
    public void testRegister() {
        String newUsername = this.randomString();
        IAppUser newUser = new AppUser(newUsername, "");
        try {
            result = this.se.register(newUser);
            assertTrue(result.getSuccess());
            assertEquals("Publisher registered successfully", result.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

            // make sure our test publisher is included
            List<Argument> value = result.getValue();
            assertTrue(() -> {
                for (Argument arg : value) {
                    if (arg.getPublisher().equals(this.username))
                        return true;
                }
                return false;
            });
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
            result = this.se.getSheets(this.username);

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

            Result sheets = this.se.getSheets(this.username);
            assertTrue(sheets.getSuccess());
            List<Argument> args = sheets.getValue();
            // store how many sheets there are currently
            int sizeBefore = args.size();
            // make sure the most recent sheet is the one we just created
            assertEquals("DELETE", args.getLast().getSheet());

            this.se.deleteSheet(this.username, "DELETE");
            sheets = this.se.getSheets(this.username);
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
    public void testUpdateSubscriptionAndGetUpdatesForSubscription() {
        try {
            // create a new sheet
            String sheet = this.randomString();
            result = this.se.createSheet(sheet);
            assertTrue(result.getSuccess());

            // update sheet, make sure it was successful
            String payload = "$A1 00\\n$B1 01\\n$A2 10\\n$B2 11\\n";
            result = this.se.updateSubscription(this.username, sheet, payload);
            assertTrue(result.getSuccess());
            assertEquals("Sheet updated successfully", result.getMessage());

            // get updates of the sheet
            result = this.se.getUpdatesForSubscription(this.username, sheet, "0");
            assertTrue(result.getSuccess());
            assertEquals("Updates received", result.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testUpdatePublishedAndGetUpdatesForPublished() {
        try {
            // create a new sheet
            String sheet = this.randomString();
            result = this.se.createSheet(sheet);
            assertTrue(result.getSuccess());

            // update sheet, make sure it was successful
            String payload = "$A1 00\\n$B1 01\\n$A2 10\\n$B2 11\\n";
            result = this.se.updatePublished(this.username, sheet, payload);
            assertTrue(result.getSuccess());
            assertEquals("Sheet updated successfully", result.getMessage());

            // get updates of the sheet
            result = this.se.getUpdatesForPublished(this.username, sheet, "0");
            assertTrue(result.getSuccess());
            assertEquals("Updates received", result.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
