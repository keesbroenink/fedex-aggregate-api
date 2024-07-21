package com.fedex.aggregate_api.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fedex.aggregate_api.domain.AggregatedInfo;
import com.fedex.aggregate_api.domain.AggregatedInfoService;
import com.fedex.aggregate_api.domain.TrackingInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.fedex.aggregate_api.util.StringUtil.listToCommaSeparated;
import static java.util.Collections.emptyList;
import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = RestAggregateApi.class)
public class TestRestAggregateApi {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    AggregatedInfoService infoService;

    final ObjectMapper mapper = new ObjectMapper();
    @Test
    void testGetAggregatedInfo() throws Exception {
        // client with five requested items will get a response without waiting
        List<String> orderNumbers = List.of("1","2","3","4","5");
        AggregatedInfo request = new AggregatedInfo();
        List<TrackingInfo> tracking = List.of(
                new TrackingInfo(orderNumbers.get(0),"WAITING"),
                new TrackingInfo(orderNumbers.get(1),"WAITING"),
                new TrackingInfo(orderNumbers.get(2),"WAITING"),
                new TrackingInfo(orderNumbers.get(3),"WAITING"),
                new TrackingInfo(orderNumbers.get(4),"WAITING"));
        request.addTracking(tracking);
        given( infoService.getInfo(emptyList(), orderNumbers, emptyList())).willReturn(Mono.just(request));

        webClient.get()
                .uri("/aggregation?track="+listToCommaSeparated(orderNumbers))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().json(mapper.writeValueAsString(request));
    }


}
