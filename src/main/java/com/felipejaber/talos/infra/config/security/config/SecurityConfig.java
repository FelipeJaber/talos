package com.felipejaber.talos.infra.config.security.config;

import com.felipejaber.talos.infra.config.security.auth.JwtAuthenticationProvider;
import com.felipejaber.talos.infra.config.security.auth.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {


    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    public SecurityConfig(JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(jwtAuthenticationProvider);
    }

    @Bean
    public JwtFilter jwtFilter(AuthenticationManager authManager){
        return new JwtFilter(authManager);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/swagger",
                                //LOCAL ONLY -- CHANGE-ME ON PROD
                                "/api-docs/**",
                                "/api/swagger-ui/**",
                                "/api/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(
                                "/api/**").hasRole("ADMIN")
                )
                .addFilterBefore(jwtFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
