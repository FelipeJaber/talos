package com.felipejaber.talos.presentation.controller;

import com.felipejaber.talos.presentation.dtos.AuthRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/v1")
public class AuthenticationController {


    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        return ResponseEntity.ok().build();
    }
}
