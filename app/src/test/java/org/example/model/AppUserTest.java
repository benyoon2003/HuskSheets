package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppUserTest {

    @Test
    public void testConstructorAndGetterSetter() {
        AppUser user = new AppUser();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        assertEquals("testUser", user.getUsername());
        assertEquals("testPassword", user.getPassword());
    }

    @Test
    public void testAuthenticateUser() {
        AppUser user = new AppUser();
        String result = user.authenticateUser("testUser", "testPassword");

        assertNotNull(result);

        // Add more assertions based on the expected outcomes
        user.createAccount("Ben", "1229");
        assertEquals(user.authenticateUser("Ben", "1"),
                "Failed to login: Invalid credentials");
        assertEquals(user.authenticateUser("Ben", "1229"),
                "Login successful!");
        assertEquals(user.authenticateUser("Be", "1229"),
                "Failed to login: Invalid credentials");
        assertEquals(user.authenticateUser("", ""),
                "Failed to login: Invalid credentials");

    }

    @Test
    public void testCreateAccount() {
        AppUser user = new AppUser();
        String result = user.createAccount("newUser", "newPassword");

        assertNotNull(result);

        // Add more assertions based on the expected outcomes
        assertEquals(user.createAccount("newUser", "newPassword"),
                "Failed to create account: Internal Server Error: Username already exists!");
        assertEquals(user.createAccount("Ben1", "1229"),
                "Account created successfully!");

    }
}
