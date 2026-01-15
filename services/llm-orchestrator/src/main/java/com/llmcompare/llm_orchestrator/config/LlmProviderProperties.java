package com.llmcompare.llm_orchestrator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "llm")
public class LlmProviderProperties {

    private Providers providers;

    public Providers getProviders() {
        return providers;
    }

    public void setProviders(Providers providers) {
        this.providers = providers;
    }

    // =====================================================
    // NESTED CLASSES
    // =====================================================

    public static class Providers {
        private Provider openai;
        private Provider gemini;

        public Provider getOpenai() {
            return openai;
        }

        public void setOpenai(Provider openai) {
            this.openai = openai;
        }

        public Provider getGemini() {
            return gemini;
        }

        public void setGemini(Provider gemini) {
            this.gemini = gemini;
        }
    }

    public static class Provider {

        private String baseUrl;
        private String model;
        private boolean enabled;
        private boolean mock;
        private long timeoutMs;
        private Api api;

        // ---------- getters / setters ----------

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isMock() {
            return mock;
        }

        public void setMock(boolean mock) {
            this.mock = mock;
        }

        public long getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(long timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public Api getApi() {
            return api;
        }

        public void setApi(Api api) {
            this.api = api;
        }
    }

    public static class Api {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
