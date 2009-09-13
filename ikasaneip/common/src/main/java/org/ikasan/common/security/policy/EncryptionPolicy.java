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

import org.apache.log4j.Logger;
import org.ikasan.common.security.algo.Algorithm;
import org.ikasan.common.security.algo.AlgorithmConverter;

// Imported XStream classes
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

/**
 * This class represents the '<code>Policy</code>'.
 * 
 * @author Ikasan Development Team
 */
public class EncryptionPolicy
{
    /** Logger */
    private final static Logger logger = Logger
        .getLogger(EncryptionPolicy.class);
    /** policy name */
    private String name;
    /** algorithm */
    private Algorithm algorithm;
    
    /** Encryption Policy to String StringBuild (holder) */
    static transient StringBuilder appPolicyToString = null;

    /**
     * Sets the policy name.
     * 
     * @param name
     */
    public void setName(final String name)
    {
        this.name = name;
        logger.debug("Setting name [" + this.name + "].");
    }

    /**
     * Gets the policy algorithm.
     * 
     * @return algorithm
     */
    public Algorithm getAlgorithm()
    {
        logger.debug("Getting algorithm [" + this.algorithm + "].");
        return this.algorithm;
    }

    /**
     * Sets the policy algorithm.
     * 
     * @param algorithm
     */
    public void setAlgorithm(final Algorithm algorithm)
    {
        this.algorithm = algorithm;
        logger.debug("Setting algorithm [" + this.algorithm + "].");
    }

    /**
     * Gets the policy name.
     * 
     * @return name
     */
    public String getName()
    {
        logger.debug("Getting name [" + this.name + "].");
        return this.name;
    }

    /** Returns a string representation of the Policy object */
    @Override
    public String toString()
    {
        appPolicyToString = new StringBuilder();
        appPolicyToString.append("AEncryptionPolicy name [");
        appPolicyToString.append(this.getName());
        appPolicyToString.append("] ");
        appPolicyToString.append("algorithm [");
        appPolicyToString.append(this.getAlgorithm().getClass().getSimpleName());
        appPolicyToString.append("]\n");
        return appPolicyToString.toString();
    }

    /**
     * Converts the Component object to an XML string
     * 
     * @return componentXML : the resulting XML string
     */
    public String toXML()
    {
        XStream xstream = new XStream(new XppDriver(new XmlFriendlyReplacer(
            "$", "_")));
        setXstreamProps(xstream);
        return xstream.toXML(this);
    }

    /**
     * Converts an incoming XML string to a Component object
     * 
     * @param policyXML : the incoming XML string
     * @return the ApplicationPolicy
     */
    public static EncryptionPolicy fromXML(String policyXML)
    {
        XStream xstream = new XStream(new DomDriver());
        setXstreamProps(xstream);
        return (EncryptionPolicy) xstream.fromXML(policyXML);
    }

    /**
     * Sets the common properties for the toXML/fromXML XStream object
     * 
     * @param xstream
     */
    protected static void setXstreamProps(XStream xstream)
    {
        xstream.registerConverter(new EncryptionPolicyConverter());
        xstream.registerConverter(new AlgorithmConverter());
        xstream.alias(EncryptionPolicy.class.getSimpleName(), EncryptionPolicy.class);
    }

    /**
     * Policy equality test - overrides equals method
     * 
     * @param oPolicy
     * @return true if policies match 
     */
    @Override
    public boolean equals(Object oPolicy)
    {
        // Check if the object oPolicy is EncryptionPolicy
        if (oPolicy instanceof EncryptionPolicy)
        {
            EncryptionPolicy policy = (EncryptionPolicy) oPolicy;
            if ((this.getName() == null && policy.getName() == null)
                    || this.getName() != null
                    && this.getName().equals(policy.getName())
                    && (this.getAlgorithm() == null && policy.getAlgorithm() == null)
                    || this.getAlgorithm() != null
                    && this.getAlgorithm().equals(policy.getAlgorithm()))
                return true;
        }
        return false;
    }
}
