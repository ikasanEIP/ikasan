package org.ikasan.exclusion.dao;

import org.ikasan.exclusion.model.ExclusionEventImpl;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.exclusion.ExclusionEvent;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * The purpose of this converter is to convert an implementation of a ExclusionEvent to
 * one that is expected by the DAO layer (ExclusionEventImpl).
 */
public class ExclusionEventConverter implements Converter<List<ExclusionEvent>, List<ExclusionEvent>>
{
    private final JsonMapper jsonMapper;

    /**
     * Constructor
     */
    public ExclusionEventConverter()
    {
        jsonMapper = JsonMapper.builder().build();
    }

    @Override
    public List<ExclusionEvent> convert(List<ExclusionEvent> payload) throws TransformationException
    {
        List<ExclusionEvent> results;

        try
        {
            String json = jsonMapper.writeValueAsString(payload);
            results = jsonMapper.readValue(json, jsonMapper.getTypeFactory()
                .constructCollectionType(List.class, ExclusionEventImpl.class));
        }
        catch (JacksonException e)
        {
            throw new TransformationException("Cannot transform a list of exclusion events to a list of hibernate wiretap events!", e);
        }

        return results;
    }
}
