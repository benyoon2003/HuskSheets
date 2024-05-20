package org.example.model;

// Represents an object provided in the body of an HTTP request
public class Argument {
    // the client making the request
    private String publisher;
    // the sheet the client wants to update
    private String sheet;
    // information describing the update to the sheet
    private String id;
    // the data for the update
    private String payload;

    public Argument(String publisher, String sheet, String id, String payload) {
        this.publisher = publisher;
        this.sheet = sheet;
        this.id = id;
        this.payload = payload;
    }

    String getPublisher() {
        return this.publisher;
    }

    String getSheet() {
        return this.sheet;
    }

    String getId() {
        return this.id;
    }

    String getPayload() {
        return this.payload;
    }

}
