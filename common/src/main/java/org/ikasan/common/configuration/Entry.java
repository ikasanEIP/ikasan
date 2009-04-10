/*
 * $Id: Entry.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/configuration/Entry.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.configuration;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * Java representation of the Ikasan XML configuration individual entry.
 * 
 * @author <a href="mailto:jeff.mitchell@ikasan.org">Jeff Mitchell</a>
 */
public class Entry
{
    /** Logger */
    private static Logger logger = Logger.getLogger(Entry.class);
    /** key */
    private String key;
    /** value */
    private String value;

    /**
     * Default constructor
     */
    public Entry()
    {
        // Do Nothing
    }

    /**
     * Sets the entry key.
     * 
     * @param key
     */
    public void setKey(final String key)
    {
        this.key = key;
        logger.debug("Setting key [" + this.key + "].");
    }

    /**
     * Gets the key.
     * 
     * @return key
     */
    public String getKey()
    {
        logger.debug("Getting key [" + this.key + "].");
        return this.key;
    }

    /**
     * Sets the entry value.
     * 
     * @param value
     */
    public void setValue(final String value)
    {
        this.value = value;
        logger.debug("Setting value [" + this.value + "].");
    }

    /**
     * Gets the entry value.
     * 
     * @return value
     */
    public String getValue()
    {
        logger.debug("Getting value [" + this.value + "].");
        return this.value;
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Entry key [");
        sb.append(this.getKey());
        sb.append(']');
        sb.append(" value [");
        sb.append(this.getValue());
        sb.append("]; ");
        return sb.toString();
    }

    /**
     * Equality test
     * 
     * @param entry
     * @return boolean
     */
    public boolean equals(final Entry entry)
    {
        if ((this.getKey() == null && entry.getKey() == null)
                || this.getKey() != null
                && this.getKey().equals(entry.getKey())
                && (this.getValue() == null && entry.getValue() == null)
                || this.getValue() != null
                && this.getValue().equals(entry.getValue())) return true;
        return false;
    }

    /**
     * Converts the object to an XML string
     * 
     * @return resulting XML string
     */
    public String toXML()
    {
        XStream xstream = new XStream(new XppDriver(new XmlFriendlyReplacer(
            "$", "_")));
        this.setXstreamProps(xstream);
        return xstream.toXML(this);
    }

    /**
     * Converts an incoming XML string to an object
     * 
     * @param xml XML string
     * @return the Entry
     */
    public Entry fromXML(String xml)
    {
        XStream xstream = new XStream(new DomDriver());
        this.setXstreamProps(xstream);
        return (Entry) xstream.fromXML(xml);
    }

    /**
     * Sets the common properties for the toXML/fromXML XStream object
     * 
     * @param xstream
     */
    protected void setXstreamProps(XStream xstream)
    {
        xstream.registerConverter(new EntryConverter());
        xstream.alias(this.getClass().getSimpleName(), this.getClass());
    }

}
