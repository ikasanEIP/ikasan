/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
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
