/*
 *
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Copyright (c) 2007-2014 Mizuho International plc
 * ====================================================================
 * /
 */

package org.ikasan.component.converter.xml;

import java.util.Map;

/**
 * Configuration for an XML to Object JAXB converter.
 */
public class XmlToObjectConverterConfiguration
{
    private Class<?>[] classesToBeBound;

    private String contextPath;

    private String[] contextPaths;

    private String schema;

    private Map<String, Object> unmarshallerProperties;

    private Map<String, Object> marshallerProperties;

    public String[] getContextPaths()
    {
        return contextPaths;
    }

    public void setContextPaths(String[] contextPaths)
    {
        this.contextPaths = contextPaths;
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }

    public Class<?>[] getClassesToBeBound()
    {
        return classesToBeBound;
    }

    public void setClassesToBeBound(Class<?>[] classesToBeBound)
    {
        this.classesToBeBound = classesToBeBound;
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public Map<String, Object> getUnmarshallerProperties()
    {
        return unmarshallerProperties;
    }

    public void setUnmarshallerProperties(Map<String, Object> unmarshallerProperties)
    {
        this.unmarshallerProperties = unmarshallerProperties;
    }

    public Map<String, Object> getMarshallerProperties()
    {
        return marshallerProperties;
    }

    public void setMarshallerProperties(Map<String, Object> marshallerProperties)
    {
        this.marshallerProperties = marshallerProperties;
    }
}
