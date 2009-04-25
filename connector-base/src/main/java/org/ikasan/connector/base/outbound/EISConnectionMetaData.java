/*
 * $Id: EISConnectionMetaData.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/base/outbound/EISConnectionMetaData.java $
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
package org.ikasan.connector.base.outbound;

import javax.resource.cci.ConnectionMetaData;
import javax.resource.ResourceException;

/**
 * provides information about an EIS instance connected through a Connection instance.
 * A component calls the method Connection.getMetaData() to get a ConnectionMetaData instance
 * actual adapter can not sub-class this class
 * @author Ikasan Development Team
 */
public abstract class EISConnectionMetaData 
    implements ConnectionMetaData
{

    /** The managed connection that this meta data belongs to */
    protected EISManagedConnection managedConnection;

    /**
     * Returns EIS name via the ManagedConnection
     */
    public String getEISProductName() throws ResourceException 
    {
        return this.managedConnection.getMetaData().getEISProductName();
    }

    /**
     * Returns the EIS version via the ManagedConnection
     */
    public String getEISProductVersion() throws ResourceException
    {
        return this.managedConnection.getMetaData().getEISProductVersion();
    }
}

