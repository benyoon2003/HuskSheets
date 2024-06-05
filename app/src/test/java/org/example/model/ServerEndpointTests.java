package org.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerEndpointTests {
    private ServerEndpoint se;
    private IAppUser user;
    private Result result;

    @BeforeEach
    public void init() {
        this.se = new ServerEndpoint();
        this.user = new AppUser();

        this.user.setUsername("test");
        this.user.setPassword("test");

        try {
            result = this.se.login(this.user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testLogin() {
        assertTrue(result.getSuccess());
        assertEquals("Publisher logged in successfully", result.getMessage());
    }

    @Test
    public void testGetPublishers() {
        try {
            result = this.se.getPublishers();
            assertTrue(result.getSuccess());
            Argument arg = result.getValue().get(0);
            assertEquals("test", arg.getPublisher()); // correct publisher
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCreateSheet() {
        try {
            result = this.se.createSheet("New Sheet");
            assertTrue(result.getSuccess());
            assertEquals("Sheet created successfully", result.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetSheets() {
        try {
            result = new Result(this.se.getSheets("test"));

            assertTrue(result.getSuccess());
            assertEquals("Sheets retrieved successfully", result.getMessage());
            assertNotNull(result.getValue());
            assertFalse(result.getValue().isEmpty());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
