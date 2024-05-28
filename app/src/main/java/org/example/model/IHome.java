package org.example.model;

public interface IHome {
    Spreadsheet readXML(String path);  

    void writeXML(ReadOnlySpreadSheet sheet, String path);
}