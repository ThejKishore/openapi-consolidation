package com.tk.learn.cloudgateway.common.dynamic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DbRoute {
    public String id;

    public String uri;

    public Integer order;

    public boolean enabled;

    public Integer version;

    // JSON columns for predicates, filters, and metadata
    public String predicatesJson; // Raw JSON string from predicates column

    public String filtersJson; // Raw JSON string from filters column

    public String metadataJson; // Raw JSON string from metadata column

    // Transient fields for working with deserialized data
    public Map<String, String> metadata = new LinkedHashMap<>();

    public List<PredicateRow> predicates = new ArrayList<>();

    public List<FilterRow> filters = new ArrayList<>();
}