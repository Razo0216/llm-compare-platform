package com.llmcompare.llm_orchestrator.openai;

import java.util.List;

public record OpenAiRequest(
        String model,
        List<Message> messages
) {
    public record Message(String role, String content) {}
}
