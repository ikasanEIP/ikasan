package org.ikasan.ootb.scheduler.agent.rest.client.context;

import static org.ikasan.spec.dashboard.DashboardRestService.*;

import java.util.HashMap;
import java.util.Map;

import org.ikasan.dashboard.AbstractRestServiceImpl;
import org.ikasan.dashboard.LoadBalancedDashboardRestServiceImpl;
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

    private String moduleName;

    private final ObjectMapper mapper;

    public ContextInstanceRestServiceImpl(RestTemplate restTemplate, Environment environment,
                                          String path) {
        super(restTemplate,environment, path);
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
        this.mapper = ObjectMapperFactory.newInstance();
    }

    @Override
    protected void initialise(Environment environment, String path) {
        this.isEnabled = Boolean.valueOf(environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false"));
        if (this.isEnabled)
        {
            if(path != null) {
                this.url = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_BASE_URL_PROPERTY) + path;
            }
            else {
                this.url = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_BASE_URL_PROPERTY);
            }
            this.authenticateUrl = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
            this.username = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_USERNAME_PROPERTY);
            this.password = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_PASSWORD_PROPERTY);
            this.moduleName = environment.getProperty(MODULE_NAME_PROPERTY);
        }
    }

    @Override
    public Map<String, ContextInstance> getAllInstancesDashboardThinksAgentShouldHandle(String agentName) {
        if (this.token == null) {
            authenticate(moduleName);
        }
        HttpHeaders headers = super.createHttpHeaders(moduleName);
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        try {
            String urlTemplate = UriComponentsBuilder.fromHttpUrl(url + "/getByAgentName")
                .queryParam("agentName", "{agentName}")
                .encode()
                .toUriString();
            Map<String, String> parameters = new HashMap<>() {{
                put("agentName", agentName);
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
