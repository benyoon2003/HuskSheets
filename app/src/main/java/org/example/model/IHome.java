package org.example.model;

/**
 * Represents the interface for home operations such as choosing a publisher and sheet to subscribe
 * to or opening a saved sheet.
 */
public interface IHome {

    /**
     * Reads an XML file from the specified path and returns a Spreadsheet object.
     *
     * @param path the path of the XML file to read.
     * @return a ISpreadsheet representing the data from the XML file.
     * @author Theo
     */
    ISpreadsheet readXML(String path);

    /**
     * Reads a payload associated with a given user and selected sheet, and returns a spreadsheet model.
     *
     * @param payload   payload to be parsed.
     * @param sheetName name of sheet
     * @return a ISpreadsheet representing the data from the payload.
     * @author Tony
     */
    ISpreadsheet readPayload(String payload, String sheetName);

    /**
     * Writes the given read-only spreadsheet to an XML file at the specified path.
     *
     * @param sheet the read-only spreadsheet to write.
     * @param path  the path where the XML file will be written.
     * @author Theo
     */
    void writeXML(IReadOnlySpreadSheet sheet, String path);
}
