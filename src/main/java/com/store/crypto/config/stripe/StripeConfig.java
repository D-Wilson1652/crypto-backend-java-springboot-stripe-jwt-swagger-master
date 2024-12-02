package com.store.crypto.config.stripe;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String secretKey;

    @Value("${stripe.webhook.secret.key}")
    private String webhookSecretKey;

    @Value("${client.base.url}")
    private String clientBaseUrl;

    @Value("${client.base.frontend.url}")
    private String clientBaseFrontendUrl;

    @PostConstruct
    public void initSecretKey() {
        Stripe.apiKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getClientBaseUrl() {
        return clientBaseUrl;
    }

    public String getWebhookSecretKey() {
        return webhookSecretKey;
    }

    public String getClientBaseFrontendUrl() {
        return clientBaseFrontendUrl;
    }
}
