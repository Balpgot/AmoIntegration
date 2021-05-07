package com.sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        if(!PropertiesStorage.isLoaded) {
            PropertiesStorage.loadProperties();
        }
        SpringApplication.run(DemoApplication.class, args);
    }

}
