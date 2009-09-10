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

/**
 * This class represents the (<code>Blowfish</code>) algorithm.
 * 
 * @author Ikasan Development Team
 */
public class Blowfish extends Algorithm
{
    /** Logger */
    private static Logger logger = Logger.getLogger(Blowfish.class);

    /**
     * Creates a new <code>Blowfish</code> algorithm instance.
     */
    public Blowfish()
    {
        // does nothing
    }

    /**
     * Sets the common properties for the toXML/fromXML XStream object
     * 
     * @param xstream
     */
    @Override
    protected void setXstreamProps(XStream xstream)
    {
        xstream.registerConverter(new BlowfishConverter());
        logger.debug("Registered the Blowfish converter");
        xstream.alias(this.getClass().getSimpleName(), this.getClass());
    }

    @Override
    public Object encode(String rawText) throws NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException
    {
        logger.info("== Not yet implemented ==");
        return null;
    }

    @Override
    public Object decode(String encodedText) throws InvalidKeySpecException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException

    {
        logger.info("== Not yet implemented ==");
        return null;
    }
}
