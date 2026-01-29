package com.llmcompare.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ServiceTokenRelayGatewayFilterFactory
        extends AbstractGatewayFilterFactory<ServiceTokenRelayGatewayFilterFactory.Config> {

    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    public ServiceTokenRelayGatewayFilterFactory(
            ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        super(Config.class);
        this.authorizedClientManager = authorizedClientManager;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) ->
                authorizedClientManager
                        .authorize(OAuth2AuthorizeRequest
                                .withClientRegistrationId("orchestrator-s2s")
                                .principal("system")
                                .build())
                        .flatMap(client -> {
                            String token = client.getAccessToken().getTokenValue();
                            ServerWebExchange mutatedExchange =
                                    exchange.mutate()
                                            .request(r -> r.headers(h -> {
                                                h.remove("Authorization");
                                                h.add("Authorization", "Bearer " + token);
                                            }))
                                            .build();
                            return chain.filter(mutatedExchange);
                        });
    }

    public static class Config {
        // no config needed (enterprise default)
    }
}
