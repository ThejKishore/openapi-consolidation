package com.tk.learn.cloudgateway.dynamic;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.web.servlet.function.RouterFunctions.MATCHING_PATTERN_ATTRIBUTE;
import static org.springframework.web.servlet.function.RouterFunctions.REQUEST_ATTRIBUTE;

/**
 * Custom RouterFunctionMapping that allows dynamically refreshing the router function
 * built from DB routes and composing it with the default Spring Gateway MVC routing.
 */
@Slf4j
@Component
@Primary
public class CustomRouterFunctionMapping extends RouterFunctionMapping {

    private final AtomicReference<RouterFunction<?>> customRouterFunction = new AtomicReference<>();
    private final CustomRouterFunctionRetriever customRouterFunctionRetriever;
    private final List<HttpMessageConverter<?>> messageConverters;

    public CustomRouterFunctionMapping(CustomRouterFunctionRetriever retriever,
                                       List<HttpMessageConverter<?>> messageConverters) {
        super();
        this.customRouterFunctionRetriever = retriever;
        this.messageConverters = messageConverters;
    }

    public void refresh() {
        log.info("Refreshing the custom router function");
        RouterFunction<?> dbRouterFunction = customRouterFunctionRetriever.retrieve();
        RouterFunction<?> defaultSpringRoutingFunction = super.getRouterFunction();
        RouterFunction<?> finalRouterFunction = getFinalRouterFunction(defaultSpringRoutingFunction, dbRouterFunction);
        customRouterFunction.set(finalRouterFunction);
        log.info("Refreshing of the custom router function finished: {}", finalRouterFunction);
    }

    private RouterFunction<?> getFinalRouterFunction(@Nullable RouterFunction<?> defaultRouterFunction,
                                                     @Nullable RouterFunction<?> dbRouterFunction) {
        if (dbRouterFunction != null) {
            // Place DB routes before default (YAML) so DB can override
            return defaultRouterFunction != null ? dbRouterFunction.andOther(defaultRouterFunction) : dbRouterFunction;
        }
        return defaultRouterFunction;
    }

    @Override
    public RouterFunction<?> getRouterFunction() {
        return this.customRouterFunction.get();
    }

    @Override
    @Nullable
    protected Object getHandlerInternal(HttpServletRequest servletRequest) throws Exception {
        RouterFunction<?> currentRoutingFunction = this.customRouterFunction.get();
        if (currentRoutingFunction != null) {
            ServerRequest request = ServerRequest.create(servletRequest, this.messageConverters);
            HandlerFunction<?> handlerFunction = currentRoutingFunction.route(request).orElse(null);
            setAttributes(servletRequest, request, handlerFunction);
            return handlerFunction;
        } else {
            return null;
        }
    }

    private void setAttributes(HttpServletRequest servletRequest, ServerRequest request,
                               @Nullable HandlerFunction<?> handlerFunction) {

        PathPattern matchingPattern = (PathPattern) servletRequest.getAttribute(MATCHING_PATTERN_ATTRIBUTE);
        if (matchingPattern != null) {
            servletRequest.removeAttribute(MATCHING_PATTERN_ATTRIBUTE);
            servletRequest.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, matchingPattern.getPatternString());
        }
        servletRequest.setAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE, handlerFunction);
        servletRequest.setAttribute(REQUEST_ATTRIBUTE, request);
    }
}
