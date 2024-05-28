package org.example.model;

public class SheetDTO {
    private String filename;
    private String content;

    public SheetDTO(String filename, String content) {
        this.filename = filename;
        this.content = content;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
