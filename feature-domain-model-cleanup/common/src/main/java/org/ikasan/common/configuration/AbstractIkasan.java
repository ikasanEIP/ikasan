/*
 * $Id$
 * $URL$
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;

import org.apache.log4j.Logger;

/**
 * Java representation of the IkasanSecurity XML configuration file.
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractIkasan
{
    /** Logger */
    private static Logger logger = Logger.getLogger(AbstractIkasan.class);
    /** XML version and encoding */
    protected static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    /** URI for the XML schema instance */
    protected String schemaInstanceNSURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
    /** The schema location for the no namespace case */
    protected String noNamespaceSchemaLocation;
    /** The schema version */
    protected String version;
    /** Ikasan configuration entries */
    protected List<Entry> entries = new ArrayList<Entry>();

    /**
     * Default constructor
     */
    public AbstractIkasan()
    {
        // Do Nothing
    }

    /**
     * Constructor: creates new <code>AbstractIkasan</code> instance
     * 
     * @param noNamespaceSchemaLocation
     */
    public AbstractIkasan(String noNamespaceSchemaLocation)
    {
        this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
    }

    /**
     * @param schemaInstanceNSURI
     */
    public void setSchemaInstanceNSURI(String schemaInstanceNSURI)
    {
        this.schemaInstanceNSURI = schemaInstanceNSURI;
    }

    /**
     * @return schemaInstanceNSURI
     */
    public String getSchemaInstanceNSURI()
    {
        return this.schemaInstanceNSURI;
    }

    /**
     * @param noNamespaceSchemaLocation
     */
    public void setNoNamespaceSchemaLocation(String noNamespaceSchemaLocation)
    {
        this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
    }

    /**
     * @return noNamespaceSchemaLocation
     */
    public String getNoNamespaceSchemaLocation()
    {
        return this.noNamespaceSchemaLocation;
    }

    /**
     * @param version
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * @return version
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * Sets the entries.
     * @param entries 
     */
    public void setEntries(final List<Entry> entries)
    {
        this.entries = entries;
        logger.debug("Setting entries [" + this.entries + "].");
    }

    /**
     * Gets the entries.
     * 
     * @return entries
     */
    public List<Entry> getEntries()
    {
        logger.debug("Getting entries [" + this.entries + "].");
        return this.entries;
    }

    /**
     * Adds a new entry to the current entries list
     * 
     * @param entry
     */
    public void addEntry(final Entry entry)
    {
        this.entries.add(entry);
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        // String values of each entry
        StringBuilder sbEntries = new StringBuilder();
        for(Entry entry : this.getEntries())
            sbEntries.append(entry.toString());

        sb.append(sbEntries);
        sb.append("].");
        return sb.toString();
    }

}
