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

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * Java representation of the Ikasan Security XML configuration file.
 * 
 * @author Ikasan Development Team
 */
public class IkasanSecurity
    extends AbstractIkasan
{

    /**
     * Default constructor
     */
    public IkasanSecurity()
    {
        // Do Nothing
    }

    /**
     * Constructor: creates new <code>IkasanSecurity</code> instance
     * 
     * @param noNamespaceSchemaLocation
     */
    public IkasanSecurity(String noNamespaceSchemaLocation)
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
        sb.append("Ikasan Security [");

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
     * @param ikasanSecurity 
     * @return boolean
     */
    public boolean equals(final IkasanSecurity ikasanSecurity)
    {
        if (this.getEntries() == null && ikasanSecurity.getEntries() == null)
        {
            // compare each entry with each entry in the incoming entries
            for(int x=0; x < ikasanSecurity.getEntries().size(); x++)
            {
                // return false if any do not match
                if( !(this.entries.get(x).equals(ikasanSecurity.getEntries().get(x))) )
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
     * @return the IkasanSecurity instance
     */
    public static IkasanSecurity fromXML(final String xml)
    {
        XStream xstream = new XStream(new DomDriver());
        setXstreamProps(xstream);
        return (IkasanSecurity) xstream.fromXML(xml);
    }

    /**
     * Converts an incoming XML string to an object
     * 
     * @param xml XML input stream
     * @return the IkasanSecurity instance
     */
    public static IkasanSecurity fromXML(final InputStream xml)
    {
        XStream xstream = new XStream(new DomDriver());
        setXstreamProps(xstream);
        return (IkasanSecurity) xstream.fromXML(xml);
    }

    /**
     * Sets the common properties for the toXML/fromXML XStream object
     * 
     * @param xstream
     */
    private static void setXstreamProps(XStream xstream)
    {
        xstream.registerConverter(new IkasanSecurityConverter());
        xstream.alias(IkasanSecurity.class.getSimpleName(), IkasanSecurity.class);
        xstream.registerConverter(new EntryConverter());
        xstream.alias(Entry.class.getSimpleName(), Entry.class);
    }

}
