package org.ikasan.component.converter.json;

import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.serialiser.Converter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * Json String -> POJO converter implemented using Jackson.
 *
 * @param <T> - The type to be deserialised.
 */
public class JsonDeserialiserConverter<T> implements Converter<String, T>
{
    private final Class<T> target;

    private final JsonMapper mapper;

    /**
     * Construct instance with using the provided Jackson ObjectMapper
     */
    public JsonDeserialiserConverter(Class<T> target, JsonMapper mapper)
    {
        this.target = target;
        this.mapper = mapper;
    }

    /**
     * Construct instance with a default Jackson ObjectMapper
     */
    public JsonDeserialiserConverter(Class<T> target)
    {
        this.target = target;
        this.mapper = JsonMapper.builder().build();
    }

    /**
     * Convert the Json String input payload to a POJO of type @target
     *
     * @param payload - The String to be deserialised
     * @return The deserialised POJO
     * @throws TransformationException if payload cannot be deserialised
     */
    @Override public T convert(String payload)
    {
        try
        {
            return mapper.readValue(payload, target);
        }
        catch (JacksonException e)
        {
            throw new TransformationException(e);
        }
    }
}
