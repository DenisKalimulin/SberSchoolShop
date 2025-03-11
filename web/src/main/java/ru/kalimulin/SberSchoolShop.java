package ru.kalimulin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SberSchoolShop {
    public static void main(String[] args) {
        SpringApplication.run(SberSchoolShop.class, args);
    }
}