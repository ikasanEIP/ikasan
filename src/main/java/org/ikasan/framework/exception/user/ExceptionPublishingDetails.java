 /* 
 * $Id: ExceptionPublishingDetails.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/exception/user/ExceptionPublishingDetails.java $
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

/**
 * Value object for Exception Publishing Configuration
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class ExceptionPublishingDetails
{
    /**
     * whether or not this Exception should be published
     */
    private boolean publishable;
    /**
     * Whether or not duplicates should be identified and prevented from publication
     */
    private boolean filterDuplicates;
    
    /**
     * Maximum age of a previous occurrence deemed to be a duplicate
     */
    private long maximumDuplicateAge;

    /**
     * @param publishable 
     * @param filterDuplicates
     * @param maximumDuplicateAge
     */
    public ExceptionPublishingDetails(boolean publishable, boolean filterDuplicates,
            long maximumDuplicateAge)
    {
        super();
        this.publishable=publishable;
        this.filterDuplicates = filterDuplicates;
        this.maximumDuplicateAge = maximumDuplicateAge;
    }

    /**
     * Accessor for filterDuplicates
     * 
     * @return filterDuplicates
     */
    public boolean isFilterDuplicates()
    {
        return filterDuplicates;
    }

    /**
     * Accessor for maximumDuplicateAge
     * 
     * @return maximumDuplicateAge
     */
    public long getMaximumDuplicateAge()
    {
        return maximumDuplicateAge;
    }

    /**
     * Accessor for publishable
     * 
     * @return publishable
     */
    public boolean isPublishable()
    {
        return publishable;
    }


}
