/*
 * $Id: AbstractBaseFileTransferTransactionalResourceCommandTest.java 168 2009-06-15 12:22:10Z magicduncan $
 * $URL: https://ikasaneip.svn.sourceforge.net/svnroot/ikasaneip/branches/ikasan-0.7.x/connector-basefiletransfer/src/test/java/org/ikasan/connector/basefiletransfer/outbound/command/AbstractBaseFileTransferTransactionalResourceCommandTest.java $
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

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.Assert;

import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * Test the AbstractBaseFileTransferTransactionalResourceCommand class
 * @author Ikasan Development Team
 */
public class AbstractBaseFileTransferTransactionalResourceCommandTest extends
        AbstractBaseFileTransferTransactionalResourceCommand
{
    /** Mockery for mocking test objects */
    private Mockery mockery = new Mockery();

    /** A mocked File transfer client */
    FileTransferClient fileTransferClient = mockery.mock(FileTransferClient.class);

    /**
     * Tests finding a file with the correct path
     * 
     * @throws ResourceException Exception from the JCA connector
     * @throws ClientCommandLsException Could not List directory
     * @throws URISyntaxException Could not locate file
     */
    @Test
    public void testFindFile_withExactlyMatchingPath() throws ResourceException, ClientCommandLsException,
            URISyntaxException
    {
        setTransactionalResource(fileTransferClient);
        final String directoryPath = "parentDir"+File.pathSeparator+"subDir";
        final String fileName = "fileName";
        final ClientListEntry clientListEntry = new ClientListEntry();
        clientListEntry.setName(fileName);
        final List<ClientListEntry> directoryListing = new ArrayList<ClientListEntry>();
        directoryListing.add(clientListEntry);
        mockery.checking(new Expectations()
        {
            {
                one(fileTransferClient).ls(directoryPath);
                will(returnValue(directoryListing));
            }
        });
        ClientListEntry identifiedFile = findFile(directoryPath + "/" + fileName);
        Assert.assertEquals("file identified by findFile should be the one that matches on path and filename",
            clientListEntry, identifiedFile);
    }

    /**
     * This test was written to identify and test a bug with the findFile method. At time of writing, this method would
     * return a match for any files found that end with the name of the file in the filePath requested. Thus if we try
     * to find "parentDir/subDir/fileName", and a file exists "parentDir/subDir/similar-fileName" then that would be
     * matched and returned. This should clearly not be the case!
     * 
     * @throws ResourceException Exception from the JCA connector
     * @throws ClientCommandLsException Could not List directory
     * @throws URISyntaxException Could not locate file
     */
    @Test
    public void testFindFile_withSimilarlyEndingPath() throws ResourceException, ClientCommandLsException,
            URISyntaxException
    {
        setTransactionalResource(fileTransferClient);
        final String directoryPath = "parentDir"+File.separator+"subDir";
        final String fileName = "fileName";
        final ClientListEntry similarClientListEntry = new ClientListEntry();
        similarClientListEntry.setName("similar-fileName");
        final List<ClientListEntry> directoryListing = new ArrayList<ClientListEntry>();
        directoryListing.add(similarClientListEntry);
        mockery.checking(new Expectations()
        {
            {
                one(fileTransferClient).ls(directoryPath);
                will(returnValue(directoryListing));
            }
        });
        ClientListEntry identifiedFile = findFile(directoryPath + "/" + fileName);
        Assert
            .assertNull(
                "no file should be identified when the only dir entry is one with a file name ending with the requested file name, but not matching it",
                identifiedFile);
    }

    @Override
    protected ExecutionOutput performExecute()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void doCommit()
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void doRollback()
    {
        // TODO Auto-generated method stub
    }
}
