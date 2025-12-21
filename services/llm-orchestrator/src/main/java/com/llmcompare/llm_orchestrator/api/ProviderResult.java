package com.llmcompare.llm_orchestrator.api;

public record ProviderResult(
        String provider,
        String status,
        String response,
        long latencyMs
) {}
