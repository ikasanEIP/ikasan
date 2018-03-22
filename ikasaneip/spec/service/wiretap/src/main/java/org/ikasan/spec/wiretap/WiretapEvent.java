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
package org.ikasan.spec.wiretap;

/**
 * Ikasan WiretapFlowEvent Value Object.
 * 
 * @author Ikasan Development Team
 */
public interface WiretapEvent<TAPPED_EVENT>
{
    /**
     * Get immutable flow event identifier.
     * @return IDENTIFIER - event identifier
     */
    public long getIdentifier();

    /**
     * Get the module name from where this event is tapped
     * @return String
     */
    public String getModuleName();

    /**
     * Get the flow name from where this event is tapped
     * @return String 
     */
    public String getFlowName();

    /**
     * Get the component name from where this event is tapped
     * @return
     */
    public String getComponentName();

    /**
     * Get the immutable created date/time of the flow event.
     * @return long - create date time
     */
    public long getTimestamp();

    /**
     * Get the event within this wiretap.
     * @return EVENT event
     */
    public TAPPED_EVENT getEvent();

    /**
     * Get the expiry time of this event
     * @return long
     */
    public long getExpiry();

    /**
     * Get the event identifier
     *
     * @return
     */
    public String getEventId();
}