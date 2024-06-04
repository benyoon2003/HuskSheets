package org.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HomeTest {
    private Home home;

    @BeforeEach
    public void init() {
        this.home = new Home();
    }

    @Test
    public void testReadXML() {
        Spreadsheet sheet = this.home.readXML("..\\sheets\\testSheet.xml");

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
    public void testSaveSheet() {
    }

    // @Test
    // public void testReadPayload() {
    // IAppUser user = new AppUser();
    // user.setUsername("test");
    // user.setPassword("test");
    // ServerEndpoint se = new ServerEndpoint();

    // try {
    // se.register(user);
    // se.createSheet("Test");
    // se.updatePublished("test", "Test", "Payload update");
    // se.updateSubscription("test", "Test", "Payload update");
    // this.home.readPayload(user, se, "Test");
    // } catch (Exception e) {
    // }
    // }

    @Test
    public void testWriteXML() {
        Spreadsheet sheet = new Spreadsheet("Test");
        sheet.setCellValue(0, 0, "0");
        sheet.setCellValue(1, 0, "1");
        sheet.setCellValue(2, 0, "2");
        sheet.setCellValue(3, 0, "3");
        sheet.setCellValue(4, 0, "4");

        String path = "..\\sheets\\test.xml";
        this.home.writeXML(sheet, path);

        File file = new File(path);
        assertTrue(file.exists());

        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                line.trim();
            }
            assertTrue(lines.contains("<sheet name=\"test\">"));
            assertTrue(lines.contains("    <cell col=\"0\" row=\"0\">0</cell>"));
            assertTrue(lines.contains("    <cell col=\"0\" row=\"1\">1</cell>"));
        } catch (IOException e) {

        }
    }
}
