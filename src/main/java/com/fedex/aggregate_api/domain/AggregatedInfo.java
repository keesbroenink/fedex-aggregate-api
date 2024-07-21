package com.fedex.aggregate_api.domain;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public record AggregatedInfo(Map<String, Double> pricing, Map<String, String> track, Map<String, List<String>> shipments) {
    public AggregatedInfo(Map<String, Double> pricing, Map<String, String> track, Map<String, List<String>> shipments) {
        this.pricing = new TreeMap(); this.pricing.putAll(pricing); //treemap is not threadsafe
        this.track = new TreeMap(); this.track.putAll(track);
        this.shipments = new TreeMap(); this.shipments.putAll(shipments);
    }
    public AggregatedInfo() {
        this(new TreeMap<>(), new TreeMap<>(), new TreeMap<>());
    }
    public synchronized void addPricing(List<PricingInfo> pricingList) {
        pricingList.forEach(entry -> pricing.put(entry.isoCountryCode(), entry.price()));
    }

    public synchronized void addTracking(List<TrackingInfo> trackingList) {
        trackingList.forEach(entry -> track.put(entry.orderNumber(), entry.status()));
    }

    public synchronized void addShipments(List<ShipmentInfo> shippingList) {
        shippingList.forEach(entry -> shipments.put(entry.orderNumber(), entry.shipments()));
    }



}
