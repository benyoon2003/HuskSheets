package org.example.model;

/**
 * Data Transfer Object (DTO) for representing sheet data.
 */
public class SheetDTO {
    private String filename;
    private String content;
    private String publisher; // Add this field

    private String sheet;

    /**
     * Constructs a new SheetDTO with the specified filename, content, publisher, and sheet.
     *
     * @param filename the name of the file.
     * @param content the content of the sheet.
     * @param publisher the publisher of the sheet.
     * @param sheet the sheet name or identifier.
     */
    public SheetDTO(String filename, String content, String publisher , String sheet) {
        this.filename = filename;
        this.content = content;
        this.publisher = publisher;
        this.sheet = sheet;
    }

    /**
     * Gets the sheet name or identifier.
     *
     * @return the sheet name or identifier.
     */
    public String getSheet() {
        return sheet;
    }

    /**
     * Sets the sheet name or identifier.
     *
     * @param sheet the sheet name or identifier to set.
     */
    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    /**
     * Gets the filename of the sheet.
     *
     * @return the filename of the sheet.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename of the sheet.
     *
     * @param filename the filename to set.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the content of the sheet.
     *
     * @return the content of the sheet.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the sheet.
     *
     * @param content the content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the publisher of the sheet.
     *
     * @return the publisher of the sheet.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher of the sheet.
     *
     * @param publisher the publisher to set.
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}

