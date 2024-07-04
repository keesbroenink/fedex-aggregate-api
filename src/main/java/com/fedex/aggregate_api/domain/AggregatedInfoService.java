package com.fedex.aggregate_api.domain;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AggregatedInfoService {
    public Mono<AggregatedInfo> getInfo(List<String> pricing,
                                        List<String> track,
                                        List<String> shipments) {
        return null;
    }
}
