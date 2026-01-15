package com.llmcompare.llm_orchestrator.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import reactor.core.publisher.Mono;

@Configuration
public class JwtRoleConverterConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }

    private static class JwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
        @Override
        public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
            return Mono.just(extractAuthorities(jwt));
        }

        private JwtAuthenticationToken extractAuthorities(Jwt jwt) {
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();

            Object realmAccess = jwt.getClaim("realm_access");
            if (realmAccess instanceof Map<?, ?> realmMap) {
                Object roles = realmMap.get("roles");
                if (roles instanceof Collection<?> roleList) {
                    for (Object roleObj : roleList) {
                        String role = String.valueOf(roleObj);
                        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                        authorities.add(new SimpleGrantedAuthority(authority));
                    }
                }
            }

            return new JwtAuthenticationToken(jwt, authorities);
        }
    }
}
