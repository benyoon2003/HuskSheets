package org.example.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigLoader is a utility class that loads configuration properties from a file.
 */
public class ConfigLoader {

    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            }
            // Load properties from the input stream
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the value of a property key from the loaded configuration properties.
     *
     * @param key the property key whose value is to be retrieved.
     * @return the value of the property key, or null if the key is not found.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
