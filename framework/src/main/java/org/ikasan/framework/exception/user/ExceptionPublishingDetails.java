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
