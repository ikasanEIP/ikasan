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
