package com.fedex.aggregate_api.domain;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AggregatedInfo {
    public Map<String, Double> pricing = new TreeMap();
    public Map<String, String> track = new TreeMap();
    public Map<String, List<String>> shipments = new TreeMap();
}
