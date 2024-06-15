package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * The main application class for HuskSheets, a Spring Boot application.
 * @author Tony
 */
@SpringBootApplication
public class HuskSheetsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuskSheetsApplication.class, args);
    }

    /**
     * Customizes the embedded Tomcat web server factory to use a port specified by the "PORT" environment variable.
     *
     * @return a customizer for the TomcatServletWebServerFactory
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            String port = System.getenv("PORT");
            if (port != null) {
                factory.setPort(Integer.parseInt(port));
            }
        };
    }
}

