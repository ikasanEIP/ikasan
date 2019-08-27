package org.ikasan.exclusion.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.exclusion.model.ExclusionEventImpl;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.exclusion.ExclusionEvent;

import java.io.IOException;
import java.util.List;

/**
 * The purpose of this converter is to convert an implementation of a ExclusionEvent to
 * one that is expected by the DAO layer (ExclusionEventImpl).
 */
public class ExclusionEventConverter implements Converter<List<ExclusionEvent>, List<ExclusionEvent>>
{
    private ObjectMapper objectMapper;

    /**
     * Constructor
     */
    public ExclusionEventConverter()
    {
        objectMapper = new ObjectMapper();
    }

    @Override
    public List<ExclusionEvent> convert(List<ExclusionEvent> payload) throws TransformationException
    {
        List<ExclusionEvent> results;

        try
        {
            String json = objectMapper.writeValueAsString(payload);
            results = objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, ExclusionEventImpl.class));
        }
        catch (IOException e)
        {
            throw new TransformationException("Cannot transform a list of exclusion events to a list of hibernate wiretap events!", e);
        }

        return results;
    }
}
