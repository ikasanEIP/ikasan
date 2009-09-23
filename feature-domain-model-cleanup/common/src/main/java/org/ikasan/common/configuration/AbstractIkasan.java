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
