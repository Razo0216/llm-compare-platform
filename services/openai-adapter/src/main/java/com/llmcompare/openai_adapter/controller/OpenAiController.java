package com.llmcompare.openai_adapter.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class OpenAiController {

    @PostMapping("/openai")
    public Mono<String> callOpenAi(@RequestBody String prompt) {

        return Mono.just("OPENAI response for prompt: " + prompt);
    }
}
