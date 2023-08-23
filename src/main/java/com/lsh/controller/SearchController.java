package com.lsh.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SearchController {

    @GetMapping("/")
    public String index(){
        return "Welcome to Search Demo.";
    }
}
