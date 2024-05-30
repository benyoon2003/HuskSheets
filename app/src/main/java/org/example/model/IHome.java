package org.example.model;

public interface IHome {
    Spreadsheet readXML(String path);  

    void saveSheet(ReadOnlySpreadSheet sheet, String path);

    ISpreadsheet readPayload(IAppUser user, String selectedSheet);
}