package org.ikasan.rest.client;

import org.ikasan.rest.client.dto.*;
import org.ikasan.spec.module.client.ModuleControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModuleControlRestServiceImpl extends ModuleRestService implements ModuleControlService<ModuleDto, FlowDto, FlowStartupTypeDto>
{
    Logger logger = LoggerFactory.getLogger(ModuleControlRestServiceImpl.class);

    protected final static String CHANGE_FLOW_STATE_URL= "/rest/moduleControl";
    protected final static String CHANGE_FLOW_STARTUP_MODE_URL= "/rest/moduleControl/startupMode";
    protected final static String GET_FLOW_STARTUP_MODE_URL= "/rest/moduleControl/startupMode/{moduleName}/{flowName}";
    protected final static String FLOWS_STATUS_URL= "/rest/moduleControl/{moduleName}";
    protected final static String SINGLE_FLOW_STATUS_URL= "/rest/moduleControl/{moduleName}/{flowName}";


    public ModuleControlRestServiceImpl(Environment environment,
                                        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory) {
        super(environment, httpComponentsClientHttpRequestFactory);
    }

    @Override
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

    @Override
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

    @Override
    public boolean changeFlowState(String contextUrl, String moduleName, String flowName, String action)
    {
        ChangeFlowStateDto dto = new ChangeFlowStateDto(moduleName,flowName,action);
        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(dto,headers);
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

    @Override
    public boolean changeFlowStartupType(String contextUrl, String moduleName, String flowName, String startupType,
                                         String comment)
    {
        ChangeFlowStartupModeDto dto = new ChangeFlowStartupModeDto(moduleName, flowName, startupType, comment);
        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(dto, headers);
        String url = contextUrl + CHANGE_FLOW_STARTUP_MODE_URL;
        try
        {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return true;
        }
        catch (RestClientException e)
        {
            logger.warn(
                "Issue updating flow startup type [" + url + "] with module [" + moduleName + "] " + "and flows ["
                    + flowName + "] and startup [" + startupType + "]" + " with response [{" + e.getLocalizedMessage()
                    + "}]");
            return false;
        }
    }

    @Override
    public Optional<FlowStartupTypeDto> getFlowStartupType(String contextUrl, String moduleName, String flowName) {
        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        String url = contextUrl + GET_FLOW_STARTUP_MODE_URL;
        Map<String, String> parameters = new HashMap<String, String>(){{put("moduleName",moduleName);put("flowName",flowName);}};
        try
        {
            ResponseEntity<FlowStartupTypeDto> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, FlowStartupTypeDto.class, parameters);

            return Optional.of(responseEntity.getBody());
        }
        catch (RestClientException e)
        {
            logger.warn(
                "Could not get flow startup type with module [" + moduleName + "] " + "and flow ["
                    + flowName + "] " + " with response [{" + e.getLocalizedMessage()
                    + "}]");

            return Optional.empty();
        }
    }
}
