/*
 * $Id: StringMasker.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/util/StringMasker.java $
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
package org.ikasan.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Simple behavioural class that encapsulates the logic of performing a masking
 * String substitution on a String where a known mask and pattern to match exist
 * 
 * @author duncro
 * 
 */
public class StringMasker
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(StringMasker.class);
    /**
     * The mask to use when replacing any matches
     */
    private String mask;
    /**
     * The pattern to match and replace
     */
    private Pattern pattern;

    /**
     * Constructor
     * 
     * @param regexp
     * @param mask
     */
    public StringMasker(String regexp, String mask)
    {
        this.mask = mask;
        this.pattern = Pattern.compile(regexp);
    }

    /**
     * @param text unmasked String
     * @return masked String
     */
    public String mask(String text)
    {
        Matcher matcher = pattern.matcher(text);
        String result = matcher.replaceAll(mask);
        logger.debug("masked ["+text+"] into ["+result+"]");
        return result;
    }
}
