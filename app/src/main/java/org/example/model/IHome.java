package org.example.model;

public interface IHome {
    Spreadsheet readXML(String path);  

    void saveSheet(IReadOnlySpreadSheet sheet, String path);

    ISpreadsheet readPayload(IAppUser user, String selectedSheet);
    void writeXML(IReadOnlySpreadSheet sheet, String path);
}