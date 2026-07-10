package org.ikasan.ootb.scheduler.agent.rest.client.context;

import org.ikasan.dashboard.AbstractRestServiceImpl;
import org.ikasan.job.orchestration.model.job.FileEventDrivenJobImpl;
import org.ikasan.ootb.scheduler.agent.rest.converters.ObjectMapperFactory;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.ikasan.spec.dashboard.DashboardRestService.*;

public class ContextInstanceRestServiceImpl extends AbstractRestServiceImpl implements ContextInstanceRestService<ContextInstance> {
    Logger logger = LoggerFactory.getLogger(ContextInstanceRestServiceImpl.class);

    private final String moduleName;

    private final JsonMapper mapper;

    public ContextInstanceRestServiceImpl(Environment environment,
                                          HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory,
                                          String path) {
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        JsonMapper mapper = JsonMapper.builder()
            .configure(tools.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .build();

        JacksonJsonHttpMessageConverter jsonHttpMessageConverter = new JacksonJsonHttpMessageConverter(mapper);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

        super.url = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + path;
        super.authenticateUrl = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
        super.username = environment.getProperty(DASHBOARD_USERNAME_PROPERTY);
        super.password = environment.getProperty(DASHBOARD_PASSWORD_PROPERTY);

        // TODO sort our user agent
        this.moduleName = environment.getProperty(MODULE_NAME_PROPERTY);

        this.mapper = ObjectMapperFactory.newInstance();
    }

    @Override
    public Map<String, ContextInstance> getAllInstancesDashboardThinksAgentShouldHandle(String agentName) {
        if (this.token == null) {
            authenticate(moduleName);
        }
        HttpHeaders headers = super.createHttpHeaders(moduleName);
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        try {
            String urlTemplate = UriComponentsBuilder.fromUriString(url + "/jobContext/getByAgentName")
                .queryParam("agentName", "{agentName}")
                .encode()
                .toUriString();
            Map<String, String> parameters = Collections.unmodifiableMap(new HashMap<>() {{
                put("agentName", agentName);
            }});

            ResponseEntity<String> response = restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, String.class, parameters);

            return this.mapper.readValue(response.getBody(), new TypeReference<>() {
            });

        } catch (RestClientException | JacksonException e) {
            String message = "Issue getting context instance for url [" + url + "]  with response [{" + e.getLocalizedMessage() + "}]";
            logger.error(message);
            throw new EndpointException(e);
        }
    }

    @Override
    public FileEventDrivenJob getFileEventJob(String jobName, String contextName) {
        if (this.token == null) {
            authenticate(moduleName);
        }
        HttpHeaders headers = super.createHttpHeaders(moduleName);
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        try {
            String urlTemplate = UriComponentsBuilder.fromUriString(url + "/job")
                .path("/")
                .path(contextName)
                .path("/")
                .path(jobName)
                .encode()
                .toUriString();

            ResponseEntity<String> response = restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, String.class);

            return this.mapper.readValue(response.getBody(), FileEventDrivenJobImpl.class);

        } catch (RestClientException | JacksonException e) {
            String message = "Issue getting context instance for url [" + url + "]  with response [{" + e.getLocalizedMessage() + "}]";
            logger.error(message);
            throw new EndpointException(e);
        }
    }
}
