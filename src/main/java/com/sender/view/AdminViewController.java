package com.sender.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {

    public AdminViewController() {
    }

    @GetMapping(value = "/admin")
    public String mainPage() {
        return "index";
    }

}
