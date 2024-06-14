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
        this.home = new Home();
    }

    /**
     * Tests the readXML method with a valid XML file.
     * @throws IOException if there is an I/O error
     */
    @Test
    public void testReadXML() throws IOException {
        File file = new File("..\\sheets\\readTestSheet.xml");
        ISpreadsheet sheet = this.home.readXML(file.getAbsolutePath());

        assertEquals("6", sheet.getCellValue(0, 0));
        assertEquals("7", sheet.getCellValue(1, 0));
        assertEquals("8", sheet.getCellValue(2, 0));
        assertEquals("9", sheet.getCellValue(3, 0));
        assertEquals("10", sheet.getCellValue(4, 0));
        assertEquals("11", sheet.getCellValue(5, 0));
        assertEquals("12", sheet.getCellValue(6, 0));
        assertEquals("13", sheet.getCellValue(7, 0));

        assertEquals("", sheet.getCellValue(0, 1));
    }

    /**
     * Tests the readXML method with a bad file path.
     */
    @Test
    public void testReadXMLBadFilepath() {
        File file = new File("does not exist");
        assertNull(this.home.readXML(file.getAbsolutePath()));
    }

    /**
     * Tests the readPayload method with a valid payload.
     */
    @Test
    public void testReadPayload() {
        String payload = "$A1 00\\n$B1 01\\n$A2 10\\n$B2 11\\n";
        String serverSheet = "serverSheetTest";

        ISpreadsheet sheet = this.home.readPayload(payload, serverSheet);

        assertEquals(serverSheet, sheet.getName());
        assertEquals("00", sheet.getCellValue(0, 0));
        assertEquals("01", sheet.getCellValue(0, 1));
        assertEquals("10", sheet.getCellValue(1, 0));
        assertEquals("11", sheet.getCellValue(1, 1));
    }

    /**
     * Tests the writeXML method with a valid sheet and path.
     */
    @Test
    public void testWriteXML() {
        Spreadsheet sheet = new Spreadsheet("Test");
        sheet.setCellValue(0, 0, "0");
        sheet.setCellValue(1, 0, "1");

        String path = "..\\sheets\\writeTestSheet.xml";
        this.home.writeXML(sheet, path);

        File file = new File(path);
        assertTrue(file.exists());

        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                line.trim();
            }
            assertTrue(lines.contains("<sheet name=\"writeTestSheet\">"));
            assertTrue(lines.contains("    <cell col=\"0\" row=\"0\">0</cell>"));
            assertTrue(lines.contains("    <cell col=\"0\" row=\"1\">1</cell>"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the writeXML method with a trimmed path.
     */
    @Test
    public void testWriteXMLPathTrim() {
        Spreadsheet sheet = new Spreadsheet("Test");
        sheet.setCellValue(0, 0, "0");
        sheet.setCellValue(1, 0, "1");

        String path = "  ..\\sheets\\writeTestSheet.xml  ";
        this.home.writeXML(sheet, path.trim());

        File file = new File(path.trim());
        assertTrue(file.exists());

        try {
            List<String> lines = Files.readAllLines(Paths.get(path.trim()));
            for (String line : lines) {
                line.trim();
            }
            assertTrue(lines.contains("<sheet name=\"writeTestSheet\">"));
            assertTrue(lines.contains("    <cell col=\"0\" row=\"0\">0</cell>"));
            assertTrue(lines.contains("    <cell col=\"0\" row=\"1\">1</cell>"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the writeXML method with a path that has a different extension.
     */
    @Test
    public void testWriteXMLWithDifferentExtension() {
        Spreadsheet sheet = new Spreadsheet("Test");
        sheet.setCellValue(0, 0, "0");
        sheet.setCellValue(1, 0, "1");

        String path = "..\\sheets\\writeTestSheet";
        this.home.writeXML(sheet, path);

        File file = new File(path + ".xml");
        assertTrue(file.exists());

        try {
            List<String> lines = Files.readAllLines(Paths.get(path + ".xml"));
            for (String line : lines) {
                line.trim();
            }
            assertTrue(lines.contains("<sheet name=\"writeTestSheet\">"));
            assertTrue(lines.contains("    <cell col=\"0\" row=\"0\">0</cell>"));
            assertTrue(lines.contains("    <cell col=\"0\" row=\"1\">1</cell>"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the convertStringTo2DArray method with a valid input string.
     */
    @Test
    public void testConvertStringTo2DArray() {
        String input = "$A1 00\n$B1 01\n$A2 10\n$B2 11\n";
        List<List<String>> result = Home.convertStringTo2DArray(input);

        assertEquals(4, result.size());
        assertEquals("0", result.get(0).get(0));
        assertEquals("0", result.get(0).get(1));
        assertEquals("00", result.get(0).get(2));

        assertEquals("0", result.get(1).get(0));
        assertEquals("1", result.get(1).get(1));
        assertEquals("01", result.get(1).get(2));

        assertEquals("1", result.get(2).get(0));
        assertEquals("0", result.get(2).get(1));
        assertEquals("10", result.get(2).get(2));

        assertEquals("1", result.get(3).get(0));
        assertEquals("1", result.get(3).get(1));
        assertEquals("11", result.get(3).get(2));
    }

    /**
     * Tests the convertStringTo2DArray method with a null input string.
     */
    @Test
    public void testConvertStringTo2DArrayWithNullInput() {
        String input = null;
        List<List<String>> result = Home.convertStringTo2DArray(input);

        assertTrue(result.isEmpty());
    }

    /**
     * Tests the convertStringTo2DArray method with an empty input string.
     */
    @Test
    public void testConvertStringTo2DArrayWithEmptyInput() {
        String input = "";
        List<List<String>> result = Home.convertStringTo2DArray(input);

        assertTrue(result.isEmpty());
    }

    /**
     * Tests the convertStringTo2DArray method with an input string containing an empty line.
     */
    @Test
    public void testConvertStringTo2DArrayWithEmptyLine() {
        String input = "$A1 00\n\n$B1 01\n$A2 10\n$B2 11\n";
        List<List<String>> result = Home.convertStringTo2DArray(input);

        assertEquals(4, result.size());
        assertEquals("0", result.get(0).get(0));
        assertEquals("0", result.get(0).get(1));
        assertEquals("00", result.get(0).get(2));

        assertEquals("0", result.get(1).get(0));
        assertEquals("1", result.get(1).get(1));
        assertEquals("01", result.get(1).get(2));

        assertEquals("1", result.get(2).get(0));
        assertEquals("0", result.get(2).get(1));
        assertEquals("10", result.get(2).get(2));

        assertEquals("1", result.get(3).get(0));
        assertEquals("1", result.get(3).get(1));
        assertEquals("11", result.get(3).get(2));
    }

    /**
     * Tests the convertStringTo2DArray method with an input string containing an invalid parts length.
     */
    @Test
    public void testConvertStringTo2DArrayWithInvalidPartsLength() {
        String input = "$A1 00\n$B1\n$A2 10\n$B2 11\n";
        List<List<String>> result = Home.convertStringTo2DArray(input);

        assertEquals(3, result.size());
        assertEquals("0", result.get(0).get(0));
        assertEquals("0", result.get(0).get(1));
        assertEquals("00", result.get(0).get(2));

        assertEquals("1", result.get(1).get(0));
        assertEquals("0", result.get(1).get(1));
        assertEquals("10", result.get(1).get(2));

        assertEquals("1", result.get(2).get(0));
        assertEquals("1", result.get(2).get(1));
        assertEquals("11", result.get(2).get(2));
    }

    /**
     * Tests the private trimEnds method using reflection.
     * @throws Exception if there is an error during reflection
     */
    @Test
    public void testTrimEnds() throws Exception {
        String path = "C:\\Users\\user\\Desktop\\file.xml";
        Method method = Home.class.getDeclaredMethod("trimEnds", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(home, path);
        assertEquals("file", result);

        path = "/home/user/file.xml";
        result = (String) method.invoke(home, path);
        assertEquals("file", result);

        path = "file.xml";
        result = (String) method.invoke(home, path);
        assertEquals("file", result);
    }

    /**
     * Tests the private trimEnds method without an extension using reflection.
     * @throws Exception if there is an error during reflection
     */
    @Test
    public void testTrimEndsWithoutExtension() throws Exception {
        String path = "C:\\Users\\user\\Desktop\\file";
        Method method = Home.class.getDeclaredMethod("trimEnds", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(home, path);
        assertEquals("file", result);

        path = "/home/user/file";
        result = (String) method.invoke(home, path);
        assertEquals("file", result);

        path = "file";
        result = (String) method.invoke(home, path);
        assertEquals("file", result);
    }

    /**
     * Tests the private convertRefToRowCol method using reflection.
     * @throws Exception if there is an error during reflection
     */
    @Test
    public void testConvertRefToRowCol() throws Exception {
        Method method = Home.class.getDeclaredMethod("convertRefToRowCol", String.class);
        method.setAccessible(true);

        int[] result = (int[]) method.invoke(null, "$A1");
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);

        result = (int[]) method.invoke(null, "$B2");
        assertEquals(1, result[0]);
        assertEquals(1, result[1]);

        result = (int[]) method.invoke(null, "$AA10");
        assertEquals(9, result[0]);
        assertEquals(26, result[1]);
    }
    
    /**
     * Tests the private convertRefToRowCol method with an invalid reference using reflection.
     * @throws Exception if there is an error during reflection
     */
    @Test
    public void testConvertRefToRowColInvalidRef() throws Exception {
        Method method = Home.class.getDeclaredMethod("convertRefToRowCol", String.class);
        method.setAccessible(true);

        int[] result = (int[]) method.invoke(null, "$1");
        assertEquals(0, result[0]);
        assertEquals(-1, result[1]);

        result = (int[]) method.invoke(null, "$A");
        assertEquals(-1, result[0]);
        assertEquals(0, result[1]);
    }
    
    /**
     * Tests the writeXML method with an invalid path.
     */
    @Test
    public void testWriteXMLWithInvalidPath() {
        Spreadsheet sheet = new Spreadsheet("Test");
        sheet.setCellValue(0, 0, "0");
        sheet.setCellValue(1, 0, "1");

        String path = "invalidPath\\writeTestSheet.xml";
        try {
            this.home.writeXML(sheet, path);
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }
    }
}
