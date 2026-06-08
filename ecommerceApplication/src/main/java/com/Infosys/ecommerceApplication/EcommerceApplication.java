package com.Infosys.ecommerceApplication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.Infosys")
public class EcommerceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(EcommerceApplication.class, args);
    }
}