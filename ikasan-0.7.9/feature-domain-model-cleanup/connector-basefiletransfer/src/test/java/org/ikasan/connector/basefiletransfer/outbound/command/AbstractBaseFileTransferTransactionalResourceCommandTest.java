/*
 * $Id: AbstractBaseFileTransferTransactionalResourceCommandTest.java 168 2009-06-15 12:22:10Z magicduncan $
 * $URL: https://ikasaneip.svn.sourceforge.net/svnroot/ikasaneip/branches/ikasan-0.7.x/connector-basefiletransfer/src/test/java/org/ikasan/connector/basefiletransfer/outbound/command/AbstractBaseFileTransferTransactionalResourceCommandTest.java $
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
