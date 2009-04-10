/*
 * $Id: Ikasan.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/configuration/Ikasan.java $
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

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * Java representation of the Ikasan XML configuration file.
 * 
 * @author <a href="mailto:jeff.mitchell@ikasan.org">Jeff Mitchell</a>
 */
public class Ikasan
    extends AbstractIkasan
{

    /**
     * Default constructor
     */
    public Ikasan()
    {
        // Do Nothing
    }

    /**
     * Constructor: creates new <code>Ikasan</code> instance
     * 
     * @param noNamespaceSchemaLocation
     */
    public Ikasan(String noNamespaceSchemaLocation)
    {
        this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Ikasan [");

        // String values of each entry
        StringBuilder sbEntries = new StringBuilder();
        for(Entry entry : this.getEntries())
            sbEntries.append(entry.toString());

        sb.append(sbEntries);
        sb.append("].");
        return sb.toString();
    }

    /**
     * Equality test
     * 
     * @param ikasan
     * @return boolean
     */
    public boolean equals(final Ikasan ikasan)
    {
        if (this.getEntries() == null && ikasan.getEntries() == null)
        {
            // compare each entry with each entry in the incoming entries
            for(int x=0; x < ikasan.getEntries().size(); x++)
            {
                // return false if any do not match
                if( !(this.entries.get(x).equals(ikasan.getEntries().get(x))) )
                    return false;
            }

            // if we're here they must all match
            return true;
        }

        // something was null and therefore no match
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
        setXstreamProps(xstream);
        return xstream.toXML(this);
    }

    /**
     * Converts an incoming XML string to an object
     * 
     * @param xml XML string
     * @return the Ikasan instance
     */
    public static Ikasan fromXML(final String xml)
    {
        XStream xstream = new XStream(new DomDriver());
        setXstreamProps(xstream);
        return (Ikasan) xstream.fromXML(xml);
    }

    /**
     * Converts an incoming XML string to an object
     * 
     * @param xml XML input stream
     * @return the Ikasan instance
     */
    public static Ikasan fromXML(final InputStream xml)
    {
        XStream xstream = new XStream(new DomDriver());
        setXstreamProps(xstream);
        return (Ikasan) xstream.fromXML(xml);
    }

    /**
     * Sets the common properties for the toXML/fromXML XStream object
     * 
     * @param xstream
     */
    private static void setXstreamProps(XStream xstream)
    {
        xstream.registerConverter(new IkasanConverter());
        xstream.alias(Ikasan.class.getSimpleName(), Ikasan.class);
        xstream.registerConverter(new EntryConverter());
        xstream.alias(Entry.class.getSimpleName(), Entry.class);
    }

}
