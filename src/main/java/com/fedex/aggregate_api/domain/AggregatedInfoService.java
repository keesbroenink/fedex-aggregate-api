package com.fedex.aggregate_api.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AggregatedInfoService {
    Logger logger = LoggerFactory.getLogger(AggregatedInfoService.class);
    public Mono<AggregatedInfo> getInfo(List<String> pricing,
                                        List<String> track,
                                        List<String> shipments) {
        logger.info("getInfo() pricing {}, track {}, shipments {}",pricing,track,shipments);
        return null;
    }
}
