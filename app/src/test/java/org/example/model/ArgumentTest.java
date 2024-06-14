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
        String publisher = "TestPublisher";
        String sheet = "TestSheet";
        String id = "TestID";
        String payload = "TestPayload";

        Argument argument = new Argument(publisher, sheet, id, payload);

        assertEquals(publisher, argument.getPublisher());
        assertEquals(sheet, argument.getSheet());
        assertEquals(id, argument.getId());
        assertEquals(payload, argument.getPayload());

        assertEquals("Argument{publisher=TestPublisher, sheet=TestSheet, id=TestID, payload='TestPayload'}", argument.toString());
    }
    
    /**
     * Tests the setter methods of the Argument class.
     */
    @Test
    public void testSetters() {
        Argument argument = new Argument();

        assertNull(argument.getPublisher());
        assertNull(argument.getSheet());
        assertNull(argument.getId());
        assertNull(argument.getPayload());

        argument.setPublisher("TestPublisher");
        argument.setSheet("TestSheet");
        argument.setId("TestID");
        argument.setPayload("TestPayload");

        assertEquals("TestPublisher", argument.getPublisher());
        assertEquals("TestSheet", argument.getSheet());
        assertEquals("TestID", argument.getId());
        assertEquals("TestPayload", argument.getPayload());
    }
}
