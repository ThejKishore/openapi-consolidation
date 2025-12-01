package com.tk.learn.cloudgateway.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.server.mvc.config.GatewayMvcProperties;
import org.springframework.cloud.gateway.server.mvc.config.RouteProperties;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthAggregatorControllerUnitTest {

    @Mock
    private GatewayMvcProperties gatewayMvcProperties;

    @Mock
    private ObjectProvider<DiscoveryClient> discoveryClientProvider;

    private MockWebServer server;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        when(discoveryClientProvider.getIfAvailable()).thenReturn(null);
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    private RouteProperties routeTo(String id, String baseUrl) {
        RouteProperties rp = new RouteProperties();
        rp.setId(id);
        rp.setUri(URI.create(baseUrl));
        return rp;
    }

    @Test
    @DisplayName("Positive: Aggregated UP when upstream returns UP")
    void aggregatedUpWhenUpstreamUp() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"UP\"}"));

        String baseUrl = "http://" + server.getHostName() + ":" + server.getPort();
        when(gatewayMvcProperties.getRoutes()).thenReturn(List.of(routeTo("emp", baseUrl)));

        HealthAggregatorController controller = new HealthAggregatorController(gatewayMvcProperties, objectMapper, discoveryClientProvider);
        Map<String, Object> result = controller.getAggregatedHealth();

        Map<String, Object> emp = cast(result.get("emp"));
        assertThat(emp.get("aggregatedStatus")).isEqualTo("UP");
        List<?> instances = (List<?>) emp.get("instances");
        Map<String, Object> inst = cast(instances.get(0));
        assertThat(inst.get("status")).isEqualTo("UP");
        assertThat((String) inst.get("url")).contains("/actuator/health");
    }

    @Test
    @DisplayName("Negative: Aggregated DOWN when upstream returns DOWN")
    void aggregatedDownWhenUpstreamDown() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"DOWN\"}"));

        String baseUrl = "http://" + server.getHostName() + ":" + server.getPort();
        when(gatewayMvcProperties.getRoutes()).thenReturn(List.of(routeTo("person", baseUrl)));

        HealthAggregatorController controller = new HealthAggregatorController(gatewayMvcProperties, objectMapper, discoveryClientProvider);
        Map<String, Object> result = controller.getAggregatedHealth();

        Map<String, Object> person = cast(result.get("person"));
        assertThat(person.get("aggregatedStatus")).isEqualTo("DOWN");
        List<?> instances = (List<?>) person.get("instances");
        Map<String, Object> inst = cast(instances.get(0));
        assertThat(inst.get("status")).isEqualTo("DOWN");
    }

    @Test
    @DisplayName("Negative: Upstream not reachable -> UNKNOWN and no exception")
    void upstreamNotReachableUnknown() {
        // point to a non-listening port
        String baseUrl = "http://localhost:" + 65530;
        when(gatewayMvcProperties.getRoutes()).thenReturn(List.of(routeTo("ghost", baseUrl)));

        HealthAggregatorController controller = new HealthAggregatorController(gatewayMvcProperties, objectMapper, discoveryClientProvider);
        Map<String, Object> result = controller.getAggregatedHealth();

        Map<String, Object> ghost = cast(result.get("ghost"));
        assertThat(ghost.get("aggregatedStatus")).isEqualTo("UNKNOWN");
        List<?> instances = (List<?>) ghost.get("instances");
        Map<String, Object> inst = cast(instances.get(0));
        assertThat(inst.get("status")).isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("PI: No services/routes -> empty response and no crash")
    void noRoutesEmptyResponse() {
        when(gatewayMvcProperties.getRoutes()).thenReturn(Collections.emptyList());

        HealthAggregatorController controller = new HealthAggregatorController(gatewayMvcProperties, objectMapper, discoveryClientProvider);
        Map<String, Object> result = controller.getAggregatedHealth();

        assertThat(result).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cast(Object o) {
        return (Map<String, Object>) o;
    }
}
