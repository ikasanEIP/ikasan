package org.ikasan.error.reporting.dao;

import org.ikasan.error.reporting.model.ErrorOccurrenceImpl;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * The purpose of this converter is to convert an implementation of a ErrorOccurrence to
 * one that is expected by the DAO layer (ErrorOccurrenceImpl).
 */
public class ErrorOccurrenceConverter implements Converter<List<ErrorOccurrence>, List<ErrorOccurrence>>
{
    private final JsonMapper jsonMapper;

    /**
     * Constructor
     */
    public ErrorOccurrenceConverter()
    {
        jsonMapper = JsonMapper.builder().build();
    }

    @Override
    public List<ErrorOccurrence> convert(List<ErrorOccurrence> payload) throws TransformationException
    {
        List<ErrorOccurrence> results;

        try
        {
            String json = jsonMapper.writeValueAsString(payload);
            results = jsonMapper.readValue(json, jsonMapper.getTypeFactory()
                .constructCollectionType(List.class, ErrorOccurrenceImpl.class));
        }
        catch (JacksonException e)
        {
            throw new TransformationException("Cannot transform a list of error occurrences to a list of hibernate wiretap events!", e);
        }

        return results;
    }
}
