package com.jam2in.arcus.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BoardController {

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("id", 1);
        model.addAttribute("name", "hello");
        return "home";
    }

}
