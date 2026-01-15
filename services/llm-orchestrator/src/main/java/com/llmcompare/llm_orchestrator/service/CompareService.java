package com.llmcompare.llm_orchestrator.service;

import com.llmcompare.llm_orchestrator.api.ProviderResult;
import com.llmcompare.llm_orchestrator.config.LlmProviderProperties;
import com.llmcompare.llm_orchestrator.gemini.GeminiRequest;
import com.llmcompare.llm_orchestrator.gemini.GeminiResponse;
import com.llmcompare.llm_orchestrator.openai.OpenAiRequest;
import com.llmcompare.llm_orchestrator.openai.OpenAiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
public class CompareService {

    private static final Logger log = LoggerFactory.getLogger(CompareService.class);

    private final WebClient webClient;
    private final LlmProviderProperties properties;

    public CompareService(WebClient webClient, LlmProviderProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    // =====================================================
    // PUBLIC ENTRY POINTS
    // =====================================================

    @CircuitBreaker(name = "openai", fallbackMethod = "openAiFallback")
    @Retry(name = "openai")
    public Mono<ProviderResult> callOpenAi(String prompt) {
        return isOpenAiMock()
                ? callOpenAiMock(prompt)
                : callOpenAiReal(prompt);
    }

    @CircuitBreaker(name = "gemini", fallbackMethod = "geminiFallback")
    @Retry(name = "gemini")
    public Mono<ProviderResult> callGemini(String prompt) {
        return isGeminiMock()
                ? callGeminiMock(prompt)
                : callGeminiReal(prompt);
    }

    // =====================================================
    // OPENAI
    // =====================================================

    private Mono<ProviderResult> callOpenAiMock(String prompt) {
        return Mono.just(success(
                "OPENAI",
                "Mock OpenAI response for: " + prompt,
                System.currentTimeMillis()
        ));
    }

    private Mono<ProviderResult> callOpenAiReal(String prompt) {

        String apiKey = properties.getProviders().getOpenai().getApi().getKey();
        if (apiKey == null || apiKey.isBlank()) {
            return Mono.error(new IllegalStateException("OPENAI_API_KEY not set"));
        }

        long start = System.currentTimeMillis();

        OpenAiRequest request = new OpenAiRequest(
                properties.getProviders().getOpenai().getModel(),
                List.of(new OpenAiRequest.Message("user", prompt))
        );

        return webClient.post()
                .uri(properties.getProviders().getOpenai().getBaseUrl() + "/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .timeout(Duration.ofSeconds(60))
                .map(resp -> success(
                        "OPENAI",
                        resp.choices().get(0).message().content(),
                        start
                ));
    }

    private Mono<ProviderResult> openAiFallback(String prompt, Throwable ex) {
        log.warn("OpenAI fallback triggered", ex);
        return Mono.just(failure("OPENAI", ex));
    }

    // =====================================================
    // GEMINI
    // =====================================================

    private Mono<ProviderResult> callGeminiMock(String prompt) {
        return Mono.just(success(
                "GEMINI",
                "Mock Gemini response for: " + prompt,
                System.currentTimeMillis()
        ));
    }

    private Mono<ProviderResult> callGeminiReal(String prompt) {

        String apiKey = properties.getProviders().getGemini().getApi().getKey();
        if (apiKey == null || apiKey.isBlank()) {
            return Mono.error(new IllegalStateException("GEMINI_API_KEY not set"));
        }

        long start = System.currentTimeMillis();

        GeminiRequest request = new GeminiRequest(
                List.of(new GeminiRequest.Content(
                        List.of(new GeminiRequest.Part(prompt))
                ))
        );

        return webClient.post()
                .uri(properties.getProviders().getGemini().getBaseUrl()
                        + "/v1beta/models/"
                        + properties.getProviders().getGemini().getModel()
                        + ":generateContent")
                .header("x-goog-api-key", apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .timeout(Duration.ofSeconds(60))
                .map(resp -> {
                    if (resp.candidates() == null || resp.candidates().isEmpty()) {
                        throw new IllegalStateException("No Gemini candidates returned");
                    }
                    return success(
                            "GEMINI",
                            resp.candidates()
                                    .get(0)
                                    .content()
                                    .parts()
                                    .get(0)
                                    .text(),
                            start
                    );
                });
    }

    private Mono<ProviderResult> geminiFallback(String prompt, Throwable ex) {
        log.warn("Gemini fallback triggered", ex);
        return Mono.just(failure("GEMINI", ex));
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private ProviderResult success(String provider, String response, long start) {
        return new ProviderResult(
                provider,
                "OK",
                response,
                System.currentTimeMillis() - start
        );
    }

    private ProviderResult failure(String provider, Throwable ex) {
        return new ProviderResult(
                provider,
                "FAILED",
                ex.getMessage(),
                0
        );
    }

    // =====================================================
    // FEATURE FLAGS
    // =====================================================

    private boolean isOpenAiMock() {
        return properties.getProviders().getOpenai().isMock();
    }

    private boolean isGeminiMock() {
        return properties.getProviders().getGemini().isMock();
    }
}
