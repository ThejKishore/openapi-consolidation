
- [ ] [dynamic-cloud-gateway-mvc](https://github.com/spring-cloud/spring-cloud-gateway/issues/3179)


```java
public class CustomRouterFunctionMapping extends RouterFunctionMapping {
    private static final Logger log = LoggerFactory.getLogger(CustomRouterFunctionMapping.class);

    private final AtomicReference<RouterFunction<?>> customRouterFunction = new AtomicReference<>();
    private final CustomRouterFunctionRetriever customRouterFunctionRetriever;
    private final List<HttpMessageConverter<?>> messageConverters;

    public CustomRouterFunctionMapping(CustomRouterFunctionRetriever customRouterFunctionRetriever, List<HttpMessageConverter<?>> messageConverters) {
        this.customRouterFunctionRetriever = customRouterFunctionRetriever;
        this.messageConverters = messageConverters;
    }

    public void refresh() {
        log.info("Refreshing the custom router function");
        RouterFunction<?> controlPlaneRouterFunction = customRouterFunctionRetriever.retrieve();
        RouterFunction<?> defaultSpringRoutingFunction = super.getRouterFunction();
        RouterFunction<?> finalRouterFunction = getFinalRouterFunction(defaultSpringRoutingFunction, controlPlaneRouterFunction);
        customRouterFunction.set(finalRouterFunction);
        log.info("Refreshing of the custom router function finished: {}", finalRouterFunction);
    }

    private RouterFunction<?> getFinalRouterFunction(RouterFunction<?> defaultRouterFunction, RouterFunction<?> controlPlaneRouterFunction) {
        if (controlPlaneRouterFunction != null) {
            return defaultRouterFunction != null ? controlPlaneRouterFunction.andOther(defaultRouterFunction) : controlPlaneRouterFunction;
        }
        return defaultRouterFunction;
    }

    @Override
    public RouterFunction<?> getRouterFunction() {
        return this.customRouterFunction.get();
    }

    /**
     * Based on the {@link org.springframework.web.servlet.function.support.RouterFunctionMapping#getHandlerInternal(jakarta.servlet.http.HttpServletRequest)}
     */
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

    /**
     * Based on the {@link org.springframework.web.servlet.function.support.RouterFunctionMapping#setAttributes(HttpServletRequest, ServerRequest, HandlerFunction)}
     */
    private void setAttributes(HttpServletRequest servletRequest, ServerRequest request,
                               @Nullable HandlerFunction<?> handlerFunction) {

        PathPattern matchingPattern =
                (PathPattern) servletRequest.getAttribute(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE);
        if (matchingPattern != null) {
            servletRequest.removeAttribute(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE);
            servletRequest.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, matchingPattern.getPatternString());
            ServerHttpObservationFilter.findObservationContext(request.servletRequest())
                    .ifPresent(context -> context.setPathPattern(matchingPattern.getPatternString()));
        }
        servletRequest.setAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE, handlerFunction);
        servletRequest.setAttribute(RouterFunctions.REQUEST_ATTRIBUTE, request);
    }
}
```

```java
@Configuration
public class RoutingConfiguration extends WebMvcConfigurationSupport {

    /**
     * This configuration is based on the {@link WebMvcConfigurationSupport#routerFunctionMapping(FormattingConversionService, ResourceUrlProvider)}
     * It should be checked for the potential breaking changes with every Spring Boot upgrade.
     */
    @Bean
    @Primary
    public CustomRouterFunctionMapping customRouterFunctionMapping(
            @Qualifier("mvcConversionService") FormattingConversionService conversionService,
            @Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider,
            CustomRouterFunctionRetriever customRouterFunctionRetriever
    ) {
        CustomRouterFunctionMapping mapping = new CustomRouterFunctionMapping(customRouterFunctionRetriever, getMessageConverters());
        // We want the custom router function mapping to be the first in the chain.
        mapping.setOrder(HIGHEST_PRECEDENCE);
        mapping.setInterceptors(getInterceptors(conversionService, resourceUrlProvider));
        mapping.setCorsConfigurations(getCorsConfigurations());

        PathPatternParser patternParser = getPathMatchConfigurer().getPatternParser();
        if (patternParser != null) {
            mapping.setPatternParser(patternParser);
        }
        return mapping;
    }
}
```