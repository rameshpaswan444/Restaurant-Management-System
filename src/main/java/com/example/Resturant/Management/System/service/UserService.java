package com.example.Resturant.Management.System.service;

import com.example.Resturant.Management.System.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {

    ResponseEntity<String> signup(Map<String,String> requestMap);
    ResponseEntity<String> login(Map<String,String> requestMap);
    ResponseEntity<List<UserWrapper>> getAllUser();
    ResponseEntity<String>update(Map<String,String>requestMap);

    ResponseEntity<String> checkToken();
    ResponseEntity<String> changePassword(Map<String,String> requestMap);
    ResponseEntity<String> forgotPassword(Map<String,String> requestMap);
}
