package com.tk.learn.cloudgateway.dynamic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DbRouteModels {
    public static class DbRoute {
        public String id;
        public String uri;
        public Integer order;
        public boolean enabled;
        public Map<String, String> metadata = new LinkedHashMap<>();
        public List<PredicateRow> predicates = new ArrayList<>();
        public List<FilterRow> filters = new ArrayList<>();
    }

    public record PredicateRow(String name, String args) {}
    public record FilterRow(String name, String args) {}
}
