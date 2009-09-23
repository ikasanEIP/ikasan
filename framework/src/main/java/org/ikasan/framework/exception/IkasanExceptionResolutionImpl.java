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
package org.ikasan.framework.exception;

/**
 * Concrete implementation of the Ikasan Exception Resolution interface.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionResolutionImpl implements IkasanExceptionResolution
{
    /** Emergency id constant */
    private static String EMERGENCY_ID = "emergencyResolution";

    /** Static emergency action */
    private static IkasanExceptionAction emergencyAction = new IkasanExceptionActionImpl(
        IkasanExceptionActionType.ROLLBACK_STOP, new Long(0), new Integer(0));

    /** Static emergency resolution */
    private static IkasanExceptionResolution emergencyResolution = new IkasanExceptionResolutionImpl(EMERGENCY_ID,
        emergencyAction);

    /** Resolution identifier */
    private String id;

    /** Resolution action */
    private IkasanExceptionAction action;

    /** Default constructor */
    public IkasanExceptionResolutionImpl()
    {
        // Default constructor
    }

    /**
     * Constructor
     * 
     * @param id The id of this resolution
     * @param action The action to take
     */
    public IkasanExceptionResolutionImpl(final String id, final IkasanExceptionAction action)
    {
        this.id = id;
        this.action = action;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the action
     */
    public IkasanExceptionAction getAction()
    {
        return this.action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(IkasanExceptionAction action)
    {
        this.action = action;
    }

    /**
     * Get fixed emergency resolution. This is required if things have really gone wrong to the extent the exception
     * handling process cannot take the normal route to resolving an exception to a resolution.
     * 
     * @return IkasanExceptionResolution - required as a last resort to stop when things have gone critically wrong.
     */
    public static IkasanExceptionResolution getEmergencyResolution()
    {
        return emergencyResolution;
    }
}
