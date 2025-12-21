package com.llmcompare.llm_orchestrator.api;

import java.util.List;

public record CompareResponse(
        String requestId,
        List<ProviderResult> results
) {}
