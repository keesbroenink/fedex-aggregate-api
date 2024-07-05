package com.fedex.aggregate_api.domain;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface FedexApi {
    Mono<Map<String, Double>> getPricing(List<String> iso2CountryCodes);

    Mono<Map<String, String>> getTrackStatus(List<String> orderNumbers);

    Mono<Map<String, List<String>>> getShipments(List<String> orderNumbers);
}
