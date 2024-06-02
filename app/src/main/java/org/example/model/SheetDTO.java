package org.example.model;

public class SheetDTO {
    private String filename;
    private String content;
    private String publisher; // Add this field

    private String sheet;

    public SheetDTO(String filename, String content, String publisher , String sheet) {
        this.filename = filename;
        this.content = content;
        this.publisher = publisher; // Initialize this field
        this.sheet = sheet;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublisher() { // Add this getter method
        return publisher;
    }

    public void setPublisher(String publisher) { // Add this setter method
        this.publisher = publisher;
    }
}

