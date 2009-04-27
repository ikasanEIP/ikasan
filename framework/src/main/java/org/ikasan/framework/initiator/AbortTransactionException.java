 /* 
 * $Id: AbortTransactionException.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/initiator/AbortTransactionException.java $
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

/**
 * Runtime exception definition thrown to instruct the transaction manager 
 * to 'rollback' (abort) a transaction in the context of a failure.
 * 
 * This exception has no other information or value other than 
 * to communicate the desired action to the transaction manager.
 * 
 * @author Ikasan Development Team
 *
 */
public class AbortTransactionException 
    extends RuntimeException
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3203142027287174087L;
 
    /**
     * Constructor
     */
    public AbortTransactionException()
    {
        // does nothing;
    }
    
}
