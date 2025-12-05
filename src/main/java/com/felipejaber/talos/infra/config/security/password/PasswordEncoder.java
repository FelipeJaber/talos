package com.felipejaber.talos.infra.config.security.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(String password) {

        if(!isValidPassword(password)) throw new IllegalArgumentException("Invalid password");

        return encoder.encode(password);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;

        // Pelo menos 1 maiúscula, 1 minúscula, 1 número, 1 especial
        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$";

        return password.matches(pattern);
    }

}
