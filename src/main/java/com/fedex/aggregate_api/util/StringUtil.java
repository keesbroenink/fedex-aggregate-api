package com.fedex.aggregate_api.util;

import java.util.*;
import java.util.stream.Collectors;

public class StringUtil {
    /**
     * When the input is null we return an empty list. Extra functionality: remove duplicates
     * @param element
     * @return
     */
    public static List<String> commaSeparatedtoList(String element) {
        return element == null ?
                Collections.emptyList() :
                Arrays.stream(element.split(",")).distinct().collect(Collectors.toList());
    }
}
