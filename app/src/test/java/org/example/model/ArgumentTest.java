package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the methods within the Argument class.
 */
public class ArgumentTest {

    /**
     * Tests the constructor and getter methods of the Argument class.
     */
    @Test
    public void testConstructorAndGetters() {
        String publisher = "TestPublisher"; // Declares a test publisher string.
        String sheet = "TestSheet"; // Declares a test sheet string.
        String id = "TestID"; // Declares a test ID string.
        String payload = "TestPayload"; // Declares a test payload string.

        Argument argument = new Argument(publisher, sheet, id, payload); // Creates a new Argument instance with test data.

        assertEquals(publisher, argument.getPublisher()); // Verifies the publisher getter.
        assertEquals(sheet, argument.getSheet()); // Verifies the sheet getter.
        assertEquals(id, argument.getId()); // Verifies the ID getter.
        assertEquals(payload, argument.getPayload()); // Verifies the payload getter.

        assertEquals("Argument{publisher=TestPublisher, sheet=TestSheet, id=TestID, payload='TestPayload'}", argument.toString()); // Verifies the toString method output.
    }
    
    /**
     * Tests the setter methods of the Argument class.
     */
    @Test
    public void testSetters() {
        Argument argument = new Argument(); // Creates a new Argument instance with the default constructor.

        assertNull(argument.getPublisher()); // Verifies that the publisher is initially null.
        assertNull(argument.getSheet()); // Verifies that the sheet is initially null.
        assertNull(argument.getId()); // Verifies that the ID is initially null.
        assertNull(argument.getPayload()); // Verifies that the payload is initially null.

        argument.setPublisher("TestPublisher"); // Sets the publisher to "TestPublisher".
        argument.setSheet("TestSheet"); // Sets the sheet to "TestSheet".
        argument.setId("TestID"); // Sets the ID to "TestID".
        argument.setPayload("TestPayload"); // Sets the payload to "TestPayload".

        assertEquals("TestPublisher", argument.getPublisher()); // Verifies the publisher setter.
        assertEquals("TestSheet", argument.getSheet()); // Verifies the sheet setter.
        assertEquals("TestID", argument.getId()); // Verifies the ID setter.
        assertEquals("TestPayload", argument.getPayload()); // Verifies the payload setter.
    }
}
