package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The HuskSheetsApplication class serves as the entry point for the Husk Sheets application.
 * It initializes and runs the Spring Boot application.
 */
@SpringBootApplication
public class HuskSheetsApplication {
    
    /**
     * The main method is the entry point of the application.
     * It uses Spring Boot's SpringApplication.run() method to launch the application.
     *
     * @param args command line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(HuskSheetsApplication.class, args);
    }
}
