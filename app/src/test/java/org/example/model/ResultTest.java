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
}
