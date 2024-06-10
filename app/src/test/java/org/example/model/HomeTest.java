package org.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class HomeTest {
    private IHome home;

    @BeforeEach
    public void init() {
        this.home = new Home();
    }

    @Test
    public void testReadXML() throws IOException {
        File file = new File("..\\sheets\\readTestSheet.xml");// new ClassPathResource(path).getFile();
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

    @Test
    public void testReadXMLBadFilepath() {
        File file = new File("does not exist");
        assertNull(this.home.readXML(file.getAbsolutePath()));
    }

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

        }
    }
}
