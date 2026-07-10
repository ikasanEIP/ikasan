package org.ikasan.dashboard;

import org.ikasan.dashboard.dto.FlowInvocationMetricImpl;
import org.ikasan.spec.history.FlowInvocationMetric;
import org.ikasan.spec.metrics.MetricsService;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsRestServiceImpl extends AbstractRestServiceImpl implements MetricsService<FlowInvocationMetric> {

    public static final String DASHBOARD_BASE_URL_PROPERTY="ikasan.dashboard.base.url";
    public static final String DASHBOARD_USERNAME_PROPERTY="ikasan.dashboard.rest.username";
    public static final String DASHBOARD_PASSWORD_PROPERTY="ikasan.dashboard.rest.password";
    public static final String DASHBOARD_REST_USERAGENT ="ikasan.dashboard.rest.useragent";
    public static final String METRICS_PATH = "/rest/metrics";
    public static final String PAGED_METRICS_PATH = "/rest/metrics/paged";
    public static final String COUNT_METRICS_PATH = "/rest/metrics/count";
    public static final String METRICS_BY_TIME = METRICS_PATH + "/{startTime}/{endTime}";
    public static final String METRICS_BY_MODULE_AND_TIME = METRICS_PATH + "/{moduleName}/{startTime}/{endTime}";
    public static final String METRICS_BY_MODULE_FLOW_AND_TIME = METRICS_PATH + "/{moduleName}/{flowName}/{startTime}/{endTime}";
    public static final String METRICS_BY_TIME_PAGED = PAGED_METRICS_PATH + "/{startTime}/{endTime}/{offset}/{limit}";
    public static final String METRICS_BY_MODULE_AND_TIME_PAGED = PAGED_METRICS_PATH + "/{moduleName}/{startTime}/{endTime}/{offset}/{limit}";
    public static final String METRICS_BY_MODULE_FLOW_AND_TIME_PAGED = PAGED_METRICS_PATH + "/{moduleName}/{flowName}/{startTime}/{endTime}/{offset}/{limit}";
    public static final String COUNT_METRICS_BY_TIME = COUNT_METRICS_PATH + "/{startTime}/{endTime}";
    public static final String COUNT_METRICS_BY_MODULE_AND_TIME = COUNT_METRICS_PATH + "/{moduleName}/{startTime}/{endTime}";
    public static final String COUNT_METRICS_BY_MODULE_FLOW_AND_TIME = COUNT_METRICS_PATH + "/{moduleName}/{flowName}/{startTime}/{endTime}";

    private final String userAgent;

    private final JsonMapper mapper;

    public MetricsRestServiceImpl(Environment environment, HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory)
    {
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);

        JsonMapper mapper = JsonMapper.builder()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .build();

        JacksonJsonHttpMessageConverter jsonHttpMessageConverter = new JacksonJsonHttpMessageConverter(mapper);
        restTemplate.getMessageConverters().addFirst(jsonHttpMessageConverter);

        super.url = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY);
        super.authenticateUrl = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
        super.username = environment.getProperty(DASHBOARD_USERNAME_PROPERTY);
        super.password = environment.getProperty(DASHBOARD_PASSWORD_PROPERTY);
        this.userAgent = environment.getProperty(DASHBOARD_REST_USERAGENT);

        this.mapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
    }

    @Override
    public List<FlowInvocationMetric> getMetrics(long startTime, long endTime) {
        return this.getMetricsBase(new HashMap()
            {{
                put("startTime", String.valueOf(startTime));
                put("endTime", String.valueOf(endTime));
            }}
            , METRICS_BY_TIME
            , true
        );
    }

    @Override
    public List<FlowInvocationMetric> getMetrics(String moduleName, long startTime, long endTime) {
        return this.getMetricsBase(new HashMap()
            {{
                put("moduleName", moduleName);
                put("startTime", String.valueOf(startTime));
                put("endTime", String.valueOf(endTime));
            }}
            , METRICS_BY_MODULE_AND_TIME
            , true
        );
    }

    @Override
    public List<FlowInvocationMetric> getMetrics(String moduleName, String flowName, long startTime, long endTime) {
        return this.getMetricsBase(new HashMap()
            {{
                put("moduleName", moduleName);
                put("flowName", flowName);
                put("startTime", String.valueOf(startTime));
                put("endTime", String.valueOf(endTime));
            }}
            , METRICS_BY_MODULE_FLOW_AND_TIME
            , true
        );
    }

    @Override
    public List<FlowInvocationMetric> getMetrics(long startTime, long endTime, int offset, int limit) {
        return this.getMetricsBase(new HashMap()
           {{
               put("startTime", String.valueOf(startTime));
               put("endTime", String.valueOf(endTime));
               put("offset", String.valueOf(offset));
               put("limit", String.valueOf(limit));
           }}
            , METRICS_BY_TIME_PAGED
            , true
        );
    }

    @Override
    public long count(long startTime, long endTime) {
        return this.getCountBase(new HashMap()
           {{
               put("startTime", String.valueOf(startTime));
               put("endTime", String.valueOf(endTime));
           }}
            , COUNT_METRICS_BY_TIME
            , true
        );
    }

    @Override
    public List<FlowInvocationMetric> getMetrics(String moduleName, long startTime, long endTime, int offset, int limit) {
        return this.getMetricsBase(new HashMap()
           {{
               put("moduleName", moduleName);
               put("startTime", String.valueOf(startTime));
               put("endTime", String.valueOf(endTime));
               put("offset", String.valueOf(offset));
               put("limit", String.valueOf(limit));
           }}
            , METRICS_BY_MODULE_AND_TIME_PAGED
            , true
        );
    }

    @Override
    public long count(String moduleName, long startTime, long endTime) {
        return this.getCountBase(new HashMap()
             {{
                 put("moduleName", moduleName);
                 put("startTime", String.valueOf(startTime));
                 put("endTime", String.valueOf(endTime));
             }}
            , COUNT_METRICS_BY_MODULE_AND_TIME
            , true
        );
    }

    @Override
    public List<FlowInvocationMetric> getMetrics(String moduleName, String flowName, long startTime, long endTime, int offset, int limit) {
        return this.getMetricsBase(new HashMap()
              {{
                  put("moduleName", moduleName);
                  put("flowName", flowName);
                  put("startTime", String.valueOf(startTime));
                  put("endTime", String.valueOf(endTime));
                  put("offset", String.valueOf(offset));
                  put("limit", String.valueOf(limit));
              }}
            , METRICS_BY_MODULE_FLOW_AND_TIME_PAGED
            , true
        );
    }

    @Override
    public long count(String moduleName, String flowName, long startTime, long endTime) {
        return this.getCountBase(new HashMap()
             {{
                 put("moduleName", moduleName);
                 put("flowName", flowName);
                 put("startTime", String.valueOf(startTime));
                 put("endTime", String.valueOf(endTime));
             }}
            , COUNT_METRICS_BY_MODULE_FLOW_AND_TIME
            , true
        );
    }

    /**
     * Retrieves a list of flow invocation metrics by executing an HTTP request to a specified path
     * with the provided parameters.
     *
     * @param parameters a map of query parameters to be included in the HTTP request
     * @param path the specific API endpoint path to fetch the metrics data
     * @param isFirst a boolean flag indicating if this is the first attempt to fetch metrics
     *                (used for re-authentication logic in case of an authentication failure)
     * @return a list of {@code FlowInvocationMetric} objects representing the metrics data retrieved
     *         from the specified endpoint
     * @throws RuntimeException if there are issues with the HTTP request, response parsing, or
     *                          authentication
     */
    private List<FlowInvocationMetric> getMetricsBase(Map<String, String> parameters, String path, boolean isFirst){
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);
        try
        {
            ResponseEntity<String> response;
            response = restTemplate.exchange(url+path, HttpMethod.GET, entity, String.class,parameters);

            return this.mapper.readValue(response.getBody()
                , mapper.getTypeFactory().constructCollectionType(List.class, FlowInvocationMetricImpl.class));
        }
        catch (HttpClientErrorException e)
        {
            if ( e.getStatusCode().equals(HttpStatusCode.valueOf(401)) && isFirst )
            {
                this.token = null;
                if ( authenticate(this.userAgent) )
                { return getMetricsBase(parameters, path, false); }
            }

            logger.warn("Issue getting metrics for url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue getting metrics for url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]", e);
        }
        catch (RestClientException | JacksonException e)
        {
            logger.warn("Issue getting metrics for url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue getting metrics for url [" + url+path + "]  with response [{" + e
            .getLocalizedMessage() + "}]", e);
        }
    }

    /**
     * Retrieves the count of metrics by executing an HTTP request to a specified path
     * with the provided parameters. Handles authentication retries if necessary.
     *
     * @param parameters a map of query parameters to be included in the HTTP request
     * @param path the specific API endpoint path to fetch the metrics count
     * @param isFirst a boolean flag indicating if this is the first attempt to fetch the metrics count
     *                (used for re-authentication logic in case of an authentication failure)
     * @return the count of metrics as a long value retrieved from the specified endpoint
     * @throws RuntimeException if there are issues with the HTTP request, response parsing, or
     *                          authentication
     */
    private long getCountBase(Map<String, String> parameters, String path, boolean isFirst) {
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);
        try
        {
            ResponseEntity<String> response;
            response = restTemplate.exchange(url+path, HttpMethod.GET, entity, String.class,parameters);

            return Long.parseLong(response.getBody());
        }
        catch (HttpClientErrorException e)
        {
            if ( e.getStatusCode().equals(HttpStatusCode.valueOf(401)) && isFirst )
            {
                this.token = null;
                if ( authenticate(this.userAgent) )
                { return getCountBase(parameters, path, false); }
            }

            logger.warn("Issue getting count for metrics for url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue getting count for metrics for url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]", e);
        }
        catch (Exception e)
        {
            logger.warn("Issue getting count for metrics for url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue getting count for metrics with url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]", e);
        }
    }
}
