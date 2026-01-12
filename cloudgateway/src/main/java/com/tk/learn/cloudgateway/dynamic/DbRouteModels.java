package com.tk.learn.cloudgateway.dynamic;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DbRouteModels {
    @Entity
    @Table(name = "gw_routes")
    public static class DbRoute {
        @Id
        public String id;

        @Column(name = "uri")
        public String uri;

        @Column(name = "order_no")
        public Integer order;

        @Column(name = "enabled")
        public boolean enabled;

        @Transient
        public Map<String, String> metadata = new LinkedHashMap<>();

        @Transient
        public List<PredicateRow> predicates = new ArrayList<>();

        @Transient
        public List<FilterRow> filters = new ArrayList<>();
    }

    public record PredicateRow(String name, String args) {}
    public record FilterRow(String name, String args) {}
}
