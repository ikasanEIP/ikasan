package org.ikasan.component.validator.xml;

import java.net.URL;

public class ClasspathSchemaResolver
{
    private String schemaLocation;

    public ClasspathSchemaResolver(String schemaName)
    {
        try
        {
            URL schemaUrl = getClass().getClassLoader().getResource(schemaName);

            if(schemaName == null)
            {
                throw new RuntimeException("Could not locate schema file on the " +
                        "classpath: " + schemaName);
            }

            setSchemaLocation(schemaUrl.toString());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception occurred when trying to locate schema file on " +
                    "classpath: " + schemaName, e);
        }
    }

    public void setSchemaLocation(String schemaLocation)
    {
        this.schemaLocation = schemaLocation;
    }

    public String getSchemaLocation()
    {
        return this.schemaLocation;
    }
}