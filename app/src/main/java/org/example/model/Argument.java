package org.example.model;

public class Argument {

  String publisher, sheet, id, payload;

  public Argument(String publisher, String sheet, String id, String payload) {
    this.publisher = publisher;
    this.sheet = sheet;
    this.id = id;
    this.payload = payload;
  }

  public Argument() {

  }

  public String getPublisher() {
    return this.publisher;
  }

  public String getSheet() {
    return this.sheet;
  }

  public String getId() {
    return this.id;
  }

  public String getPayload() {
    return this.payload;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public void setSheet(String sheet) {
    this.sheet = sheet;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }
}
