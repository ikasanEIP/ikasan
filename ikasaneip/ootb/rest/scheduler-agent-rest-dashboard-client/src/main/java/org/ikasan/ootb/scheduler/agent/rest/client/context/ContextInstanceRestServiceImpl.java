package org.ikasan.ootb.scheduler.agent.rest.client.context;

import static org.ikasan.spec.dashboard.DashboardRestService.*;

import java.util.HashMap;
import java.util.Map;

import org.ikasan.dashboard.AbstractRestServiceImpl;
import org.ikasan.ootb.scheduler.agent.rest.converters.ObjectMapperFactory;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ContextInstanceRestServiceImpl extends AbstractRestServiceImpl implements ContextInstanceRestService<ContextInstance> {
    Logger logger = LoggerFactory.getLogger(ContextInstanceRestServiceImpl.class);

    private final String moduleName;

    private final ObjectMapper mapper;

    public ContextInstanceRestServiceImpl(Environment environment,
                                          HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory,
                                          String path) {
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
    public Map<String, ContextInstance> getAll() {
        if (this.token == null) {
            authenticate(moduleName);
        }
        HttpHeaders headers = super.createHttpHeaders(moduleName);
        HttpEntity entity = new HttpEntity(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url + "/getAll", HttpMethod.GET, entity, String.class);

            return this.mapper.readValue(response.getBody(), new TypeReference<>() {
            });
        } catch (RestClientException | JsonProcessingException e) {
            String message = "Issue getting all context instances for url [" + url + "]  with response [{" + e.getLocalizedMessage() + "}]";
            logger.error(message);
            throw new EndpointException(e);
        }
    }

    @Override
    public Map<String, ContextInstance> getByContextId(String correlationId) {
        if (this.token == null) {
            authenticate(moduleName);
        }
        HttpHeaders headers = super.createHttpHeaders(moduleName);
        HttpEntity entity = new HttpEntity(headers);
        try {
            String urlTemplate = UriComponentsBuilder.fromHttpUrl(url + "/getByContextName")
                .queryParam("contextName", "{contextName}")
                .encode()
                .toUriString();
            Map<String, String> parameters = new HashMap<>() {{
                put("contextName", correlationId);
            }};

            ResponseEntity<String> response = restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, String.class, parameters);

            return this.mapper.readValue(response.getBody(), new TypeReference<>() {
            });

        } catch (RestClientException | JsonProcessingException e) {
            String message = "Issue getting context instance for url [" + url + "]  with response [{" + e.getLocalizedMessage() + "}]";
            logger.error(message);
            throw new EndpointException(e);
        }
    }
}
