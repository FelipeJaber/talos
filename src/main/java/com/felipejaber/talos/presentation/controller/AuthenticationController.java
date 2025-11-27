package com.felipejaber.talos.presentation.controller;

import com.felipejaber.talos.presentation.dtos.*;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/v1")
public class AuthenticationController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        AuthenticatedUserResponse response = new AuthenticatedUserResponse();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/set-password/{token}")
    public ResponseEntity<?> setPassword(@PathVariable String token, @RequestBody SetPasswordRequest request){
        AuthenticatedUserResponse response = new AuthenticatedUserResponse();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(){
        RefreshTokenResponse response = new RefreshTokenResponse();
        return ResponseEntity.ok(response);
    }

}
