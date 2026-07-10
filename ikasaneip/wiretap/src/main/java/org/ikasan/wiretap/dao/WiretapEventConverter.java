package org.ikasan.wiretap.dao;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * The purpose of this converter is to convert an implementation of a WiretapEvent to
 * one that is expected by the DAO layer (WiretapFlowEvent).
 */
public class WiretapEventConverter implements Converter<List<WiretapEvent>, List<WiretapEvent>>
{
    JsonMapper jsonMapper;

    /**
     * Constructor
     */
    public WiretapEventConverter()
    {
        jsonMapper = JsonMapper.builder().build();
    }

    @Override
    public List<WiretapEvent> convert(List<WiretapEvent> payload) throws TransformationException
    {
        List<WiretapEvent> results;

        try
        {
            String json = jsonMapper.writeValueAsString(payload);
            results = jsonMapper.readValue(json, jsonMapper.getTypeFactory()
                .constructCollectionType(List.class, WiretapFlowEvent.class));
        }
        catch (JacksonException e)
        {
            throw new TransformationException("Cannot transform a list of wiretap events to a list of hibernate wiretap events!", e);
        }

        return results;
    }
}
