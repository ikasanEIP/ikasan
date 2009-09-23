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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Default implementation of a users exception configuration.
 * 
 * @author Ikasan Development Team
 */
public class UserExceptionDefinition
{
    /** User defined major code for this exception */
    private Integer majorCode;

    /** User defined minor code for this exception */
    private Integer minorCode;

    /** Drop duplicate exception */
    private Boolean dropDuplicate = new Boolean(false);

    /** Duplicate exception mask for specific identification of duplicates */
    private String duplicateMaskExpression;

    /** Duration period in milliseconds within which exceptions may be regarded as duplicates */
    private Long dropDuplicatePeriod = new Long(0);

    /** Depicts whether the exception is to be published */
    private Boolean publishable = new Boolean(true);

    /** Reference to an external representation of an exception */
    private String externalExceptionRef;

    /**
     * Constructor
     * 
     * @param majorCode The Major code
     * @param minorCode The Minor code
     * @param externalExceptionRef The external exception reference
     */
    public UserExceptionDefinition(final Integer majorCode, final Integer minorCode, final String externalExceptionRef)
    {
        this.majorCode = majorCode;
        this.minorCode = minorCode;
        this.externalExceptionRef = externalExceptionRef;
    }

    /**
     * Get the major code
     * 
     * @return the majorCode
     */
    public Integer getMajorCode()
    {
        return this.majorCode;
    }

    /**
     * Set the major code
     * 
     * @param majorCode the majorCode to set
     */
    public void setMajorCode(Integer majorCode)
    {
        this.majorCode = majorCode;
    }

    /**
     * Get the minor code
     * 
     * @return the minorCode
     */
    public Integer getMinorCode()
    {
        return this.minorCode;
    }

    /**
     * Set the minor code
     * 
     * @param minorCode the minorCode to set
     */
    public void setMinorCode(Integer minorCode)
    {
        this.minorCode = minorCode;
    }

    /**
     * Get the drop duplicates flag
     * 
     * @return the dropDuplicate
     */
    public Boolean getDropDuplicate()
    {
        return this.dropDuplicate;
    }

    /**
     * Set the drop duplicates flag
     * 
     * @param dropDuplicate the dropDuplicate to set
     */
    public void setDropDuplicate(Boolean dropDuplicate)
    {
        this.dropDuplicate = dropDuplicate;
    }

    /**
     * Get the drop duplicates masking expression
     * 
     * @return the duplicateMaskExpression
     */
    public String getDuplicateMaskExpression()
    {
        return this.duplicateMaskExpression;
    }

    /**
     * Set the drop duplicates masking expression
     * 
     * @param duplicateMaskExpression the duplicateMaskExpression to set
     */
    public void setDuplicateMaskExpression(String duplicateMaskExpression)
    {
        this.duplicateMaskExpression = duplicateMaskExpression;
    }

    /**
     * Get the drop duplicates period
     * 
     * @return the dropDuplicatePeriod
     */
    public Long getDropDuplicatePeriod()
    {
        return this.dropDuplicatePeriod;
    }

    /**
     * Set the drop duplicates period
     * 
     * @param dropDuplicatePeriod the dropDuplicatePeriod to set
     */
    public void setDropDuplicatePeriod(Long dropDuplicatePeriod)
    {
        this.dropDuplicatePeriod = dropDuplicatePeriod;
    }

    /**
     * Get the publishable flag
     * 
     * @return the publishable
     */
    public Boolean getPublishable()
    {
        return this.publishable;
    }

    /**
     * Set the publishable flag
     * 
     * @param publishable the publishable to set
     */
    public void setPublishable(Boolean publishable)
    {
        this.publishable = publishable;
    }

    /**
     * Get the publishable flag (standard bean invocation)
     * 
     * @return true if publishable
     */
    public Boolean isPublishable()
    {
        return this.publishable;
    }

    /**
     * Get the external exception reference
     * 
     * @return the externalExceptionRef
     */
    public String getExternalExceptionRef()
    {
        return this.externalExceptionRef;
    }

    /**
     * Set the external exception reference
     * 
     * @param externalExceptionRef the externalExceptionRef to set
     */
    public void setExternalExceptionRef(String externalExceptionRef)
    {
        this.externalExceptionRef = externalExceptionRef;
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
        UserExceptionDefinition ued = (UserExceptionDefinition) object;
        return (this.majorCode != null && this.majorCode.equals(ued.majorCode))
                && (this.minorCode != null && this.minorCode.equals(ued.minorCode))
                && (this.dropDuplicate != null && this.dropDuplicate.equals(ued.dropDuplicate))
                && ((this.duplicateMaskExpression == null && ued.duplicateMaskExpression == null) || (this.duplicateMaskExpression != null && this.duplicateMaskExpression
                    .equals(ued.duplicateMaskExpression)))
                && (this.dropDuplicatePeriod != null && this.dropDuplicatePeriod.equals(ued.dropDuplicatePeriod))
                && (this.publishable != null && this.publishable.equals(ued.publishable))
                && ((this.externalExceptionRef == null && ued.externalExceptionRef == null) || (this.externalExceptionRef != null && this.externalExceptionRef
                    .equals(ued.externalExceptionRef)));
    }

    @Override
    public int hashCode()
    {
        return (null == this.majorCode ? 0 : this.majorCode.hashCode())
                + (null == this.minorCode ? 0 : this.minorCode.hashCode())
                + (null == this.dropDuplicate ? 0 : this.dropDuplicate.hashCode())
                + (null == this.duplicateMaskExpression ? 0 : this.duplicateMaskExpression.hashCode())
                + (null == this.dropDuplicatePeriod ? 0 : this.dropDuplicatePeriod.hashCode())
                + (null == this.publishable ? 0 : this.publishable.hashCode())
                + (null == this.externalExceptionRef ? 0 : this.externalExceptionRef.hashCode());
    }

    /**
     * Default user exception definition to be used as a catch all
     * 
     * @return the Default user exception definition to be used as a catch all
     */
    public static UserExceptionDefinition getDefaultUserExceptionDefinition()
    {
        Integer defaultMajorCode = new Integer(999);
        Integer defaultMinorCode = new Integer(1);
        String defaultExternalRef = "default";
        return new UserExceptionDefinition(defaultMajorCode, defaultMinorCode, defaultExternalRef);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("dropDuplicatePeriod", this.dropDuplicatePeriod).append("majorCode",
            this.majorCode).append("duplicateMaskExpression", this.duplicateMaskExpression).append("publishable",
            this.publishable).append("externalExceptionRef", this.externalExceptionRef).append("minorCode",
            this.minorCode).append("dropDuplicate", this.dropDuplicate).toString();
    }
}
