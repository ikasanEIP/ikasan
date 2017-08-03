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
package org.ikasan.spec.event;

/**
 * Interface for a ManagedEventIdentifierService providing the contract for any 
 * business event identifier to be added on the creation of a business event.
 * 
 * The Event Identifier is a business identifier immutable for the business
 * life of the event.
 * 
 * @author Ikasan Development Team
 *
 */
public interface ManagedEventIdentifierService<IDENTIFIER,EVENT>
{
    /** provide consistent properties for access */
    String EVENT_LIFE_ID = "IkasanEventLifeIdentifier";
    
    /**
     * Set the event life identifier based on the incoming event implementation.
     * The incoming identifier should always be a valid object - never 'null',
     * although this is not enforced in the code.
     * 
     * @param identifier
     * @param event
     * @throws ManagedEventIdentifierException
     */
    void setEventIdentifier(IDENTIFIER identifier, EVENT event)
        throws ManagedEventIdentifierException;

    /**
     * Get the life identifier for the incoming event.
     * This will either return a value for the life identifier or
     * throw an exception if it cannot be obtained.
     * This should never return 'null'.
     * 
     * @param event
     * @return IDENTIFIER
     */
    IDENTIFIER getEventIdentifier(EVENT event)
        throws ManagedEventIdentifierException;
}
