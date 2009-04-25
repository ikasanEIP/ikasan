/*
 * $Id: SFTPConnectionProperties.java 16794 2009-04-24 13:27:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-sftp/src/main/java/org/ikasan/connector/sftp/outbound/SFTPConnectionProperties.java $
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
package org.ikasan.connector.sftp.outbound;

// Imported log4j classes
import org.apache.log4j.Logger;

import org.ikasan.connector.base.outbound.EISConnectionProperties;

/**
 * SFTPConnectionProperties defines enumeration types for the session
 * configuration.
 *
 * @author Ikasan Development Team
 */
public enum SFTPConnectionProperties
    implements EISConnectionProperties
{

    // User session properties
    /** Cleanup Journal On Complete */
    CLEANUP_JOURNAL_ON_COMPLETE("cleanupJournalOnComplete"), //$NON-NLS-1$
    /** Component Group Name */
    COMPONENT_GROUP("componentGroup"),
    /** Component Status URL */
    COMPONENT_STATUS_URL("componentStatusURL"),
    /** Host */
    HOST("hostname"), //$NON-NLS-1$
    /** Known hosts file */
    KNOWN_HOSTS("knownHosts"), //$NON-NLS-1$
    /** Maximum number of error/retry attempts before dropping the line */
    MAX_RETRIES("maxRetries"), //$NON-NLS-1$
    /** No Trasaction Connection Factory Handle */
    NO_TXN_CF_HANDLE("sftpNoTxnConnectionFactoryHandle"),
    /** Client poll time */
    POLLTIME("pollTime"), //$NON-NLS-1$
    /** Port */
    PORT("port"), //$NON-NLS-1$
    /** Private key file */
    PRIVATE_KEY("privateKey"), //$NON-NLS-1$
    /** Username */
    USERNAME("username"), //$NON-NLS-1$
    /** XA Connection Factory Handle */
    XA_CF_HANDLE("sftpXAConnectionFactoryHandle");

    /** Serialize ID */
    private static final long serialVersionUID = 1L;

    /** The logger instance. */
    private Logger logger = Logger.getLogger(SFTPConnectionProperties.class);

    /** Property Name */
    private final String name;

    /**
     * Creates a new instance of <code>SFTPConnectionProperties</code>
     * with the specified property name.
     *
     * @param name Property name
     */
    private SFTPConnectionProperties(final String name)
    {
        this.name = name;
        logger.debug("Created " + this.getClass().getName() //$NON-NLS-1$
                + " for [" + this.name + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        return this.name;
    }

}
