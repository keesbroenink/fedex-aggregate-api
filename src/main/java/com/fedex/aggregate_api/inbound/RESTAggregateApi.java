package com.fedex.aggregate_api.inbound;


import com.fedex.aggregate_api.domain.AggregatedInfo;
import com.fedex.aggregate_api.domain.AggregatedInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fedex/aggregate-api")
public class RESTAggregateApi {
    private final AggregatedInfoService service;
    public RESTAggregateApi( AggregatedInfoService aggregatedInfoService) {
        this.service = aggregatedInfoService;
    }
    @GetMapping("/")
    Mono<AggregatedInfo> getAggregatedInfo() {
        return service.getInfo();
    }
}
