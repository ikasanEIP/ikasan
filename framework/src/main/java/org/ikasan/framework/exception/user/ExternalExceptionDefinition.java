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
package org.ikasan.framework.exception.user;

/**
 * Default implementation of a external exception configuration.
 * 
 * @author Ikasan Development Team
 */
public class ExternalExceptionDefinition
{
    /** Constant defining an unlimited payload size */
    public static final Integer UNLIMITED_PAYLOAD_SIZE = new Integer(-1);

    /** Default major code */
    private static final Integer DEFAULT_MAJOR_CODE = new Integer(99900);

    /** Default minor code */
    private static final Integer DEFAULT_MINOR_CODE = new Integer(1000);

    /** Default action */
    private static final ExternalUserAction DEFAULT_EXTERNAL_USER_ACTION = ExternalUserAction.RETRY;

    /** Default source system */
    private static String DEFAULT_SOURCE_SYSTEM = "undefined";

    /** Default system to return to */
    private static String DEFAULT_RETURN_SYSTEM = "undefined";

    /** The major code */
    private Integer majorCode;

    /** The minor code */
    private Integer minorCode;

    /** Potential user action */
    private ExternalUserAction userAction;

    /** Reference for the source system */
    private String sourceSystemRef;

    /** Reference for the return system */
    private String returnSystemRef;

    /** Restrict the payload size - default unlimited (-1) */
    private Integer maxPayloadSize = UNLIMITED_PAYLOAD_SIZE;

    /**
     * Defaulted Constructor
     * 
     * @param majorCode The major code
     */
    public ExternalExceptionDefinition(final Integer majorCode)
    {
        this.majorCode = majorCode;
        this.minorCode = DEFAULT_MINOR_CODE;
        this.userAction = DEFAULT_EXTERNAL_USER_ACTION;
        this.sourceSystemRef = DEFAULT_SOURCE_SYSTEM;
        this.returnSystemRef = DEFAULT_RETURN_SYSTEM;
    }

    /**
     * Constructor
     * 
     * @param majorCode The major code
     * @param minorCode The minor code
     * @param userAction The external user action
     * @param sourceSystemRef The source system reference
     * @param returnSystemRef The return system reference
     */
    public ExternalExceptionDefinition(final Integer majorCode, final Integer minorCode, ExternalUserAction userAction,
            String sourceSystemRef, String returnSystemRef)
    {
        this.majorCode = majorCode;
        this.minorCode = minorCode;
        this.userAction = userAction;
        this.sourceSystemRef = sourceSystemRef;
        this.returnSystemRef = returnSystemRef;
    }

    /**
     * @return the majorCode
     */
    public Integer getMajorCode()
    {
        return this.majorCode;
    }

    /**
     * @param majorCode the majorCode to set
     */
    public void setMajorCode(Integer majorCode)
    {
        this.majorCode = majorCode;
    }

    /**
     * @return the minorCode
     */
    public Integer getMinorCode()
    {
        return this.minorCode;
    }

    /**
     * @param minorCode the minorCode to set
     */
    public void setMinorCode(Integer minorCode)
    {
        this.minorCode = minorCode;
    }

    /**
     * @return the userAction
     */
    public ExternalUserAction getUserAction()
    {
        return this.userAction;
    }

    /**
     * @param userAction the userAction to set
     */
    public void setUserAction(ExternalUserAction userAction)
    {
        this.userAction = userAction;
    }

    /**
     * @return the returnSystemRef
     */
    public String getReturnSystemRef()
    {
        return this.returnSystemRef;
    }

    /**
     * @return the sourceSystemRef
     */
    public String getSourceSystemRef()
    {
        return this.sourceSystemRef;
    }

    /**
     * @param sourceSystemRef the sourceSystemRef to set
     */
    public void setSourceSystemRef(String sourceSystemRef)
    {
        this.sourceSystemRef = sourceSystemRef;
    }

    /**
     * @param returnSystemRef the returnSystemRef to set
     */
    public void setReturnSystemRef(String returnSystemRef)
    {
        this.returnSystemRef = returnSystemRef;
    }

    /**
     * @return the maxPayloadSize
     */
    public Integer getMaxPayloadSize()
    {
        return this.maxPayloadSize;
    }

    /**
     * @param maxPayloadSize the maxPayloadSize to set
     */
    public void setMaxPayloadSize(Integer maxPayloadSize)
    {
        this.maxPayloadSize = maxPayloadSize;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if ((object == null) || (object.getClass() != this.getClass()))
        {
            return false;
        }
        ExternalExceptionDefinition eed = (ExternalExceptionDefinition) object;
        return (this.majorCode != null && this.majorCode.equals(eed.majorCode))
                && (this.minorCode != null && this.minorCode.equals(eed.minorCode))
                && (this.userAction != null && this.userAction.equals(eed.userAction))
                && (this.maxPayloadSize != null && this.maxPayloadSize.equals(eed.maxPayloadSize))
                && ((this.sourceSystemRef == null && eed.sourceSystemRef == null) || (this.sourceSystemRef != null && this.sourceSystemRef
                    .equals(eed.sourceSystemRef)))
                && ((this.returnSystemRef == null && eed.returnSystemRef == null) || (this.returnSystemRef != null && this.returnSystemRef
                    .equals(eed.returnSystemRef)));
    }

    @Override
    public int hashCode()
    {
        return (null == this.majorCode ? 0 : this.majorCode.hashCode())
                + (null == this.minorCode ? 0 : this.minorCode.hashCode())
                + (null == this.userAction ? 0 : this.userAction.hashCode())
                + (null == this.maxPayloadSize ? 0 : this.maxPayloadSize.hashCode())
                + (null == this.sourceSystemRef ? 0 : this.sourceSystemRef.hashCode())
                + (null == this.returnSystemRef ? 0 : this.returnSystemRef.hashCode());
    }

    /**
     * Default External exception definition allows for catch all scenarios.
     * 
     * @return default ExternalExceptionDefinition
     */
    public static ExternalExceptionDefinition getDefaultExternalExceptionDefinition()
    {
        return new ExternalExceptionDefinition(DEFAULT_MAJOR_CODE, DEFAULT_MINOR_CODE, DEFAULT_EXTERNAL_USER_ACTION,
            DEFAULT_SOURCE_SYSTEM, DEFAULT_RETURN_SYSTEM);
    }
}
