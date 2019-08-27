package org.ikasan.wiretap.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.WiretapFlowEvent;

import java.io.IOException;
import java.util.List;

/**
 * The purpose of this converter is to convert an implementation of a WiretapEvent to
 * one that is expected by the DAO layer (WiretapFlowEvent).
 */
public class WiretapEventConverter implements Converter<List<WiretapEvent>, List<WiretapEvent>>
{
    ObjectMapper objectMapper;

    /**
     * Constructor
     */
    public WiretapEventConverter()
    {
        objectMapper = new ObjectMapper();
    }

    @Override
    public List<WiretapEvent> convert(List<WiretapEvent> payload) throws TransformationException
    {
        List<WiretapEvent> results;

        try
        {
            String json = objectMapper.writeValueAsString(payload);
            results = objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, WiretapFlowEvent.class));
        }
        catch (IOException e)
        {
            throw new TransformationException("Cannot transform a list of wiretap events to a list of hibernate wiretap events!", e);
        }

        return results;
    }
}
