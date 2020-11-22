package org.ikasan.business.stream.metadata.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.spec.metadata.BusinessStreamMetaData;

public class BusinessStreamMetaDataImpl implements BusinessStreamMetaData<BusinessStream>
{
    private String id;
    private String name;
    private String description;
    private String json;
    private BusinessStream businessStream;

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getJson()
    {
        return this.json;
    }

    @Override
    public void setJson(String json)
    {
        this.json = json;
    }

    @Override
    public BusinessStream getBusinessStream() {
        if(this.businessStream == null) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                this.businessStream = mapper.readValue(this.json, BusinessStream.class);
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException("Could not map business stream from JSON", e);
            }
        }

        return this.businessStream;
    }
}
