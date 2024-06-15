package org.example.model;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the methods within the Result class.
 * @author Theo
 */
public class ResultTest {
    private Result result;
    private List<Argument> initialArguments;

    /**
     * Sets up the initial conditions before each test.
     * @author Theo
     */
    @BeforeEach
    public void setUp() {
        initialArguments = new ArrayList<>();
        initialArguments.add(new Argument("publisher1", "sheet1", "id1", "payload1"));
        result = new Result(true, "Initial success", initialArguments);
    }

    /**
     * Tests the constructor and getter methods of the Result class.
     * @author Theo
     */
    @Test
    public void testConstructorAndGetters() {
        // result constructor initialized object as expected
        assertTrue(result.getSuccess(), "Constructor should correctly initialize the success field");
        assertEquals("Initial success", result.getMessage(),
                "Constructor should correctly initialize the message field");
        assertEquals(initialArguments, result.getValue(), "Constructor should correctly initialize the value field");
    }

    /**
     * Tests the constructor with a null value.
     * @author Theo
     */
    @Test
    public void testConstructorWithNullValue() {
        Result resultWithNullValue = new Result(true, "Success with null value", null);
        assertTrue(resultWithNullValue.getSuccess());
        assertEquals("Success with null value", resultWithNullValue.getMessage());
        // ensure that the constructor initialized an empty list of argument values
        assertNotNull(resultWithNullValue.getValue());
        assertTrue(resultWithNullValue.getValue().isEmpty(), "Value should be an empty list if null is passed");
    }

    /**
     * Tests the constructor that initializes a Result object from a JSON string.
     * @author Theo
     */
    @Test
    public void testConstructorFromJSONString() {
        String jsonString = "{\"success\":true, \"message\":null, \"value\": [{\"publisher\":\"test\",\"sheet\":\"Test\",\"id\":null,\"payload\":null}]}";
        Result test = new Result(jsonString);

        // ensure that the test result came back successful
        assertNotNull(test);
        assertTrue(test.getSuccess(), "Constructor should correctly initialize the success field");
        assertNull(test.getMessage(), "Constructor should correctly initialize the message field as null");
        assertNotNull(test.getValue(), "Constructor should correctly initialize the value field");

        // ensure the argument value is not empty and is the one we expect
        assertEquals(1, test.getValue().size(), "Value should have one argument");
        Argument arg = test.getValue().get(0);
        assertEquals("test", arg.getPublisher());
        assertEquals("Test", arg.getSheet());
    }

    /**
     * Tests the constructor that initializes a Result object from a JSON string
     * with an empty value.
     * @author Theo
     */
    @Test
    public void testConstructorFromJSONStringWithEmptyValue() {
        String jsonString = "{\"success\":true, \"message\":null, \"value\": []}";
        Result test = new Result(jsonString);

        // ensure that the test result came back successful with no argument value
        assertNotNull(test);
        assertTrue(test.getSuccess(), "Constructor should correctly initialize the success field");
        assertNull(test.getMessage(), "Constructor should correctly initialize the message field as null");
        assertNotNull(test.getValue(), "Constructor should correctly initialize the value field");
        assertTrue(test.getValue().isEmpty(), "Value should be an empty list");
    }

    /**
     * Tests the constructor that initializes a Result object from a JSON string
     * with a null value.
     * @author Theo
     */
    @Test
    public void testConstructorFromJSONStringWithNullValue() {
        String jsonString = "{\"success\":true, \"message\":null}";
        Result test = new Result(jsonString);

        // ensure that the test result came back successful with no message and no
        // argument value
        assertNotNull(test);
        assertTrue(test.getSuccess(), "Constructor should correctly initialize the success field");
        assertNull(test.getMessage(), "Constructor should correctly initialize the message field as null");
        assertNotNull(test.getValue(), "Constructor should correctly initialize the value field");
        assertTrue(test.getValue().isEmpty(), "Value should be an empty list if not provided in JSON");
    }

    /**
     * Tests the constructor that initializes a Result object from a malformed JSON
     * string.
     * @author Theo
     */
    @Test
    public void testConstructorFromJSONStringWithMalformedJSON() {
        String jsonString = "{success:true, message:null, value: [{publisher:test,sheet:Test,id:null,payload:null}"; // Missing
                                                                                                                     // closing
                                                                                                                     // brackets
        assertThrows(JSONException.class, () -> new Result(jsonString), "Malformed JSON should throw JSONException");
    }

    /**
     * Tests the constructor that initializes a Result object from an invalid JSON
     * string.
     * @author Theo
     */
    @Test
    public void testConstructorFromJSONStringWithInvalidJSON() {
        // invalid JSON object
        String jsonString = "{\"success\":true, \"message\":null, \"value\": [{\"publisher\":\"test\",\"sheet\":\"Test\",\"id\":\"1\",\"payload\":\"payload\"}, {\"publisher\":}}";
        assertThrows(JSONException.class, () -> new Result(jsonString), "Invalid JSON should throw JSONException");
    }

    /**
     * Tests the toString method of the Result class.
     * @author Theo
     */
    @Test
    public void testToString() {
        String string = "Result{success=true, message='Initial success', value=[Argument{publisher=publisher1, sheet=sheet1, id=id1, payload='payload1'}]}";
        assertEquals(string, result.toString());
    }

    /**
     * Tests the toString method of the Result class with an empty value.
     * @author Theo
     */
    @Test
    public void testToStringWithEmptyValue() {
        Result resultWithEmptyValue = new Result(true, "Success with empty value", new ArrayList<>());
        String expectedString = "Result{success=true, message='Success with empty value', value=[]}";
        assertEquals(expectedString, resultWithEmptyValue.toString());
    }

    /**
     * Tests the toString method of the Result class with a null message.
     * @author Theo
     */
    @Test
    public void testToStringWithNullMessage() {
        Result resultWithNullMessage = new Result(true, null, initialArguments);
        String expectedString = "Result{success=true, message='null', value=[Argument{publisher=publisher1, sheet=sheet1, id=id1, payload='payload1'}]}";
        assertEquals(expectedString, resultWithNullMessage.toString());
    }
}
