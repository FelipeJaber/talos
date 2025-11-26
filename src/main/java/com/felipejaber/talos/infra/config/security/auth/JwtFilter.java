package com.felipejaber.talos.infra.config.security.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter extends AbstractAuthenticationProcessingFilter {

    private final static String HEADER = "Authorization";
    private final static String PREFIX = "Bearer ";

    public JwtFilter(AuthenticationManager authenticationManager) {
        super(new RequestHeaderRequestMatcher(HEADER));
        setAuthenticationManager(authenticationManager);
    }

    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        String header = request.getHeader(HEADER);

        if(header.isEmpty() || !header.startsWith(PREFIX)){
            return null;
        }

        String rawToken = header.substring(PREFIX.length());

        //Not authenticated yet
        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(rawToken);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException{

        //seta no SecurityContext automaticamente
        super.successfulAuthentication(request, response, chain, authResult);

        chain.doFilter(request, response);
    }

}
