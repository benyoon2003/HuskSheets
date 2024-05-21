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
    }

    @Test
    public void testCreateAccount() {
        AppUser user = new AppUser();
        String result = user.createAccount("newUser", "newPassword");

        assertNotNull(result);
        // Add more assertions based on the expected outcomes
    }
}
