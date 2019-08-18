package org.ikasan.error.reporting.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.error.reporting.model.ErrorOccurrenceImpl;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;

import java.io.IOException;
import java.util.List;

/**
 * The purpose of this converter is to convert an implementation of a ErrorOccurrence to
 * one that is expected by the DAO layer (ErrorOccurrenceImpl).
 */
public class ErrorOccurrenceConverter implements Converter<List<ErrorOccurrence>, List<ErrorOccurrence>>
{
    private ObjectMapper objectMapper;

    /**
     * Constructor
     */
    public ErrorOccurrenceConverter()
    {
        objectMapper = new ObjectMapper();
    }

    @Override
    public List<ErrorOccurrence> convert(List<ErrorOccurrence> payload) throws TransformationException
    {
        List<ErrorOccurrence> results;

        try
        {
            String json = objectMapper.writeValueAsString(payload);
            results = objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, ErrorOccurrenceImpl.class));
        }
        catch (IOException e)
        {
            throw new TransformationException("Cannot transform a list of error occurrences to a list of hibernate wiretap events!", e);
        }

        return results;
    }
}
