package com.felipejaber.talos.presentation.controller;

import com.felipejaber.talos.application.dto.AuthenticatedUserData;
import com.felipejaber.talos.application.service.AuthService;
import com.felipejaber.talos.presentation.dtos.*;
import com.felipejaber.talos.presentation.mapper.AuthMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/v1")
public class AuthenticationController {

    private final AuthService authService;

    @Autowired
    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        AuthenticatedUserData authenticatedUserData = authService.authenticate(request.email(), request.password());
        AuthenticatedUserResponse response = AuthMapper.toResponse(authenticatedUserData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        authService.register(request.email());
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/set-password/{token}")
    public ResponseEntity<?> setPassword(@PathVariable String token, @RequestBody SetPasswordRequest request){

        if(!request.password().equals(request.confirmPassword())) throw new IllegalArgumentException("Passwords do not match");

        AuthenticatedUserData authenticatedUserData = authService.setPassword(token, request.password());
        AuthenticatedUserResponse response = AuthMapper.toResponse(authenticatedUserData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader){

        String refreshToken = authHeader.replace("Bearer ", "");
        AuthenticatedUserData authenticatedUserData = authService.refreshToken(refreshToken);

        AuthenticatedUserResponse response = AuthMapper.toResponse(authenticatedUserData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        authService.invalidateSession(authHeader);
        return ResponseEntity.ok().build();
    }

}
