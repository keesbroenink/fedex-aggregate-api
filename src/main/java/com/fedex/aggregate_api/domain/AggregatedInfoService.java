package com.fedex.aggregate_api.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

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

        Mono<List<PricingInfo>> pricing = fedexApi.getPricing(pricingIso2CountryCodes);
        Mono<List<TrackingInfo>> trackStatus = fedexApi.getTrackStatus(trackOrderNumbers);
        Mono<List<ShipmentInfo>> shipments = fedexApi.getShipments(shipmentsOrderNumbers);

        // call in parallel
        Mono<AggregatedInfo> answer = Mono.zip( pricing, trackStatus, shipments).map( data -> {
            AggregatedInfo result = new AggregatedInfo();
            result.addPricing(data.getT1());
            result.addTracking(data.getT2());
            result.addShipments(data.getT3());
            return result;
        });
        return answer;
    }


}
