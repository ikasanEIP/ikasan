package org.ikasan.rest.client;

import org.ikasan.rest.client.dto.ResubmissionRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;

public class ResubmissionRestServiceImpl extends ModuleRestService
{
    Logger logger = LoggerFactory.getLogger(ResubmissionRestServiceImpl.class);

    protected final static String RESUBMSSION_URL = "/rest/resubmission";


    public ResubmissionRestServiceImpl(Environment environment)
    {
        super(environment);
    }

    public boolean resubmit(String contextUrl, String moduleName, String flowName, String action, String errorUri)
    {
        ResubmissionRequestDto dto = new ResubmissionRequestDto(moduleName, flowName, errorUri, action);
        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(dto, headers);
        String url = contextUrl + RESUBMSSION_URL;
        try
        {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return true;
        }
        catch (Exception e)
        {
            logger.warn("Issue resubmitting event [" + errorUri + "] [" + url + "] with module [" + moduleName + "] "
                            + "and flows [" + flowName + "]" + "and action [" + action + "]" + " with response [{" + e
                .getLocalizedMessage() + "}]");
            return false;
        }
    }

}
