package com.example.spring_mvc_lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class AboutController {

    @GetMapping("/about")
    public String about(Model model) {

        model.addAttribute("appName", "Spring MVC Lab");
        model.addAttribute("version", "1.0");
        model.addAttribute("author", "Rachel Ratna Sari");

        List<String> technologies = Arrays.asList(
                "Spring Boot",
                "Thymeleaf",
                "Bootstrap",
                "Java 25"
        );

        model.addAttribute("technologies", technologies);

        return "about";
    }
}