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
package org.ikasan.common.security.policy;

// Imported Java classes
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.ikasan.common.CommonXMLParser;
import org.ikasan.common.CommonXMLTransformer;
import org.ikasan.common.ResourceLoader;
import org.ikasan.common.security.algo.AlgorithmConverter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * This class represents the '<code>Policy</code>'.
 * 
 * @author Ikasan Development Team
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
