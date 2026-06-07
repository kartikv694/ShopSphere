package com.Infosys.ecommerceApplication.config;

import java.util.Arrays;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private EnvConfig envConfig;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config =
                new CorsConfiguration();

        String configuredOrigins =
                envConfig.get("FRONTEND_ORIGINS");

        if (configuredOrigins.isBlank()) {
            configuredOrigins = envConfig.get("FRONTEND_URL");
        }

        List<String> allowedOrigins =
                configuredOrigins.isBlank()
                        ? List.of("http://localhost:5173")
                        : Arrays.stream(configuredOrigins.split(","))
                                .map(String::trim)
                                .filter(origin -> !origin.isBlank())
                                .toList();

        config.setAllowedOrigins(
                allowedOrigins
        );

        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        config.setAllowedHeaders(
                List.of("*")
        );

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> {})

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // PUBLIC
                .requestMatchers(
                        "/api/auth/**"
                ).permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/products/**"
                ).permitAll()

                .requestMatchers(
                        "/api/cart/**"
                ).permitAll()

                // ORDERS
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/orders/checkout"
                ).hasRole("CUSTOMER")

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/orders/my-orders"
                ).hasRole("CUSTOMER")

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/orders",
                        "/api/orders/all"
                ).hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/orders/*/status"
                ).hasRole("ADMIN")

                .requestMatchers(
                        "/api/payments/**"
                ).hasRole("CUSTOMER")

                .requestMatchers(
                        "/api/user/**"
                ).hasAnyRole(
                        "CUSTOMER",
                        "ADMIN"
                )

                // ADMIN ONLY
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/products/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/products/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/products/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                        "/api/dashboard/**"
                ).hasRole("ADMIN")

                // EVERYTHING ELSE
                .anyRequest().authenticated()

            )

            .addFilterBefore(
                    jwtFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();

    }

}
