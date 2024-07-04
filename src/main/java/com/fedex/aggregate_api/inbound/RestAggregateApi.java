package com.fedex.aggregate_api.inbound;


import com.fedex.aggregate_api.domain.AggregatedInfo;
import com.fedex.aggregate_api.domain.AggregatedInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("aggregation")
public class RestAggregateApi {
    private final AggregatedInfoService service;
    public RestAggregateApi(AggregatedInfoService aggregatedInfoService) {
        this.service = aggregatedInfoService;
    }
    @GetMapping("/")
    Mono<AggregatedInfo> getAggregatedInfo(
            String pricing,
            String track,
            String shipments) {
        return service.getInfo( toList(pricing), toList(track), toList(shipments));
    }

    private List<String> toList(String element) {
        return element == null ? Collections.emptyList() : List.of(element);
    }
}
