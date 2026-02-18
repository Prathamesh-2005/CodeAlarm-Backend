package com.Jadhav.Contest_tracker.controller;

import com.Jadhav.Contest_tracker.service.CodeChefService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/codechef")
@CrossOrigin(origins = {
    "https://code-alarm-contest.vercel.app",
    "http://localhost:5173"
})
public class CodeChefController {

    @Autowired
    private CodeChefService codeChefService;

    @GetMapping("/fetch")
    public String fetch() {
        System.out.println("🎯 /api/codechef/fetch endpoint hit");
        return codeChefService.fetchCodeChefContestsManually();
    }
}
