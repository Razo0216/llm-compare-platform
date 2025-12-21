package com.llmcompare.llm_orchestrator.controller;

import com.llmcompare.llm_orchestrator.api.CompareRequest;
import com.llmcompare.llm_orchestrator.api.CompareResponse;
import com.llmcompare.llm_orchestrator.service.CompareService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class CompareController {

    private final CompareService service;

    public CompareController(CompareService service) {
        this.service = service;
    }

    @PostMapping("/compare")
    public Mono<CompareResponse> compare(@RequestBody CompareRequest request) {

        var openAiCall = service.callProvider(
                "OPENAI",
                "http://localhost:9991/openai",   // stub
                request.prompt()
        );

        var geminiCall = service.callProvider(
                "GEMINI",
                "http://localhost:9992/gemini",   // stub
                request.prompt()
        );

        return Mono.zip(openAiCall, geminiCall)
                .map(tuple -> new CompareResponse(
                        UUID.randomUUID().toString(),
                        List.of(tuple.getT1(), tuple.getT2())
                ));
    }
}
