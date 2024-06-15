package org.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the methods within the ServerEndpoint class.
 * @author Theo
 */
public class ServerEndpointTest {
    private ServerEndpoint se;
    private IAppUser user;
    private String username = "TestUser";
    private Result result;

    /**
     * Initializes the fields and logs in the user before each test.
     * @author Theo
     */
    @BeforeEach
    public void init() {
        this.se = new ServerEndpoint();
        this.user = new AppUser(this.username, "password");

        try {
            result = this.se.login(this.user); // try logging in first
            if (!result.getSuccess()) {
                // if logging in didn't initially work, register and try again
                result = this.se.register(this.user);
                result = this.se.login(this.user);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests the register method of the ServerEndpoint class.
     * @author Theo
     */
    @Test
    public void testRegister() {
        String newUsername = this.randomString();
        String newPassword = this.randomString();
        IAppUser newUser = new AppUser(newUsername, newPassword);
        try {
            result = this.se.register(newUser);
            // successfully registered, result contains expected information
            assertTrue(result.getSuccess());
            assertEquals("Publisher registered successfully", result.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests the login method of the ServerEndpoint class.
     * @author Theo
     */
    @Test
    // make sure we logged in successfully
    public void testLogin() {
        try {
            result = this.se.login(this.user);
            // successfully logged in, result contains expected information
            assertTrue(result.getSuccess());
            assertEquals("Publisher logged in successfully", result.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests the getPublishers method of the ServerEndpoint class.
     * @author Theo
     */
    @Test
    public void testGetPublishers() {
        try {
            result = this.se.getPublishers();
            assertTrue(result.getSuccess()); // successful result

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

    /**
     * Tests the createSheet method of the ServerEndpoint class.
     * @author Theo
     */
    @Test
    public void testCreateSheet() {
        try {
            result = this.se.createSheet(this.randomString());
            // sheet was created successfully, result contains expected information
            assertTrue(result.getSuccess());
            assertEquals("Sheet created successfully", result.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests the getSheets method of the ServerEndpoint class.
     * @author Theo
     */
    @Test
    public void testGetSheets() {
        try {
            result = this.se.getSheets(this.username);

            // list of sheets successfully retrieved, result contains expected information
            assertTrue(result.getSuccess());
            assertEquals("Sheets retrieved successfully", result.getMessage());
            // make sure there is an argument value
            assertNotNull(result.getValue());
            assertFalse(result.getValue().isEmpty());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests the deleteSheet method of the ServerEndpoint class.
     * @author Theo
     */
    @Test
    public void testDeleteSheet() {
        try {
            Argument deleteArg = new Argument();
            deleteArg.setSheet("DELETE");
            deleteArg.setPublisher(this.username);
            if (!this.se.getSheets(this.username).getValue().contains(deleteArg)) {
                result = this.se.createSheet("DELETE");
            }

            Result sheets = this.se.getSheets(this.username);
            assertTrue(sheets.getSuccess());
            List<Argument> args = sheets.getValue();

            int sizeBefore = args.size(); // store how many sheets there are currently
            this.se.deleteSheet(this.username, "DELETE");

            sheets = this.se.getSheets(this.username);
            args = sheets.getValue();
            assertEquals(sizeBefore - 1, args.size()); // make sure there is one less sheet
            assertFalse(args.contains(deleteArg)); // make sure the DELETE sheet is not in the argument value
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests the updateSubscription and getUpdatesForSubscription methods of the ServerEndpoint class.
     * @author Theo
     */
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

    /**
     * Tests the updatePublished and getUpdatesForPublished methods of the ServerEndpoint class.
     * @author Theo
     */
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
    
    /**
     * Generates a random string for testing purposes.
     * @return a random string
     * @author Theo
     */
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
