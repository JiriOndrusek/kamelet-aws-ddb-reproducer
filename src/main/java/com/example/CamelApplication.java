package com.example;

import org.apache.camel.main.Main;

public class CamelApplication {

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        // Configure Camel context
        main.configure().addRoutesBuilder(new TimerKameletRoute());

        // Load properties from application.properties
        main.setPropertyPlaceholderLocations("classpath:application.properties");

        // Run Camel
        main.run(args);
    }
}
