package com.tk.learn.cloudgateway.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.server.mvc.config.GatewayMvcProperties;
import org.springframework.cloud.gateway.server.mvc.config.RouteProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class OpenApiAggregatorController {

    @Autowired
    private GatewayMvcProperties gatewayMvcProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping(value = "/open-api-docs" , produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getConsolidatedOpenApiDocs(){
        // Collect all OpenAPI JSONs from the microservices
        List<Map<String, Object>> openApiDocs = new ArrayList<>();
        List<RouteProperties> routeProperties = gatewayMvcProperties.getRoutes().stream().collect(Collectors.toUnmodifiableList());
        routeProperties.stream().forEach(route -> {
            String microserviceUrl = route.getUri().toString();
            String openApiUrl = microserviceUrl + "/api-docs"; // Assuming the OpenAPI docs are exposed here

            RestTemplate restTemplate = new RestTemplate();
            // Fetch OpenAPI JSON from each microservice
            try {
                ResponseEntity<String> openApiJson = restTemplate.getForEntity(openApiUrl, String.class);
                if (openApiJson.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> openApiDoc = parseOpenApiJson(openApiJson.getBody());
                    openApiDocs.add(openApiDoc);
                }
                // Convert the OpenAPI JSON to a Map
            } catch (RuntimeException e) {
                log.error("route is not accessible {}",route.getUri().toString(),e);
            }
        });


        // Merge OpenAPI documentation
        Map<String, Object> mergedOpenApiDoc = mergeOpenApiDocs(openApiDocs);

        // Return the merged OpenAPI documentation as a response
        return mergedOpenApiDoc;
    }

    private Map<String, Object> parseOpenApiJson(String openApiJson) {
        // Parse JSON string into a Map using Jackson
        try {
            return objectMapper.readValue(openApiJson, Map.class);
        } catch (Exception e) {
           log.error("exception while parsing the json",e);
           return Collections.emptyMap();
        }
    }

    private Map<String, Object> mergeOpenApiDocs(List<Map<String, Object>> openApiDocs) {
        // Merge the paths and components of OpenAPI JSON
        Map<String, Object> merged = new HashMap<>();

        // Merge paths
        Map<String, Object> allPaths = new HashMap<>();
        for (Map<String, Object> doc : openApiDocs) {
            Map<String, Object> paths = (Map<String, Object>) doc.get("paths");
            if (paths != null) {
                allPaths.putAll(paths);
            }
        }
        merged.put("paths", allPaths);

        // Merge components
        Map<String, Object> allComponents = new HashMap<>();
        for (Map<String, Object> doc : openApiDocs) {
            Map<String, Object> components = (Map<String, Object>) doc.get("components");
            if (components != null) {
                Map<String, Object> schemas = (Map<String, Object>)components.get("schemas");
                allComponents.putAll(schemas);
            }
        }
        merged.put("components", Collections.singletonMap("schemas", allComponents));
        // Optionally add other sections (info, servers, etc.) as needed.
        merged.put("info", Map.of("title", "OpenAPI definition" , "version", "v0"));
        merged.put("openapi", "3.0.0");
        merged.put("servers", List.of( Map.of("description", "Generated server url","url", "http://localhost:9000")));
        return merged;
    }


}