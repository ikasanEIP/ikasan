/*
 * $Id$
 * $URL$
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
package org.ikasan.connector;

import org.ikasan.common.CommonContext;

/**
 * This interface defines the connector context methods to allow support
 * of all connectors in both managed and non-managed environments.
 *
 * @author Ikasan Development Team
 */  
public interface ConnectorContext
    extends CommonContext
{ 
    /** connector Transaction Manager */
    public String TRANSACTION_MANAGER =
        ResourceLoader.getInstance().getProperty("transaction.manager"); //$NON-NLS-1$

    /** connector User Transaction */
    public String USER_TRANSACTION =
        ResourceLoader.getInstance().getProperty("user.transaction"); //$NON-NLS-1$

    /** connector non transactional persistence factory */
    public String DS_SESSION_FACTORY =
        ResourceLoader.getInstance().getProperty("ds.session.factory"); //$NON-NLS-1$

    /** connector local transactional persistence factory */
    public String LOCALDS_SESSION_FACTORY =
        ResourceLoader.getInstance().getProperty("localds.session.factory"); //$NON-NLS-1$

    /** connector XA transactional persistence factory */
    public String XADS_SESSION_FACTORY =
        ResourceLoader.getInstance().getProperty("xads.session.factory"); //$NON-NLS-1$

} 
 

