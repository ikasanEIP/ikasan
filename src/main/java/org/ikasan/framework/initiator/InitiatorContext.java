/*
 * $Id: InitiatorContext.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/initiator/InitiatorContext.java $
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
package org.ikasan.framework.initiator;

import org.ikasan.framework.exception.IkasanExceptionAction;

/**
 * Ikasan initiator context interface providing a context allowing all initiator
 * specific implementations to be handled generically with regards to initiator 
 * retry cycles for recovering from Ikasan Exception Actions.
 * 
 * @author Ikasan Development Team
 */
public interface InitiatorContext
{
    /**
     * Constant for infinity
     */
    public static int INFINITE = -1;
    
    /** 
     * Get the last IkasanExceptionAction for this initiator.
     * @return IkasanExceptionAction
     */
    public IkasanExceptionAction getIkasanExceptionAction();

    /** 
     * Set the last IkasanExceptionAction for this initiator.
     * @param action
     */
    public void setIkasanExceptionAction(IkasanExceptionAction action);

    /** 
     * Clears the retry cycle stats on completion of a retry cycle for this initiator.
     */
    public void clearRetry();

    /** 
     * Get the consecutive retry count for this initiator.
     * @return int
     */
    public int getRetryCount();

    /** 
     * Set the consecutive retry count for this initiator.
     * @param retryCount 
     */
    public void setRetryCount(int retryCount);
}
