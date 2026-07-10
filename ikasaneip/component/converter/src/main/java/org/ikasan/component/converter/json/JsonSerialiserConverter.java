package org.ikasan.component.converter.json;

import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.serialiser.Converter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * POJO -> Json String converter implemented using Jackson.
 *
 * @param <T> - The type to be serialised.
 */
public class JsonSerialiserConverter<T> implements Converter<T, String>
{
    private final JsonMapper mapper;

    /**
     * Construct instance with using the provided Jackson ObjectMapper
     */
    public JsonSerialiserConverter(JsonMapper mapper)
    {
        this.mapper = mapper;
    }

    /**
     * Construct instance with a default Jackson ObjectMapper
     */
    public JsonSerialiserConverter()
    {
        mapper = JsonMapper.builder().build();
    }

    /**
     * Convert the input payload to a Json String
     *
     * @param payload - The object to be serialised
     * @return The Json serialised object as a String
     * @throws TransformationException if payload cannot be serialised
     */
    @Override public String convert(T payload)
    {
        try
        {
            return mapper.writeValueAsString(payload);
        }
        catch (JacksonException e)
        {
            throw new TransformationException(e);
        }
    }
}
