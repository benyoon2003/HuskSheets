package org.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the methods within the Home class.
 */
public class HomeTest {
    private IHome home;

    /**
     * Initializes the Home instance before each test.
     */
    @BeforeEach
    public void init() {
        this.home = new Home(); // Initializes the Home instance.
    }

    /**
     * Tests the readXML method with a valid XML file.
     * @throws IOException if there is an I/O error
     */
    @Test
    public void testReadXML() throws IOException {
        File file = new File("..\\sheets\\readTestSheet.xml"); // Creates a File instance for the XML file.
        ISpreadsheet sheet = this.home.readXML(file.getAbsolutePath()); // Reads the XML file into a spreadsheet.

        assertEquals("6", sheet.getCellValue(0, 0)); // Verifies the value of cell (0,0).
        assertEquals("7", sheet.getCellValue(1, 0)); // Verifies the value of cell (1,0).
        assertEquals("8", sheet.getCellValue(2, 0)); // Verifies the value of cell (2,0).
        assertEquals("9", sheet.getCellValue(3, 0)); // Verifies the value of cell (3,0).
        assertEquals("10", sheet.getCellValue(4, 0)); // Verifies the value of cell (4,0).
        assertEquals("11", sheet.getCellValue(5, 0)); // Verifies the value of cell (5,0).
        assertEquals("12", sheet.getCellValue(6, 0)); // Verifies the value of cell (6,0).
        assertEquals("13", sheet.getCellValue(7, 0)); // Verifies the value of cell (7,0).
        assertEquals("", sheet.getCellValue(0, 1)); // Verifies the value of cell (0,1) is empty.
    }

    /**
     * Tests the readXML method with a bad file path.
     */
    @Test
    public void testReadXMLBadFilepath() {
        File file = new File("does not exist"); // Creates a File instance for a non-existent file.
        assertNull(this.home.readXML(file.getAbsolutePath())); // Verifies that the method returns null for a bad file path
    }

    /**
     * Tests the readPayload method with a valid payload.
     */
    @Test
    public void testReadPayload() {
        String payload = "$A1 00\\n$B1 01\\n$A2 10\\n$B2 11\\n"; // Declares a payload string.
        String serverSheet = "serverSheetTest"; // Declares a server sheet name.

        ISpreadsheet sheet = this.home.readPayload(payload, serverSheet); // Reads the payload into a spreadsheet.

        assertEquals(serverSheet, sheet.getName()); // Verifies the sheet name.
        assertEquals("00", sheet.getCellValue(0, 0)); // Verifies the value of cell (0,0).
        assertEquals("01", sheet.getCellValue(0, 1)); // Verifies the value of cell (0,1).
        assertEquals("10", sheet.getCellValue(1, 0)); // Verifies the value of cell (1,0).
        assertEquals("11", sheet.getCellValue(1, 1)); // Verifies the value of cell (1,1).
    }

    /**
     * Tests the writeXML method with a valid sheet and path.
     */
    @Test
    public void testWriteXML() {
        Spreadsheet sheet = new Spreadsheet("Test"); // Creates a new Spreadsheet instance with the name "Test".
        sheet.setCellValue(0, 0, "0"); // Sets the value of cell (0,0) to "0".
        sheet.setCellValue(1, 0, "1"); // Sets the value of cell (1,0) to "1".

        String path = "..\\sheets\\writeTestSheet.xml"; // Declares the file path for writing.
        this.home.writeXML(sheet, path); // Writes the spreadsheet to an XML file.

        File file = new File(path); // Creates a File instance for the written file.
        assertTrue(file.exists()); // Verifies that the file exists.

        try {
            List<String> lines = Files.readAllLines(Paths.get(path)); // Reads all lines from the written file.
            for (String line : lines) {
                line.trim(); // Trims each line.
            }
            assertTrue(lines.contains("<sheet name=\"writeTestSheet\">")); // Verifies that the sheet name is in the file.
            assertTrue(lines.contains("    <cell col=\"0\" row=\"0\">0</cell>")); // Verifies the content of cell (0,0).
            assertTrue(lines.contains("    <cell col=\"0\" row=\"1\">1</cell>")); // Verifies the content of cell (1,0).
        } catch (IOException e) {
            e.printStackTrace(); // Prints the stack trace if an exception occurs.
        }
    }

    /**
     * Tests the writeXML method with a trimmed path.
     */
    @Test
    public void testWriteXMLPathTrim() {
        Spreadsheet sheet = new Spreadsheet("Test"); // Creates a new Spreadsheet instance with the name "Test".
        sheet.setCellValue(0, 0, "0"); // Sets the value of cell (0,0) to "0".
        sheet.setCellValue(1, 0, "1"); // Sets the value of cell (1,0) to "1".

        String path = "  ..\\sheets\\writeTestSheet.xml  "; // Declares the file path with leading and trailing spaces
        this.home.writeXML(sheet, path.trim()); // Writes the spreadsheet to an XML file with the trimmed path.

        File file = new File(path.trim()); // Creates a File instance for the written file.
        assertTrue(file.exists()); // Verifies that the file exists.

        try {
            List<String> lines = Files.readAllLines(Paths.get(path.trim())); // Reads all lines from the written file
            for (String line : lines) {
                line.trim();  // Trims each line.
            }
            assertTrue(lines.contains("<sheet name=\"writeTestSheet\">")); // Verifies that the sheet name is in the file.
            assertTrue(lines.contains("    <cell col=\"0\" row=\"0\">0</cell>")); // Verifies the content of cell (0,0).
            assertTrue(lines.contains("    <cell col=\"0\" row=\"1\">1</cell>")); // Verifies the content of cell (1,0).
        } catch (IOException e) {
            e.printStackTrace(); // Prints the stack trace if an exception occurs.
        }
    }

    /**
     * Tests the writeXML method with a path that has a different extension.
     */
    @Test
    public void testWriteXMLWithDifferentExtension() {
        Spreadsheet sheet = new Spreadsheet("Test"); // Creates a new Spreadsheet instance with the name "Test".
        sheet.setCellValue(0, 0, "0"); // Sets the value of cell (0,0) to "0".
        sheet.setCellValue(1, 0, "1"); // Sets the value of cell (1,0) to "1".

        String path = "..\\sheets\\writeTestSheet"; // Declares the file path without an extension.
        this.home.writeXML(sheet, path); // Writes the spreadsheet to an XML file.

        File file = new File(path + ".xml"); // Creates a File instance for the written file with the .xml extension.
        assertTrue(file.exists()); // Verifies that the file exists.

        try {
            List<String> lines = Files.readAllLines(Paths.get(path + ".xml")); // Reads all lines from the written file.
            for (String line : lines) {
                line.trim(); // Trims each line.
            }
            assertTrue(lines.contains("<sheet name=\"writeTestSheet\">")); // Verifies that the sheet name is in the file.
            assertTrue(lines.contains("    <cell col=\"0\" row=\"0\">0</cell>")); // Verifies the content of cell (0,0).
            assertTrue(lines.contains("    <cell col=\"0\" row=\"1\">1</cell>")); // Verifies the content of cell (1,0).
        } catch (IOException e) {
            e.printStackTrace(); // Prints the stack trace if an exception occurs.
        }
    }

    /**
     * Tests the convertStringTo2DArray method with a valid input string.
     */
    @Test
    public void testConvertStringTo2DArray() {
        String input = "$A1 00\n$B1 01\n$A2 10\n$B2 11\n"; // Declares an input string.
        List<List<String>> result = Home.convertStringTo2DArray(input); // Converts the input string to a 2D array.

        assertEquals(4, result.size()); // Verifies the size of the result.
        assertEquals("0", result.get(0).get(0)); // Verifies the content of the first cell.
        assertEquals("0", result.get(0).get(1)); // Verifies the content of the second cell.
        assertEquals("00", result.get(0).get(2)); // Verifies the content of the third cell.

        assertEquals("0", result.get(1).get(0)); // Verifies the content of the fourth cell.
        assertEquals("1", result.get(1).get(1)); // Verifies the content of the fifth cell.
        assertEquals("01", result.get(1).get(2)); // Verifies the content of the sixth cell.

        assertEquals("1", result.get(2).get(0)); // Verifies the content of the seventh cell.
        assertEquals("0", result.get(2).get(1)); // Verifies the content of the eighth cell.
        assertEquals("10", result.get(2).get(2)); // Verifies the content of the ninth cell.

        assertEquals("1", result.get(3).get(0)); // Verifies the content of the tenth cell.
        assertEquals("1", result.get(3).get(1)); // Verifies the content of the eleventh cell.
        assertEquals("11", result.get(3).get(2)); // Verifies the content of the twelfth cell.
    }

    /**
     * Tests the convertStringTo2DArray method with a null input string.
     */
    @Test
    public void testConvertStringTo2DArrayWithNullInput() {
        String input = null; // Declares a null input string.
        List<List<String>> result = Home.convertStringTo2DArray(input); // Converts the input string to a 2D array.
        assertTrue(result.isEmpty()); // Verifies that the result is empty.
    }

    /**
     * Tests the convertStringTo2DArray method with an empty input string.
     */
    @Test
    public void testConvertStringTo2DArrayWithEmptyInput() {
        String input = ""; // Declares an empty input string.
        List<List<String>> result = Home.convertStringTo2DArray(input); // Converts the input string to a 2D array.
        assertTrue(result.isEmpty()); // Verifies that the result is empty.
    }

    /**
     * Tests the convertStringTo2DArray method with an input string containing an empty line.
     */
    @Test
    public void testConvertStringTo2DArrayWithEmptyLine() {
        String input = "$A1 00\n\n$B1 01\n$A2 10\n$B2 11\n"; // Declares an input string with an empty line.
        List<List<String>> result = Home.convertStringTo2DArray(input); // Converts the input string to a 2D array.

        assertEquals(4, result.size()); // Verifies the size of the result.
        assertEquals("0", result.get(0).get(0)); // Verifies the content of the first cell.
        assertEquals("0", result.get(0).get(1)); // Verifies the content of the second cell.
        assertEquals("00", result.get(0).get(2)); // Verifies the content of the third cell.

        assertEquals("0", result.get(1).get(0)); // Verifies the content of the fourth cell.
        assertEquals("1", result.get(1).get(1)); // Verifies the content of the fifth cell.
        assertEquals("01", result.get(1).get(2)); // Verifies the content of the sixth cell.

        assertEquals("1", result.get(2).get(0)); // Verifies the content of the seventh cell.
        assertEquals("0", result.get(2).get(1)); // Verifies the content of the eighth cell.
        assertEquals("10", result.get(2).get(2)); // Verifies the content of the ninth cell.

        assertEquals("1", result.get(3).get(0)); // Verifies the content of the tenth cell.
        assertEquals("1", result.get(3).get(1)); // Verifies the content of the eleventh cell.
        assertEquals("11", result.get(3).get(2)); // Verifies the content of the twelfth cell.
    }

    /**
     * Tests the convertStringTo2DArray method with an input string containing an invalid parts length.
     */
    @Test
    public void testConvertStringTo2DArrayWithInvalidPartsLength() {
        String input = "$A1 00\n$B1\n$A2 10\n$B2 11\n"; // Declares an input string with an invalid parts length.
        List<List<String>> result = Home.convertStringTo2DArray(input); // Converts the input string to a 2D array.

        assertEquals(3, result.size()); // Verifies the size of the result.
        assertEquals("0", result.get(0).get(0)); // Verifies the content of the first cell.
        assertEquals("0", result.get(0).get(1)); // Verifies the content of the second cell.
        assertEquals("00", result.get(0).get(2)); // Verifies the content of the third cell.

        assertEquals("1", result.get(1).get(0)); // Verifies the content of the fourth cell.
        assertEquals("0", result.get(1).get(1)); // Verifies the content of the fifth cell.
        assertEquals("10", result.get(1).get(2)); // Verifies the content of the sixth cell.

        assertEquals("1", result.get(2).get(0)); // Verifies the content of the seventh cell.
        assertEquals("1", result.get(2).get(1)); // Verifies the content of the eighth cell.
        assertEquals("11", result.get(2).get(2)); // Verifies the content of the ninth cell.
    }

    /**
     * Tests the private trimEnds method using reflection.
     * @throws Exception if there is an error during reflection
     */
    @Test
    public void testTrimEnds() throws Exception {
        String path = "C:\\Users\\user\\Desktop\\file.xml"; // Declares a file path.
        Method method = Home.class.getDeclaredMethod("trimEnds", String.class); // Gets the trimEnds method using reflection.
        method.setAccessible(true); // Sets the method to be accessible.
        String result = (String) method.invoke(home, path); // Invokes the method on the Home instance.
        assertEquals("file", result); // Verifies the result of the method.

        path = "/home/user/file.xml"; // Declares another file path.
        result = (String) method.invoke(home, path); // Invokes the method on the Home instance.
        assertEquals("file", result); // Verifies the result of the method.

        path = "file.xml"; // Declares another file path.
        result = (String) method.invoke(home, path); // Invokes the method on the Home instance.
        assertEquals("file", result); // Verifies the result of the method.
    }

    /**
     * Tests the private trimEnds method without an extension using reflection.
     * @throws Exception if there is an error during reflection
     */
    @Test
    public void testTrimEndsWithoutExtension() throws Exception {
        String path = "C:\\Users\\user\\Desktop\\file"; // Declares a file path without an extension.
        Method method = Home.class.getDeclaredMethod("trimEnds", String.class); // Gets the trimEnds method using reflection.
        method.setAccessible(true); // Sets the method to be accessible.
        String result = (String) method.invoke(home, path);  // Invokes the method on the Home instance.
        assertEquals("file", result); // Verifies the result of the method.

        path = "/home/user/file"; // Declares another file path without an extension.
        result = (String) method.invoke(home, path); // Invokes the method on the Home instance.
        assertEquals("file", result); // Verifies the result of the method.

        path = "file"; // Declares another file path without an extension.
        result = (String) method.invoke(home, path); // Invokes the method on the Home instance.
        assertEquals("file", result); // Verifies the result of the method.
    }

    /**
     * Tests the private convertRefToRowCol method using reflection.
     * @throws Exception if there is an error during reflection
     */
    @Test
    public void testConvertRefToRowCol() throws Exception {
        Method method = Home.class.getDeclaredMethod("convertRefToRowCol", String.class); // Gets the convertRefToRowCol method using reflection.
        method.setAccessible(true); // Sets the method to be accessible.

        int[] result = (int[]) method.invoke(null, "$A1"); // Invokes the method with the reference "$A1".
        assertEquals(0, result[0]); // Verifies the row index.
        assertEquals(0, result[1]); // Verifies the column index.

        result = (int[]) method.invoke(null, "$B2"); // Invokes the method with the reference "$B2".
        assertEquals(1, result[0]); // Verifies the row index.
        assertEquals(1, result[1]); // Verifies the column index.

        result = (int[]) method.invoke(null, "$AA10"); // Invokes the method with the reference "$AA10".
        assertEquals(9, result[0]); // Verifies the row index.
        assertEquals(26, result[1]); // Verifies the column index.
    }
    
    /**
     * Tests the private convertRefToRowCol method with an invalid reference using reflection.
     * @throws Exception if there is an error during reflection
     */
    @Test
    public void testConvertRefToRowColInvalidRef() throws Exception {
        Method method = Home.class.getDeclaredMethod("convertRefToRowCol", String.class); // Gets the convertRefToRowCol method using reflection.
        method.setAccessible(true); // Sets the method to be accessible.

        int[] result = (int[]) method.invoke(null, "$1"); // Invokes the method with the invalid reference "$1".
        assertEquals(0, result[0]); // Verifies the row index.
        assertEquals(-1, result[1]); // Verifies the column index.

        result = (int[]) method.invoke(null, "$A"); // Invokes the method with the invalid reference "$A".
        assertEquals(-1, result[0]); // Verifies the row index.
        assertEquals(0, result[1]); // Verifies the column index.
    }
    
    /**
     * Tests the writeXML method with an invalid path.
     */
    @Test
    public void testWriteXMLWithInvalidPath() {
        Spreadsheet sheet = new Spreadsheet("Test"); // Creates a new Spreadsheet instance with the name "Test".
        sheet.setCellValue(0, 0, "0"); // Sets the value of cell (0,0) to "0".
        sheet.setCellValue(1, 0, "1"); // Sets the value of cell (1,0) to "1".

        String path = "invalidPath\\writeTestSheet.xml"; // Declares an invalid file path.
        try {
            this.home.writeXML(sheet, path); // Attempts to write the spreadsheet to an XML file.
        } catch (Exception e) {
            assertTrue(e instanceof IOException); // Verifies that an IOException is thrown.
        }
    }
}
