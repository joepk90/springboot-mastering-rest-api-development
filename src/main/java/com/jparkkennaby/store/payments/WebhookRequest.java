package com.jparkkennaby.store.payments;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WebhookRequest {
    private Map<String, String> headers;
    private String payload;
}
