package com.smartiadev.api_gateway.security;

import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange

                        // =========================
                        // 🔓 SWAGGER PUBLIC
                        // =========================
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                        // =========================
                        // 🔓 MICROSERVICES DOCS
                        // =========================
                        .pathMatchers(
                                "/auth-service/v3/api-docs",
                                "/item-service/v3/api-docs",
                                "/rental-service/v3/api-docs",
                                "/review-service/v3/api-docs",
                                "/payments-service/v3/api-docs",
                                "/dispute-service/v3/api-docs",
                                "/notif-service/v3/api-docs"
                        ).permitAll()

                        // =========================
                        // 🔓 AUTH PUBLIC
                        // =========================
                        .pathMatchers(
                                "/api/auth/**",
                                "/api/profile/**"
                        ).permitAll()

                        // =========================
                        // 🔐 ADMIN
                        // =========================
                        .pathMatchers("/api/admin/**").hasRole("ADMIN")

                        // =========================
                        // 🔐 AUTHENTIFIED
                        // =========================
                        .pathMatchers(
                                "/api/items/**",
                                "/api/rentals/**",
                                "/api/reviews/**",
                                "/api/notifications/**",
                                "/api/disputes/**",
                                "/api/payments/**"
                        ).authenticated()

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles"); // ton claim JWT pour les rôles
        authoritiesConverter.setAuthorityPrefix("");             // enlève "ROLE_" par défaut

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }
}