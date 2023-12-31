package com.example.Resturant.Management.System.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/bill")
public interface BillRest {

    @PostMapping("/generateReport")
    ResponseEntity<String> generateReport(@RequestBody Map<String,Object> requestMap);
}
