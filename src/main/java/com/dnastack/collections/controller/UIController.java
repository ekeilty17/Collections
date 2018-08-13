package com.dnastack.collections.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {
	
	@GetMapping("/createCollection")
    public String createCollection() {
        return "createCollection";
    }
}
