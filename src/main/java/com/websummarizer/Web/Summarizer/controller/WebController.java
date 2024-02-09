package com.websummarizer.Web.Summarizer.controller;

import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @SneakyThrows
    @PostMapping("/api/summary")
    String getSummary(Model model) {
        model.addAttribute("summaryText", "<< Data from WebController >>"); // data to send to html page
        //Thread.sleep(500); // testing server response delays - delete later
        return "api/summary"; // webpage to return
    }

}
