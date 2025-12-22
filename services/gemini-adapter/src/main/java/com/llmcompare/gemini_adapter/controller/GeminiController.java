package com.llmcompare.gemini_adapter.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class GeminiController {

    @PostMapping("/gemini")
    public Mono<String> callGemini(@RequestBody String prompt) {

        return  Mono.error(new RuntimeException("Error processing request: " ));
                //Mono.just("GEMINI response for prompt: " + prompt);
    }
}
