package com.example.falful.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/home")
    public String getHomepage(){
        return "about";
    }
}
