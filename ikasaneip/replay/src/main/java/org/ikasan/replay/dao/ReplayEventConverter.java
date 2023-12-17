package org.ikasan.replay.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.replay.model.ReplayEventImpl;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.replay.ReplayEvent;

import java.io.IOException;
import java.util.List;

/**
 * The purpose of this converter is to convert an implementation of a ReplayEvent to
 * one that is expected by the DAO layer (HibernateReplayEvent).
 */
public class ReplayEventConverter implements Converter<List<ReplayEvent>, List<ReplayEvent>>
{
    private ObjectMapper objectMapper;

    /**
     * Constructor
     */
    public ReplayEventConverter()
    {
        objectMapper = new ObjectMapper();
    }

    @Override
    public List<ReplayEvent> convert(List<ReplayEvent> payload) throws TransformationException
    {
        List<ReplayEvent> results;

        try
        {
            String json = objectMapper.writeValueAsString(payload);
            results = objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, ReplayEventImpl.class));
        }
        catch (IOException e)
        {
            throw new TransformationException("Cannot transform a list of replay events to a list of hibernate wiretap events!", e);
        }

        return results;
    }
}
