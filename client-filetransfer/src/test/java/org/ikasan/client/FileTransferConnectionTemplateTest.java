/* 
 * $Id: FileTransferConnectionTemplateTest.java 16744 2009-04-22 10:05:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/client-filetransfer/src/test/java/org/ikasan/client/FileTransferConnectionTemplateTest.java $
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
