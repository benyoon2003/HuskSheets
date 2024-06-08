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
        assertEquals("Initial success", result.getMessage(),
                "Constructor should correctly initialize the message field");
        assertEquals(initialArguments, result.getValue(), "Constructor should correctly initialize the value field");
    }

    @Test
    public void testConstructorFromJSONString() {
        String jsonString = "{\"success\":\"true\", \"message\":null, \"value\": [{\"publisher\":\"test\",\"sheet\":\"Test\",\"id\":null,\"payload\":null}]}";
        Result test = new Result(jsonString);

        assertNotNull(test);
        assertTrue(test.getSuccess(), "Constructor should correctly initialize the success field");
        Argument arg = test.getValue().get(0);
        assertEquals("test", arg.getPublisher());
        assertEquals("Test", arg.getSheet());
    }

    @Test
    public void testToString() {
        String string = "Result{success=true, message='Initial success', value=[Argument{publisher=publisher1, sheet=sheet1, id=id1, payload='payload1'}]}";
        assertEquals(string, result.toString());
    }

//    @Test
//    public void testGetSheets() {
//        String jsonString = "{\"success\":\"true\", \"message\":null,"
//                + "\"value\": [{\"publisher\":\"test\",\"sheet\":\"Test1\",\"id\":null,\"payload\":null},"
//                + "{\"publisher\":\"test\",\"sheet\":\"Test2\",\"id\":null,\"payload\":null}]}";
//        List<String> sheets = Result.getSheets(jsonString);
//
//        assertEquals("Test1", sheets.get(0));
//        assertEquals("Test2", sheets.get(1));
//
//        // for poorly constructed JSON objects
//        jsonString = "{\"success\":\"false\", \"message\": \"Bad JSON object\", \"value\": []\"}";
//        List<String> badSheets = Result.getSheets(jsonString);
//        assertTrue(badSheets.isEmpty());
//    }
//
//    @Test
//    public void testGetPayload() {
//        String response = "{\"success\":\"true\", \"message\":null,"
//        + "\"value\": [{\"publisher\":\"test\",\"sheet\":\"Test1\",\"id\":null,\"payload\":\"Payload1\"},"
//        + "{\"publisher\":\"test\",\"sheet\":\"Test2\",\"id\":null,\"payload\":\"Payload2\"}]}";
//
//        assertEquals("Payload1", Result.getPayload(response, "Test1"));
//        assertEquals("Payload2", Result.getPayload(response, "Test2"));
//
//        String badResponse = "bad response";
//        assertEquals(null, Result.getPayload(badResponse, "Test1"));
//    }

    @Test
    public void testMakeResponse() {
        
    }
}
