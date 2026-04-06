package com.studentreview.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Îi spune lui Spring că aceasta clasă va returna date (ex: JSON)
public class HelloController {

    @GetMapping("/api/hello") // Mapează un request GET la adresa /api/hello
    public String sayHello() {
        return "Hello from the Spring Boot Server!";
    }
}
