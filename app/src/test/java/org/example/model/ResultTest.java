package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class ResultTest {

    private Result result;
    private List<Argument> initialArguments;

    @BeforeEach
    public void setUp() {
        initialArguments = new ArrayList<>();
        initialArguments.add(new Argument("publisher1", "sheet1", "id1", "payload1"));
        result = new Result(true, "Initial success", initialArguments);
    }

    @Test
    public void testConstructorAndGetters() {
        assertTrue(result.getSuccess(), "Constructor should correctly initialize the success field");
        assertEquals("Initial success", result.getMessage(), "Constructor should correctly initialize the message field");
        assertEquals(initialArguments, result.getValue(), "Constructor should correctly initialize the value field");
    }

    @Test
    public void testSetters() {
        result.setSuccess(false);
        result.setMessage("Updated message");

        assertFalse(result.getSuccess(), "setSuccess should correctly update the success field");
        assertEquals("Updated message", result.getMessage(), "setMessage should correctly update the message field");
    }

    @Test
    public void testAddValue() {
        Argument newArgument = new Argument("publisher2", "sheet2", "id2", "payload2");
        result.addValue(newArgument);

        assertEquals(2, result.getValue().size(), "addValue should add the argument to the value list");
        assertTrue(result.getValue().contains(newArgument), "addValue should correctly add the new argument");
    }
}
