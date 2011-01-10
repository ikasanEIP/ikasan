/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */

package org.ikasan.filter.duplicate.model;

import java.util.Date;

/**
 * Interface defining what a persisted message should look like.
 * 
 * @author Ikasan Development Team
 *
 */
public interface FilterEntry
{
    /**
     * Constant property name for clientId field in any {@link FilterEntry}
     * implementors
     */
    public final static String CLIENT_ID_PROP_KEY = "clientId";

    /**
     * Constant property name for criteria field in any {@link FilterEntry}
     * implementors
     */
    public final static String CRITERIA_PROP_KEY = "criteria";

    /**
     * Constant property name for expiry field in any {@link FilterEntry}
     * implementors
     */
    public final static String EXPRIY_PROP_KEY = "expiry";

    /** 
     * Getter for a clientId variable. Together with the criteria,
     * it identifies a persisted {@link FilterEntry}
     * 
     * @return The client id
     */
    public String getClientId();

    /** Getter for a criteria variable: object unique about a message.
     * Together with clientId, it identifies a persisted {@link FilterEntry}
     * 
     * @return criteria object whatever it might be.
     */
    public Integer getCriteria();

    /**
     * Getter for {@link Date} object representing the date/time a {@link FilterEntry}
     * was persisted.
     * @return a creation date.
     */
    public Date getCreatedDateTime();

    /**
     * Getter for {@link Date} object representing the date/time a {@link FilterEntry}
     * is expired and can be removed from persistance.
     * @return an expiry date
     */
    public Date getExpiry();
}
