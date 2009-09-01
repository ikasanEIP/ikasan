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
