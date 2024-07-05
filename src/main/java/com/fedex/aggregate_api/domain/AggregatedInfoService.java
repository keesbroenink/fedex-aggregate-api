package com.fedex.aggregate_api.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AggregatedInfoService {
    private final Logger logger = LoggerFactory.getLogger(AggregatedInfoService.class);
    private final FedexApi fedexApi;

    public AggregatedInfoService(FedexApi fedexApi) {
        this.fedexApi = fedexApi;
    }
    public Mono<AggregatedInfo> getInfo(List<String> pricingIso2CountryCodes,
                                        List<String> trackOrderNumbers,
                                        List<String> shipmentsOrderNumbers) {
        logger.info("getInfo() pricingIso2CountryCodes {}, trackOrderNumbers {}, shipmentsOrderNumbers {}",
                pricingIso2CountryCodes, trackOrderNumbers, shipmentsOrderNumbers);

        Mono<Map<String, Double>> pricing = fedexApi.getPricing(pricingIso2CountryCodes);
        Mono<Map<String, String>> trackStatus = fedexApi.getTrackStatus(trackOrderNumbers);
        Mono<Map<String, List<String>>> shipments = fedexApi.getShipments(shipmentsOrderNumbers);
        // call in parallel
        Mono<AggregatedInfo> answer = Mono.zip( pricing, trackStatus, shipments).map( data -> {
            AggregatedInfo result = new AggregatedInfo();
            result.pricing = data.getT1();
            result.track = data.getT2();
            result.shipments = data.getT3();
            logger.info("getInfo() after collecting data {}", result);
            return result;
        });
        return answer;
    }
}
