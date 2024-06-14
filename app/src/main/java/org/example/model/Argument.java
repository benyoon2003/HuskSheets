package org.example.model;

/**
 * The Argument class represents an entity containing information related to a
 * publisher, sheet, ID, and payload.
 * It includes methods to get and set these values.
 */
public class Argument {
    private String publisher, sheet, id, payload;

    /**
     * Constructs an Argument with the specified publisher, sheet, ID, and payload.
     *
     * @param publisher the publisher associated with the argument
     * @param sheet     the sheet associated with the argument
     * @param id        the ID associated with the argument
     * @param payload   the payload associated with the argument
     */
    public Argument(String publisher, String sheet, String id, String payload) {
        this.publisher = publisher; // Initialize publisher
        this.sheet = sheet; // Initialize sheet
        this.id = id; // Initialize id
        this.payload = payload; // Initialize payload
    }

    /**
     * Default constructor for Argument.
     * Initializes an empty Argument instance.
     */
    public Argument() {
        // Empty constructor for creating an empty Argument object
    }

    /**
     * Gets the publisher associated with this argument.
     *
     * @return the publisher
     * @author Theo
     */
    public String getPublisher() {
        return this.publisher; // Return the publisher
    }

    /**
     * Gets the sheet associated with this argument.
     *
     * @return the sheet
     * @author Theo
     */
    public String getSheet() {
        return this.sheet; // Return the sheet
    }

    /**
     * Gets the ID associated with this argument.
     *
     * @return the ID
     * @author Tony
     */
    public String getId() {
        return this.id; // Return the ID
    }

    /**
     * Gets the payload associated with this argument.
     *
     * @return the payload
     * @author Ben
     */
    public String getPayload() {
        return this.payload; // Return the payload
    }

    /**
     * Sets the publisher associated with this argument.
     *
     * @param publisher the publisher to set
     * @author Theo
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher; // Set the publisher
    }

    /**
     * Sets the sheet associated with this argument.
     *
     * @param sheet the sheet to set
     * @author Theo
     */
    public void setSheet(String sheet) {
        this.sheet = sheet; // Set the sheet
    }

    /**
     * Sets the ID associated with this argument.
     *
     * @param id the ID to set
     * @author Tony
     */
    public void setId(String id) {
        this.id = id; // Set the ID
    }

    /**
     * Sets the payload associated with this argument.
     *
     * @param payload the payload to set
     * @author ben
     */
    public void setPayload(String payload) {
        this.payload = payload; // Set the payload
    }

    /**
     * toString function used for testing
     *
     * @author Theo
     */
    public String toString() {
        // Return a string representation of the Argument object
        return "Argument{publisher=" + publisher + ", sheet=" + sheet + ", id=" + id + ", payload='" + payload + "'}";
    }
}
