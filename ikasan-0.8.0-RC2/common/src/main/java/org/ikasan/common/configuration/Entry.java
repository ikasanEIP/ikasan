/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * =============================================================================
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
 * @author Ikasan Development Team
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
