package org.ikasan.rest.client;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.ikasan.rest.client.dto.ChangeFlowStateDto;
import org.ikasan.rest.client.dto.FlowDto;
import org.ikasan.rest.client.dto.ModuleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModuleControlRestServiceImpl
{
    Logger logger = LoggerFactory.getLogger(ModuleControlRestServiceImpl.class);

    protected final static String CHANGE_FLOW_STATE_URL= "/rest/moduleControl";
    protected final static String FLOWS_STATUS_URL= "/rest/moduleControl/{moduleName}";
    protected final static String SINGLE_FLOW_STATUS_URL= "/rest/moduleControl/{moduleName}/{flowName}";

    private RestTemplate restTemplate;



    public ModuleControlRestServiceImpl()
    {
        restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

    }

    public Optional<ModuleDto> getFlowStates(String contextUrl, String moduleName)
    {

        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        String url = contextUrl+FLOWS_STATUS_URL;
        Map<String, String> parameters = new HashMap<String, String>(){{put("moduleName",moduleName);}};
        try
        {
            ResponseEntity<ModuleDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, ModuleDto.class,parameters);

            return Optional.of(response.getBody());
        }
        catch(RestClientException e){
            logger.warn("Issue getting flow status from module [" + url
                + "]  and param ["+parameters+"] with response [{"+e.getLocalizedMessage()+"}]");
            return Optional.empty();
        }
    }


    public Optional<FlowDto> getFlowState(String contextUrl, String moduleName, String flowName)
    {

        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        String url = contextUrl+SINGLE_FLOW_STATUS_URL;
        Map<String, String> parameters = new HashMap<String, String>(){{put("moduleName",moduleName);put("flowName",flowName);}};
        try
        {
            ResponseEntity<FlowDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, FlowDto.class,parameters);

            return Optional.of(response.getBody());
        }
        catch(RestClientException e){
            logger.warn("Issue getting flow status from module [" + url
                + "]  and param ["+parameters+"] with response [{"+e.getLocalizedMessage()+"}]");
            return Optional.empty();
        }
    }

    public boolean changeFlowState(String contextUrl, String moduleName, String flowName, String action)
    {
        ChangeFlowStateDto changeFlowStateDto = new ChangeFlowStateDto(moduleName,flowName,action);
        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(changeFlowStateDto,headers);
        String url = contextUrl+CHANGE_FLOW_STATE_URL;
        try
        {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            return true;
        }
        catch(RestClientException e){
            logger.warn("Issue updating flow state [" + url
                + "] with module ["+moduleName+"] "
                + "and flows ["+flowName+"]"
                + "and action ["+action+"]"
                + " with response [{"+e.getLocalizedMessage()+"}]");
            return false;
        }
    }



    private HttpHeaders createHttpHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        return headers;
    }
}
