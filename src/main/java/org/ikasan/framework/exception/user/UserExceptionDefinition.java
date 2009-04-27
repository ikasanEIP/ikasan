/*
 * $Id: UserExceptionDefinition.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/exception/user/UserExceptionDefinition.java $
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
