package org.ikasan.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.ikasan.dashboard.dto.FlowInvocationMetricImpl;
import org.ikasan.spec.history.FlowInvocationMetric;
import org.ikasan.spec.metrics.MetricsService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
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

    private String userAgent;

    private ObjectMapper mapper;

    public MetricsRestServiceImpl(Environment environment, HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory)
    {
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
        super.url = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY);
        super.authenticateUrl = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
        super.username = environment.getProperty(DASHBOARD_USERNAME_PROPERTY);
        super.password = environment.getProperty(DASHBOARD_PASSWORD_PROPERTY);
        this.userAgent = environment.getProperty(DASHBOARD_REST_USERAGENT);

        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public List<FlowInvocationMetric> getMetrics(long startTime, long endTime) {
        return this.getMetricsBase(new HashMap()
            {{
                put("startTime", String.valueOf(startTime));
                put("endTime", String.valueOf(endTime));
            }}
            , METRICS_BY_TIME
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
        );
    }

    private List<FlowInvocationMetric> getMetricsBase(Map<String, String> parameters, String path){
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);
        try
        {
            ResponseEntity<String> response;
            response = restTemplate.exchange(url+path, HttpMethod.GET, entity, String.class,parameters);

            return this.mapper.readValue(response.getBody()
                , mapper.getTypeFactory().constructCollectionType(List.class, FlowInvocationMetricImpl.class));
        }
        catch (RestClientException | JsonProcessingException e)
        {
            logger.warn("Issue getting metrics for url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue getting metrics for url [" + url+path + "]  with response [{" + e
            .getLocalizedMessage() + "}]", e);
        }
    }

    private long getCountBase(Map<String, String> parameters, String path) {
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);
        try
        {
            ResponseEntity<String> response;
            response = restTemplate.exchange(url+path, HttpMethod.GET, entity, String.class,parameters);

            return Long.parseLong(response.getBody());
        }
        catch (Exception e)
        {
            logger.warn("Issue getting metrics for url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue getting count for metrics with url [" + url+path + "]  with response [{" + e
                .getLocalizedMessage() + "}]", e);
        }
    }
}
