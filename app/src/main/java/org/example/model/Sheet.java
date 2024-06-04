package org.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Represents a Sheet entity with a name, content, and publisher.
 */
@Entity
@Table(name = "sheets")
public class Sheet {

    @Id
    private String name;

    @Lob
    private String content;

    @Column(name = "publisher")
    private String publisher;

    /**
     * Gets the name of the sheet.
     *
     * @return the name of the sheet.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the sheet.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
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
