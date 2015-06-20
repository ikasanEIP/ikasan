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

/**
 * ExclusionService contract.
 * 
 * @author Ikasan Development Team
 */
public interface ExclusionService<ENTITY, IDENTIFIER>
{
    /** one year default time to live */
    public static final long DEFAULT_TIME_TO_LIVE = new Long(1000 * 60 * 60 * 24 * 365);

    /**
     * Is this identifier on the blacklist.
     *
     * @param identifier
     * @return
     */
    public boolean isBlackListed(IDENTIFIER identifier);

    /**
     * Add this identifier to the blacklist based on a specified identifier and associated URI
     * @param identifier
     * @param uri
     */
    public void addBlacklisted(IDENTIFIER identifier, String uri);

    /**
     * Get the errorUri from the blacklist associated with the identifier.
     */
    public String getErrorUri(IDENTIFIER identifier);

    /**
     * Park this entity
     * @param entity
     */
    public void park(ENTITY entity, IDENTIFIER identifier);

    /**
     * Remove this entity from the blacklist
     * @param identifier
     */
    public void removeBlacklisted(IDENTIFIER identifier);

    /**
     * Allow entities blacklisted to be marked with a timeToLive.
     * On expiry of the timeToLive the entity will no longer be blacklisted.
     *
     * @param timeToLive
     */
    public void setTimeToLive(Long timeToLive);

    /**
     * Housekeep expired exclusionEvents.
     */
    public void housekeep();
}
