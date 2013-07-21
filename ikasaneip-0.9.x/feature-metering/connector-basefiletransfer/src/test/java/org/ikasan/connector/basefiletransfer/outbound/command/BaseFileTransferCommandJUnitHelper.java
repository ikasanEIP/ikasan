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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.transaction.xa.Xid;

import org.ikasan.connector.base.command.TransactionalResourceCommand;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.base.journal.TransactionJournalingException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.jmock.Expectations;
import org.jmock.Mockery;

/**
 * Base class for common file transfer test code
 * 
 * @author Ikasan Development Team
 */
public class BaseFileTransferCommandJUnitHelper 
{

    /** One hour, in milliseconds */
    private static final long ONE_HOUR_IN_MILLIS = 60 * 60 * 1000;

    /**
     * Created an ClientListEntry
     * 
     * @param uri
     * @return clientEntryList
     * @throws URISyntaxException
     * @throws URISyntaxException
     */
    public static ClientListEntry createEntry(String uri) throws URISyntaxException
    {
        Date lastModified = new Date((new Date().getTime()) - ONE_HOUR_IN_MILLIS);
        return createEntry(uri, 1024l, lastModified);
    }

    /**
     * Create an ClientListEntry
     * 
     * @param uri
     * @param lastAccessed 
     * @return clientEntryList
     * @throws URISyntaxException
     */
    public static ClientListEntry createEntry(String uri, Date lastAccessed) throws URISyntaxException
    {
        String universalResourceLocator = uri;
        while (universalResourceLocator.indexOf("\\") > -1)
        {
            universalResourceLocator = universalResourceLocator.replace('\\', '/');
        }
        return createEntry(universalResourceLocator, 1024l, lastAccessed);
    }

    /**
     * Create an ClientListEntry
     * 
     * @param uri
     * @param size
     * @param lastModified 
     * @return new ClientListEntry
     * @throws URISyntaxException
     */
    public static ClientListEntry createEntry(String uri, long size, Date lastModified) throws URISyntaxException
    {
    	
    	
        ClientListEntry clientListEntry = new ClientListEntry();
        clientListEntry.setClientId("TestClient"); //$NON-NLS-1$
        System.out.println("about to create uri with:"+uri);
        clientListEntry.setUri(new URI(uri));
        System.out.println("success");
        clientListEntry.setName(uri);
        clientListEntry.setDtLastModified(lastModified);
        clientListEntry.setDtLastAccessed(new Date());
        clientListEntry.setSize(size);
        return clientListEntry;
    }

    /**
     * Get the transactional journal
     * 
     * @param command
     * @param notifyCount
     * @return new ClientListEntry
     */
    public static TransactionJournal getTransactionJournal(final TransactionalResourceCommand command, final int notifyCount)
    {
        // mock the transaction journal
        Mockery tjMockery = new Mockery();
        final TransactionJournal transactionJournal = tjMockery.mock(TransactionJournal.class);

        final XidImpl xidImpl = new XidImpl(new byte[0], new byte[0], 0);
        try
        {
            tjMockery.checking(new Expectations()
            {
                {
                    allowing(transactionJournal).resolveXid((Xid) with(a(Xid.class)));
                    will(returnValue(xidImpl));
                    exactly(notifyCount).of(transactionJournal).notifyUpdate(command);
                }
            });
        }
        catch (TransactionJournalingException e)
        {
            e.printStackTrace();
        }
        return transactionJournal;
    }
}
