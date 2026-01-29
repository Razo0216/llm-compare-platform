package com.llmcompare.llm_orchestrator.controller;

import com.llmcompare.llm_orchestrator.api.CompareRequest;
import com.llmcompare.llm_orchestrator.api.CompareResponse;
import com.llmcompare.llm_orchestrator.api.ProviderResult;
import com.llmcompare.llm_orchestrator.service.CompareService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/compare")
public class CompareController {

    private final CompareService service;

    public CompareController(CompareService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('SERVICE')")
    public Mono<CompareResponse> compare(@RequestBody CompareRequest request) {

        Mono<ProviderResult> openAiCall = service.callOpenAi(request.prompt());
        Mono<ProviderResult> geminiCall = service.callGemini(request.prompt());

        return Mono.zip(openAiCall, geminiCall)
                .map(tuple -> new CompareResponse(
                        UUID.randomUUID().toString(),
                        List.of(tuple.getT1(), tuple.getT2())
                ));
    }
}
