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
package org.ikasan.common.security.algo;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * Abstract class representing a generic algorithm. This class should be
 * extended to create a concrete implementation for an algorithm such as PBE or
 * Blowfish.
 * 
 * @author Ikasan Development Team
 */
public abstract class Algorithm
{
    /** Logger */
    private static Logger logger = Logger.getLogger(Algorithm.class);
    /** pass */
    private String pass;
    /** cipher */
    private String cipher;

    /**
     * Default constructor
     */
    public Algorithm()
    {
        // Do Nothing
    }

    /**
     * Sets the algorithm password or phrase.
     * 
     * @param pass
     */
    public void setPass(final String pass)
    {
        this.pass = pass;
        logger.debug("Setting pass [" + this.pass + "].");
    }

    /**
     * Gets the algorithm pass.
     * 
     * @return pass
     */
    public String getPass()
    {
        logger.debug("Getting pass [" + this.pass + "].");
        return this.pass;
    }

    /**
     * Sets the algorithm cipher.
     * 
     * @param cipher
     */
    public void setCipher(final String cipher)
    {
        this.cipher = cipher;
        logger.debug("Setting cipher [" + this.cipher + "].");
    }

    /**
     * Gets the algorithm cipher.
     * 
     * @return cipher
     */
    public String getCipher()
    {
        logger.debug("Getting cipher [" + this.cipher + "].");
        return this.cipher;
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("pass [");
        sb.append(this.getPass());
        sb.append("]\n");
        sb.append("cipher [");
        sb.append(this.getCipher());
        sb.append("]\n");
        return sb.toString();
    }

    /**
     * Algorithm equality test
     * 
     * @param algorithm
     * @return boolean
     */
    public boolean equals(final Algorithm algorithm)
    {
        if ((this.getPass() == null && algorithm.getPass() == null)
                || this.getPass() != null
                && this.getPass().equals(algorithm.getPass())
                && (this.getCipher() == null && algorithm.getCipher() == null)
                || this.getCipher() != null
                && this.getCipher().equals(algorithm.getCipher())) return true;
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
     * @return the Algorithm
     */
    public Algorithm fromXML(String xml)
    {
        XStream xstream = new XStream(new DomDriver());
        this.setXstreamProps(xstream);
        return (Algorithm) xstream.fromXML(xml);
    }

    /**
     * Sets the common properties for the toXML/fromXML XStream object
     * 
     * @param xstream
     */
    protected void setXstreamProps(XStream xstream)
    {
        xstream.registerConverter(new AlgorithmConverter());
        xstream.alias(this.getClass().getSimpleName(), this.getClass());
    }

    /**
     * A method to encrypt the raw text.
     * 
     * Base classes must implement this method.
     * 
     * @param rawText 
     * @return encoded text
     *  
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchPaddingException 
     * @throws InvalidKeyException 
     * @throws IllegalBlockSizeException 
     * @throws BadPaddingException 
     */
    public abstract Object encode(String rawText)
            throws NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidAlgorithmParameterException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException;

    /**
     * A method to decode the encoded text.
     * 
     * Base classes must implement this method.
     * 
     * @param encodedText 
     * @return decoded text
     *  
     * @throws InvalidKeySpecException 
     * @throws NoSuchAlgorithmException 
     * @throws NoSuchPaddingException 
     * @throws InvalidKeyException 
     * @throws InvalidAlgorithmParameterException 
     * @throws IllegalBlockSizeException 
     * @throws BadPaddingException 
     */
    public abstract Object decode(String encodedText)
            throws InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException;
}
