package com.Infosys.ecommerceApplication.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Infosys.ecommerceApplication.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        String token = null;
        String username = null;

        // EXTRACT TOKEN
        if (
            authHeader != null &&
            authHeader.startsWith("Bearer ")
        ) {

            token = authHeader.substring(7);

            try {

                username =
                        jwtUtil.extractUsername(token);

                System.out.println(
                        "USERNAME FROM TOKEN: " +
                        username
                );

            }

            catch (Exception e) {

                System.out.println(
                        "INVALID JWT: " +
                        e.getMessage()
                );

            }

        }

        // VALIDATE TOKEN
        if (
            username != null &&
            SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null
        ) {

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(username);

            System.out.println(
                    "AUTHORITIES: " +
                    userDetails.getAuthorities()
            );

            boolean isValid =
                    jwtUtil.validateToken(
                            token,
                            userDetails.getUsername()
                    );

            System.out.println(
                    "TOKEN VALID: " +
                    isValid
            );

            if (isValid) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);

                System.out.println(
                        "AUTHENTICATION SET SUCCESSFULLY"
                );

            }

        }

        filterChain.doFilter(
                request,
                response
        );

    }

}