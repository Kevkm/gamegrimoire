package com.project.gamegrimoire.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GgController {
    @RequestMapping("/home")
    public String home() {
        return "Hello World!";
    }
}

