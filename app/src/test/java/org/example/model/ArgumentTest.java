package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArgumentTest {

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
    }
}
