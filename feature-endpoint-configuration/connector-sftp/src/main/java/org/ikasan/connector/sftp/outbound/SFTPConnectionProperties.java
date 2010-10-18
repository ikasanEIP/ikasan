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
