package com.github.ridicuturing.guard.controller.api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class Hello {
    @RequestMapping("ping")
    public String ping(){
        return "ping";
    }
}
