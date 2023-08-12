package com.example.Resturant.Management.System.service;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface BillService {

    ResponseEntity<String> generateReport(Map<String,Object> requestMap);
}
