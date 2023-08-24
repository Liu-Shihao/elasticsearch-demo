package com.lsh.controller;

import com.lsh.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@Slf4j
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/")
    public String index(){
        return "Welcome to Search Demo.";
    }



    @GetMapping("/search")
    public Map<String,Object> search(@RequestParam("q") String q) throws IOException {
        return searchService.search(q);
    }
}
