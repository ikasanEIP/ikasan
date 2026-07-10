package org.ikasan.replay.dao;

import org.ikasan.replay.model.ReplayEventImpl;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.replay.ReplayEvent;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * The purpose of this converter is to convert an implementation of a ReplayEvent to
 * one that is expected by the DAO layer (HibernateReplayEvent).
 */
public class ReplayEventConverter implements Converter<List<ReplayEvent>, List<ReplayEvent>>
{
    private final JsonMapper jsonMapper;

    /**
     * Constructor
     */
    public ReplayEventConverter()
    {
        jsonMapper = JsonMapper.builder().build();
    }

    @Override
    public List<ReplayEvent> convert(List<ReplayEvent> payload) throws TransformationException
    {
        List<ReplayEvent> results;

        try
        {
            String json = jsonMapper.writeValueAsString(payload);
            results = jsonMapper.readValue(json, jsonMapper.getTypeFactory()
                .constructCollectionType(List.class, ReplayEventImpl.class));
        }
        catch (JacksonException e)
        {
            throw new TransformationException("Cannot transform a list of replay events to a list of hibernate wiretap events!", e);
        }

        return results;
    }
}
