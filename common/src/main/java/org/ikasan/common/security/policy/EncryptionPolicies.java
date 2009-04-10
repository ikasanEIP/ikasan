/*
 * $Id: EncryptionPolicies.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/security/policy/EncryptionPolicies.java $
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
package org.ikasan.common.security.policy;

// Imported Java classes
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// Imported commons classes
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.ikasan.common.CommonXMLParser;
import org.ikasan.common.CommonXMLTransformer;
import org.ikasan.common.ResourceLoader;
import org.ikasan.common.security.algo.AlgorithmConverter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

// Imported XStream classes
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

/**
 * This class represents the '<code>Policy</code>'.
 * 
 * @author <a href="mailto:jeff.mitchell@ikasan.org">Ikasan Development Team</a>
 */
public class EncryptionPolicies
{
    // Class members
    /** XML version and encoding */
    protected static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    /** URI for the XML schema instance */
    private String schemaInstanceNSURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;

    /** The schema location for the no namespace case */
    private String noNamespaceSchemaLocation;

    /** The schema version */
    private String version;

    /** policy group list */
    private List<EncryptionPolicy> encryptionPolicies = new ArrayList<EncryptionPolicy>();

    /** A string designating 2 tabs */
    private static final String TAB_TAB = "\t\t";

    /** Default constructor */
    public EncryptionPolicies()
    {
        // does nothing
    }

    /**
     * Constructor: creates new <code>EncryptionPolicies</code> instance
     * 
     * @param noNamespaceSchemaLocation
     */
    public EncryptionPolicies(String noNamespaceSchemaLocation)
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
     * @param encryptionPolicies 
     */
    public void setEncryptionPolicies(List<EncryptionPolicy> encryptionPolicies)
    {
        this.encryptionPolicies = encryptionPolicies;
    }

    /**
     * @return policies
     */
    public List<EncryptionPolicy> getEncryptionPolicies()
    {
        return this.encryptionPolicies;
    }

    /**
     * Adds a new policy to the current policies list
     * 
     * @param encryptionPolicy
     */
    public void addEncryptionPolicy(final EncryptionPolicy encryptionPolicy)
    {
        this.encryptionPolicies.add(encryptionPolicy);
    }

    /** Returns a string representation of an ExceptionResolver object */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(XML_DECLARATION);
        sb.append('\n');
        sb.append(TAB_TAB);
        sb.append(this.getSchemaInstanceNSURI());
        sb.append('\n');
        sb.append(TAB_TAB);
        sb.append(this.getNoNamespaceSchemaLocation());
        sb.append('\n');
        sb.append(TAB_TAB);
        sb.append(this.getVersion());
        sb.append('\n');
        sb.append(TAB_TAB);
        sb.append(this.getEncryptionPolicies());
        sb.append('\n');
        return sb.toString();
    }

    /**
     * Converts the ExceptionResolver object to an XML string
     * 
     * @param validate : validate the resulting XML string or not
     * @return resulting XML string
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public String toXML(Boolean validate) 
        throws ParserConfigurationException, SAXException, IOException
    {
        XStream xstream = new XStream(new XppDriver(new XmlFriendlyReplacer("$", "_")));
        setXstreamProps(xstream);
        String policiesXML = xstream.toXML(this);
        if (validate) this.validate(policiesXML);

        return policiesXML;
    }

    /**
     * Converts an incoming XML string to an object
     * 
     * @param policiesXML XML string
     * @param validate : validate the incoming XML string or not
     * @return Policy
     */
    public static EncryptionPolicies fromXML(String policiesXML, Boolean validate) 
    {
        XStream xstream = new XStream(new DomDriver());
        setXstreamProps(xstream);
        return (EncryptionPolicies) xstream.fromXML(policiesXML);
    }

    /**
     * Converts an incoming XML string to an object
     * 
     * @param policiesXML XML string
     * 
     * @return Policy
     */
    public static EncryptionPolicies fromXML(InputStream policiesXML)
    {
        XStream xstream = new XStream(new DomDriver());
        setXstreamProps(xstream);
        return (EncryptionPolicies) xstream.fromXML(policiesXML);
    }

    /**
     * Converts an incoming XML Document to an object
     * 
     * @param doc
     * @param validate
     * @return Policy
     * 
     * @throws TransformerException
     * @throws IOException
     */
    public static EncryptionPolicies fromXML(final Document doc, final Boolean validate) 
        throws TransformerException, IOException

    {
        CommonXMLTransformer xmlTransformer = ResourceLoader.getInstance().newXMLTransformer();
        return fromXML(xmlTransformer.toString(doc), validate);
    }

    /**
     * Sets the common properties for the toXML/fromXML XStream object
     * 
     * @param xstream
     */
    protected static void setXstreamProps(XStream xstream)
    {
        xstream.registerConverter(new EncryptionPoliciesConverter());
        xstream.registerConverter(new EncryptionPolicyConverter());
        xstream.registerConverter(new AlgorithmConverter());

        xstream.alias("EncryptionPolicies", EncryptionPolicies.class);
        xstream.alias("EncryptionPolicy", EncryptionPolicy.class);
    }

    /**
     * Policies equality test
     * 
     * @param encrpytionPolicies
     * @return true if the policies match
     */
    public boolean equals(final EncryptionPolicies encrpytionPolicies)
    {
        if ((this.getVersion() == null && encrpytionPolicies.getVersion() == null) || this.getVersion() != null
                && this.getVersion().equals(encrpytionPolicies.getVersion())
                && (this.getNoNamespaceSchemaLocation() == null && encrpytionPolicies.getNoNamespaceSchemaLocation() == null)
                || this.getNoNamespaceSchemaLocation() != null
                && this.getNoNamespaceSchemaLocation().equals(encrpytionPolicies.getNoNamespaceSchemaLocation())
                && (this.getSchemaInstanceNSURI() == null && encrpytionPolicies.getSchemaInstanceNSURI() == null)
                || this.getSchemaInstanceNSURI() != null
                && this.getSchemaInstanceNSURI().equals(encrpytionPolicies.getSchemaInstanceNSURI()))
        {
            // check for null
            if (this.getEncryptionPolicies() == null && encrpytionPolicies.getEncryptionPolicies() == null) return true;

            // check size
            if (this.getEncryptionPolicies().size() != encrpytionPolicies.getEncryptionPolicies().size()) return false;

            // we need to check each policy
            List<EncryptionPolicy> thisPoliciesList = this.getEncryptionPolicies();
            List<EncryptionPolicy> policiesList = encrpytionPolicies.getEncryptionPolicies();
            for (int idx = 0; idx < thisPoliciesList.size(); idx++)
            {
                if (!(thisPoliciesList.get(idx).equals(policiesList.get(idx)))) return false;
            }

            // if we're here it must be equal
            return true;
        }

        return false;
    }

    /**
     * Validates XML
     * 
     * @param xml - incoming XML string representation of a Component object
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws IOException 
     */
    private void validate(String xml) throws ParserConfigurationException, SAXException, IOException
    {
        CommonXMLParser xmlParser = ResourceLoader.getInstance().newXMLParser();
        xmlParser.setNamespaceAware(true);
        xmlParser.setValidation(true, XMLConstants.W3C_XML_SCHEMA_NS_URI);
        xmlParser.parse(xml.getBytes());
    }
}
