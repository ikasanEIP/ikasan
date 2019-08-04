package org.ikasan.rest.dashboard;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class AbstractRestMvcTest
{
    protected String mapToJson(Object obj) throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
        throws JsonParseException, JsonMappingException, IOException
    {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    protected String loadDataFile(String fileName) throws IOException
    {
        String contentToSend = IOUtils.toString(loadDataFileStream(fileName), "UTF-8");

        return contentToSend;
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return getClass().getResourceAsStream(fileName);
    }
}
