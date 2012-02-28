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
package org.ikasan.client;

import java.util.HashMap;
import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;

import junit.framework.TestCase;

import org.ikasan.common.Payload;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;
import org.jmock.Expectations;
import org.jmock.Mockery;

/**
 * @author Ikasan Development Team
 * 
 */
public class FileTransferConnectionTemplateTest extends TestCase
{
    
    /**
     * Mockery for interfaces
     */
    private Mockery mockery = new Mockery();
    
    /**
     * Mocked ConnectionFactory
     */
    ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class);
    
    /**
     * Mocked BaseFileTransferConnection
     */
    BaseFileTransferConnection connection = mockery.mock(BaseFileTransferConnection.class);
  
    /**
     * Mocked Payload
     */
    Payload payload = mockery.mock(Payload.class);
    
    /**
     * Test method for
     * {@link org.ikasan.client.FileTransferConnectionTemplate#deliverPayload(org.ikasan.common.Payload, java.lang.String, java.util.Map, boolean, java.lang.String, boolean, boolean, boolean)}.
     * @throws ResourceException Exception thrown by connector
     */
    public void testDeliverPayload() throws ResourceException
    {
        final String outputDir="outputDir";
        final Map<String, String> outputTargets=new HashMap<String, String>();
        final boolean overwrite = true;
        final String renameExtension = ".rename";
        final boolean checksumDelivered = false;
        final boolean unzip = false;
        final boolean cleanup = false;
        
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection();
                will(returnValue(connection));
                one(connection).deliverPayload(payload, outputDir, outputTargets, overwrite, renameExtension, checksumDelivered, unzip, cleanup);
                one(connection).close(); 
            }
        });
        FileTransferConnectionTemplate fileTransferConnectionTemplate = new FileTransferConnectionTemplate(connectionFactory,null);
        
        fileTransferConnectionTemplate.deliverPayload(payload, outputDir, outputTargets, overwrite, renameExtension, checksumDelivered, unzip, cleanup);
        mockery.assertIsSatisfied();
    }

    /**
     * Test method for
     * {@link org.ikasan.client.FileTransferConnectionTemplate#getDiscoveredFile(java.lang.String, java.lang.String, boolean, java.lang.String, boolean, java.lang.String, boolean, int, boolean, long, boolean, boolean, boolean, boolean, boolean)}.
     * @throws ResourceException Exception thrown by connector
     */
    public void testGetDiscoveredFile() throws ResourceException
    {
        
        final String sourceDir="sourceDir"; 
        final String filenamePattern="filenamePattern";       
        final boolean renameOnSuccess = false;
        final String renameOnSuccessExtension="renameOnSuccessExtension";     
        final boolean moveOnSuccess = false;       
        final String moveOnSuccessNewPath="moveOnSuccessNewPath";            
        final boolean chunking = false;
        final int chunkSize = 100;       
        final boolean checksum = false;
        final int minAge = 100;  
        final boolean destructive = false;
        final boolean filterDuplicates = false;
        final boolean filterOnFilename=true; 
        final boolean filterOnLastModifedDate=true; 
        final boolean chronological = false;
        
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection();
                will(returnValue(connection));
                one(connection).getDiscoveredFile(sourceDir, filenamePattern, renameOnSuccess, renameOnSuccessExtension, moveOnSuccess, moveOnSuccessNewPath, chunking, chunkSize, checksum,minAge, destructive, filterDuplicates, filterOnFilename, filterOnLastModifedDate, chronological);
                one(connection).close(); 
            }
        });
        FileTransferConnectionTemplate fileTransferConnectionTemplate = new FileTransferConnectionTemplate(connectionFactory,null);
        
        
        fileTransferConnectionTemplate.getDiscoveredFile(sourceDir, filenamePattern, renameOnSuccess, renameOnSuccessExtension, moveOnSuccess, moveOnSuccessNewPath, chunking, chunkSize, checksum, minAge, destructive, filterDuplicates, filterOnFilename, filterOnLastModifedDate, chronological);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test method for
     * {@link org.ikasan.client.FileTransferConnectionTemplate#housekeep(int, int) }.
     * @throws ResourceException Exception thrown by connector 
     */
    public void testHousekeep() throws ResourceException
    {
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection();
                will(returnValue(connection));
                one(connection).housekeep(99, 99);
                one(connection).close(); 
            }
        });
        FileTransferConnectionTemplate fileTransferConnectionTemplate = new FileTransferConnectionTemplate(connectionFactory, null);
        fileTransferConnectionTemplate.housekeep(99, 99);
        mockery.assertIsSatisfied();
    }
    
}
