package com.fedex.aggregate_api.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fedex.aggregate_api.domain.FedexApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static com.fedex.aggregate_api.util.StringUtil.listToCommaSeparated;


// we cannot use OpenFeign because it does not support non-blocking IO yet
@Component
public class FedexApiClient implements FedexApi {
    private static final int THROTTLING_THRESHOLD = 5;
    private static final Logger logger = LoggerFactory.getLogger(FedexApiClient.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;
    private final WebClient webClient;
    public FedexApiClient(WebClient.Builder webClientBuilder,
                          @Value("${fedexapi.client.baseurl}") String baseUrl) {
        this.baseUrl = baseUrl;
        webClient = webClientBuilder
                .baseUrl(baseUrl)
                .filter(logResponse())
                .build();
    }


    // this class is a singleton Spring Bean so we need to use a threadsafe queue implementation
    private final LinkedBlockingQueue<String> pricingQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> trackStatusQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> shipmentsQueue = new LinkedBlockingQueue<>();
    @Override
    public Mono<Map<String, Double>> getPricing(List<String> iso2CountryCodes) {
        pricingQueue.addAll( iso2CountryCodes);
        if (pricingQueue.size() < THROTTLING_THRESHOLD) {
            logger.info("number of country codes to low to call pricing api; waiting for next; queue size {}", pricingQueue.size());
            return Mono.never();
        } else {
            List<String> countryCodes = new ArrayList<>();
            pricingQueue.drainTo(countryCodes, THROTTLING_THRESHOLD); // never send more than THROTTLING_THRESHOLD
            return callApi("pricing", iso2CountryCodes)
                    .onErrorResume(FedexApiClientException.class, ex -> Mono.just(iso2CountryCodes))
                    .flatMap(keys -> createEmptyResult(iso2CountryCodes));
        }
    }
    @Override
    public Mono<Map<String, String>> getTrackStatus(List<String> orderNumbers) {
        trackStatusQueue.addAll( orderNumbers);
        if (trackStatusQueue.size() < THROTTLING_THRESHOLD) {
            logger.info("number of orders to low to call tracking api; waiting for next; queue size {}", trackStatusQueue.size());
            return Mono.never();
        } else {
            List<String> orders = new ArrayList<>();
            trackStatusQueue.drainTo(orders, THROTTLING_THRESHOLD); // never send more than THROTTLING_THRESHOLD
            return callApi("track", orders)
                    .onErrorResume(FedexApiClientException.class, ex -> Mono.just(orderNumbers))
                    .flatMap(keys -> createEmptyResult(orderNumbers));
        }
    }

    @Override
    public Mono<Map<String, List<String>>> getShipments(List<String> orderNumbers) {
        shipmentsQueue.addAll( orderNumbers);
        if (shipmentsQueue.size() < THROTTLING_THRESHOLD) {
            logger.info("number of orders to low to call shipping api; waiting for next; queue size {}", shipmentsQueue.size());
            return Mono.never();
        } else {
            List<String> orders = new ArrayList<>();
            shipmentsQueue.drainTo(orders, THROTTLING_THRESHOLD); // never send more than THROTTLING_THRESHOLD
            return callApi("shipments", orders)
                    .onErrorResume(FedexApiClientException.class, ex -> Mono.just(orderNumbers))
                    .flatMap(keys -> createEmptyResult(orderNumbers));
        }
    }

    private <T> Mono<Map<String, T>> createEmptyResult(List<String> keys) {
        Map<String, T> errorResult = new HashMap<>();
        keys.forEach( code -> errorResult.put(code, null));
        return Mono.just(errorResult);
    }

    private <T> Mono<T> callApi(String path, List<String> queryList) {
        String uri = UriComponentsBuilder.newInstance().path(path)
                .queryParam("q", listToCommaSeparated(queryList))
                .build().toUriString();
        logger.info("Calling {}/{}", baseUrl, uri);
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    logger.error("Failed to call {}/{} {}", baseUrl, uri, response.statusCode());
                    return Mono.error(new FedexApiClientException(response.statusCode()));
                })
                .bodyToMono(String.class)
                .map( jsonString -> {
                    try {
                        return mapper.readValue(jsonString, new TypeReference<T>() {});
                    } catch (JsonProcessingException e) {
                        throw new FedexApiClientException(e);
                    }
                });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.info("Response status: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> logger.info("{}={}", name, value)));
            return Mono.just(clientResponse);
        });
    }

}
