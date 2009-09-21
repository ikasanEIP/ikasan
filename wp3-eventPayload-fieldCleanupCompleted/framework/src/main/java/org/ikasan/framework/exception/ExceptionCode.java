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
package org.ikasan.framework.exception;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * Exception code instance defining the major and minor codes for any exception
 * occurrence.
 * 
 * @author Ikasan Development Team
 */
public class ExceptionCode
    implements Serializable
{

    /** Serial ID */
    private static final long serialVersionUID = 1L;
    
    /** Logger */
    private static Logger logger = Logger.getLogger(ExceptionCode.class);

    /** Major code */
    private Integer majorCode = new Integer(0);

    /** Major description */
    private String majorDesc = null;

    /** Minor code */
    private Integer minorCode = new Integer(0);

    /** Minor description */
    private String minorDesc = null;

    /**
     * Constructor which takes all string values (major and minore codes are
     * strings)
     * 
     * @param majorCode
     * @param majorDesc
     * @param minorCode
     * @param minorDesc
     */
    public ExceptionCode(String majorCode, String majorDesc, String minorCode,
                         String minorDesc)
    {
        this(new Integer(majorCode), majorDesc, new Integer(minorCode),
             minorDesc);
    }

    /**
     * Default constructor (major and minore codes are integers)
     * 
     * @param majorCode
     * @param majorDesc
     * @param minorCode
     * @param minorDesc
     */
    public ExceptionCode(Integer majorCode, String majorDesc,
                         Integer minorCode, String minorDesc)
    {

        this.majorCode = majorCode;
        this.majorDesc = majorDesc;
        this.minorCode = minorCode;
        this.minorDesc = minorDesc;
    }

    /**
     * Getter method for major code.
     * 
     * @return int majorCode
     */
    public Integer getMajorCode()
    {
        return this.majorCode;
    }

    /**
     * Setter method for major code.
     * 
     * @param majorCode
     */
    public void setMajorCode(final Integer majorCode)
    {
        this.majorCode = majorCode;
    }

    /**
     * Getter method for major code Description.
     * 
     * @return String majorDesc
     */
    public String getMajorDesc()
    {
        return this.majorDesc;
    }

    /**
     * Setter method for major code Description.
     * 
     * @param majorDesc
     */
    public void setMajorDesc(final String majorDesc)
    {
        this.majorDesc = majorDesc;
    }

    /**
     * Getter method for minor code.
     * 
     * @return int minorCode
     */
    public Integer getMinorCode()
    {
        return this.minorCode;
    }

    /**
     * Setter method for minor code.
     * 
     * @param minorCode
     */
    public void setMinorCode(final Integer minorCode)
    {
        this.minorCode = minorCode;
    }

    /**
     * Getter method for minor code Description.
     * 
     * @return int minorDescription
     */
    public String getMinorDesc()
    {
        return this.minorDesc;
    }

    /**
     * Setter method for minor code Description.
     * 
     * @param minorDesc
     */
    public void setMinorDesc(final String minorDesc)
    {
        this.minorDesc = minorDesc;
    }

    /**
     * ToString
     */
    @Override
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("majorCode [" + getMajorCode() + "],");
        buff.append("majorDesc [" + getMajorDesc() + "],");
        buff.append("minorCode [" + getMinorCode() + "],");
        buff.append("minorDesc [" + getMinorDesc() + "]");

        return buff.toString();
    }

    /**
     * @param args
     * Unit Test
     */
    public static void main(String[] args)
    {
        ExceptionCode ec = new ExceptionCode(1, "testMajorCode", 10,
            "testMinorCode");

        if (logger.isDebugEnabled())
        {
            logger.debug("Created ExceptionCode instance");
            logger.debug("majorCode [" + ec.getMajorCode() + "].");
            logger.debug("majorDesc [" + ec.getMajorDesc() + "].");
            logger.debug("minorCode [" + ec.getMinorCode() + "].");
            logger.debug("minorDesc [" + ec.getMinorDesc() + "].");
        }
    }

}
