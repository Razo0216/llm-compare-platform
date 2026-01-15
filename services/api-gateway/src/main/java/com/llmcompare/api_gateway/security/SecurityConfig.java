package com.llmcompare.api_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain security(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                // Allow actuator without token
                .pathMatchers("/actuator/**").permitAll()

                // (Optional) allow OPTIONS for CORS preflight (useful later with UI)
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Everything else requires JWT
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt())
            .build();
    }
}
