package org.example.model;

/**
 * Represents the interface for home operations such as reading and writing spreadsheets.
 */
public interface IHome {

    /**
     * Reads an XML file from the specified path and returns a Spreadsheet object.
     *
     * @param path the path of the XML file to read.
     * @return a Spreadsheet object representing the data from the XML file.
     */
    Spreadsheet readXML(String path);

    /**
     * Saves the given read-only spreadsheet to the specified path.
     *
     * @param sheet the read-only spreadsheet to save.
     * @param path the path where the spreadsheet will be saved.
     */
    void saveSheet(IReadOnlySpreadSheet sheet, String path);

    /**
     * Reads a payload associated with a given user and selected sheet, and returns a spreadsheet model.
     *
     * @param user the user associated with the payload.
     * @param selectedSheet the selected sheet to read the payload from.
     * @return a spreadsheet model representing the data from the payload.
     */
    ISpreadsheet readPayload(IAppUser user, ServerEndpoint se, String selectedSheet);

    /**
     * Writes the given read-only spreadsheet to an XML file at the specified path.
     *
     * @param sheet the read-only spreadsheet to write.
     * @param path the path where the XML file will be written.
     */
    void writeXML(IReadOnlySpreadSheet sheet, String path);
}
