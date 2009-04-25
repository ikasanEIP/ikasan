 /* 
 * $Id: ConnectionCallback.java 16743 2009-04-22 09:58:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/client-connection/src/main/java/org/ikasan/client/ConnectionCallback.java $
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
package org.ikasan.client;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;

/**
 * Callback interface for JCA connection code. When used with ConnectionTemplate, code encapsulated
 * by the methods contained will be run against an open <code>Connection</code>
 * 
 * @author Ikasan Development Team
 *
 */
public interface ConnectionCallback
{
    
    /**
     * Code encapsulated by implementations of this will be run against the <code>Connection</code>
     * 
     * @param connection The connection
     * @return Object return from the work done in the Connection
     * @throws ResourceException Exception from the Connector
     */
    Object doInConnection(Connection connection) throws ResourceException;
}
