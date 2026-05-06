package com.Infosys.ecommerceApplication.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

            	    // ✅ PUBLIC
            	    .requestMatchers("/api/auth/**").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
            	    .requestMatchers("/api/cart/**").permitAll()   // ✅ ADD THIS
            	    .requestMatchers("/api/products/all").permitAll()

            	    // 🔒 ADMIN ONLY
            	    .requestMatchers("/api/products/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
            	    .requestMatchers("/api/dashboard/**").hasRole("ADMIN")

            	    // 🔒 EVERYTHING ELSE
            	    .anyRequest().authenticated()
            	)
            
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}