package com.restclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class RestclientTutorialApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestclientTutorialApplication.class, args);
    }

}
