package com.llmcompare.llm_orchestrator.api;

import jakarta.validation.constraints.NotBlank;

public record CompareRequest(
        @NotBlank String prompt
) {}
