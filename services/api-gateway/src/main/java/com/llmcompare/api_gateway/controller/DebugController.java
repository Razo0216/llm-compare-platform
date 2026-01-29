package com.llmcompare.api_gateway.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DebugController {

    @GetMapping("/debug/auth")
    public Mono<Object> auth(Authentication authentication) {
        return Mono.just(authentication);
    }
}
