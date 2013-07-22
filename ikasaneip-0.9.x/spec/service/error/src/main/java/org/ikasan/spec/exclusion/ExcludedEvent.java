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
package org.ikasan.spec.exclusion;

import java.util.Date;
import java.util.List;

import java.lang.IllegalStateException;
import org.ikasan.spec.error.ErrorOccurrence;

/**
 * @author The Ikasan Development Team
 * 
 */
public interface ExcludedEvent<T>
{
    /**
     * Resolution indicator for a Resubmitted ExcludedEvent
     */
    public static final String RESOLUTION_RESUBMITTED = "Resubmitted";

    /**
     * Resolution indicator for a Cancelled ExcludedEvent
     */
    public static final String RESOLUTION_CANCELLED = "Cancelled";

    /**
     * Marks the ExcludedEvent as Resubmitted, setting the lastUpdatedBy and
     * lastUpdatedTime fields
     * 
     * @param resolver to be used for lastUpdatedBy
     */
    public void resolveAsResubmitted(String resolver);
    
    /**
     * Marks the ExcludedEvent as Cancelled, setting the lastUpdatedBy and
     * lastUpdatedTime fields
     * 
     * @param resolver to be used for lastUpdatedBy
     */
    public void resolveAsCancelled(String resolver);
    
    /**
     * Accessor for event
     * 
     * @return event
     */
    public T getEvent();
    
    /**
     * Accessor for moduleName
     * 
     * @return moduleName
     */
    public String getModuleName();
    
    /**
     * Accessor for flowName
     * 
     * @return flowName
     */
    public String getFlowName();
    
    /**
     * Accessor for exclusionTime
     * 
     * @return exclusionTime
     */
    public Date getExclusionTime();
    
    /**
     * Accessor for resubmissionTime
     * 
     * @return resubmissionTime
     */
    public Date getLastUpdatedTime();
    
    /**
     * Mutator for lastUpdatedTime
     * 
     * @param lastUpdatedTime
     */
    public void setLastUpdatedTime(Date lastUpdatedTime);

    /**
     * Accessor for lastUpdatedBy
     * 
     * @return lastUpdatedBy
     */
    public String getLastUpdatedBy();

    /**
     * Mutator for lastUpdatedBy
     * 
     * @param lastUpdatedBy
     */
    public void setLastUpdatedBy(String lastUpdatedBy);
    
    /**
     * Accessor for errorOccurrences
     * 
     * @return listing of occurrences of errors processing the Event
     */
    public List<ErrorOccurrence<T>> getErrorOccurrences();
    
    /**
     * Mutator for errorOccurrences TODO would be great if this didnt need to be
     * public
     * 
     * @param errorOccurrences
     */
    public void setErrorOccurrences(List<ErrorOccurrence<T>> errorOccurrences);
    
    /**
     * Shortcut for determining if this has been resolved
     * 
     * @return true if resolution exists
     */
    public boolean isResolved();
    
    /**
     * Accessor for resolution
     * 
     * @return resolution
     */
    public String getResolution();
}
