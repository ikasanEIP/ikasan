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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
import org.ikasan.connector.basefiletransfer.net.ClientCommandCdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandMkdirException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandPutException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandPwdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandRenameException;
import org.ikasan.connector.basefiletransfer.net.ClientException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * Test class for the DeliverFileCommand
 * 
 * @author Ikasan Development Team
 */
public class DeliverFileCommandTest extends TestCase
{
    /** we are dealing with pathnames so make sure we stay platform independent */
    final String FILE_SEPARATOR = System.getProperty("file.separator");

    /**
     * directory to deliver file to
     */
    final String outputDir = "outputDir";

    /**
     * rename extension
     */
    final String renameExtension = ".rename";

    /**
     * overwrite any existing files of the same name
     */
    boolean overwriteExisting = true;

    /**
     * name of the file being delivered
     */
    final String fileName = "fileName";

    /**
     * starting directory when we first connect
     */
    final String startingDir = "startingDir";

    /**
     * directory path that the file will be stored in
     */
    final File nestedFileDirectory = new File("parent1/parent2");

    /**
     * Deliverable file in a nested dir
     */
    final File nestedFile = new File(nestedFileDirectory, fileName);

    /**
     * deliverable content
     */
    final byte[] content = new byte[1024];

    /**
     * Command to be tested
     */
    private DeliverFileCommand deliverFileCommand = null;

    /**
     * Resultant contents of the output directory
     */
    final List<ClientListEntry> resultingDirectoryListing = new ArrayList<ClientListEntry>();
    
    /**
     * Mockery for concrete classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }

    };
    
    /** Mocked fileTransferClient */
    final FileTransferClient client = classMockery.mock(FileTransferClient.class);

    @Override
    public void setUp()
    {
        try
        {
            resultingDirectoryListing.add(BaseFileTransferCommandJUnitHelper.createEntry(fileName + renameExtension));
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        deliverFileCommand = new DeliverFileCommand(outputDir, renameExtension, overwriteExisting);
    }

    /**
     * Tests that the command successfully handles a non chunked data in the
     * form of a ready to deliver MappedRecord. Overwrite existing any file.
     * 
     * 
     * @throws ResourceException
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     */
    public void testExecute_withMappedRecordWithOverwrite() throws ResourceException, ClientCommandPwdException,
            ClientCommandCdException, ClientCommandPutException, ClientCommandLsException,
            URISyntaxException
    {

        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);
                // put the new file
                one(client).put(fileName + renameExtension, content);
                // return back to the starting dir
                one(client).cd(startingDir);
            }
        });

        BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord(fileName, content.length, "checksum", "checksumAlg", new Date(),
            content);
        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.BASE_FILE_TRANSFER_MAPPED_RECORD, record);
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 3));
        ExecutionOutput executionOutput = deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0],
            0));

        String tempFilename = (String) executionOutput.getResult();

        assertEquals("temp filename should be the filname with the rename extension", 
            outputDir + FILE_SEPARATOR + fileName + renameExtension, tempFilename);
    }

    /**
     * Tests that the command successfully handles a non chunked data in the
     * form of a ready to deliver MappedRecord. Delivery to default dot directory.
     * 
     * 
     * @throws ResourceException
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     */
    public void testExecute_withMappedRecordToDefaultDotDirectory() 
        throws ResourceException, ClientCommandPwdException,
            ClientCommandCdException, ClientCommandPutException, ClientCommandLsException,
            URISyntaxException
    {

        final String dotOutputDir = ".";
        deliverFileCommand = new DeliverFileCommand(dotOutputDir, renameExtension, overwriteExisting);

        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(dotOutputDir));
                // cd to the output dir
                one(client).cd(outputDir);
                // put the new file
                one(client).put(fileName + renameExtension, content);
                // return back to the starting dir
                one(client).cd(startingDir);
            }
        });

        BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord(fileName, content.length, "checksum", "checksumAlg", new Date(),
            content);
        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.BASE_FILE_TRANSFER_MAPPED_RECORD, record);
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 3));
        ExecutionOutput executionOutput = deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0],
            0));

        String tempFilename = (String) executionOutput.getResult();

        assertEquals("temp filename should be the filname with the rename extension", 
            dotOutputDir + FILE_SEPARATOR + fileName + renameExtension, tempFilename);
    }

    /**
     * Tests that the command successfully handles a non chunked data in the
     * form of a ready to deliver MappedRecord. Delivery to default directory.
     * 
     * 
     * @throws ResourceException
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     */
    public void testExecute_withMappedRecordToDefaultDirectory() 
        throws ResourceException, ClientCommandPwdException,
            ClientCommandCdException, ClientCommandPutException, ClientCommandLsException,
            URISyntaxException
    {

        deliverFileCommand = new DeliverFileCommand(startingDir, renameExtension, overwriteExisting);

        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);
                // put the new file
                one(client).put(fileName + renameExtension, content);
                // return back to the starting dir
                one(client).cd(startingDir);
            }
        });

        BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord(fileName, content.length, "checksum", "checksumAlg", new Date(),
            content);
        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.BASE_FILE_TRANSFER_MAPPED_RECORD, record);
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 3));
        ExecutionOutput executionOutput = deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0],
            0));

        String tempFilename = (String) executionOutput.getResult();

        assertEquals("temp filename should be the filname with the rename extension", 
            startingDir + FILE_SEPARATOR + fileName + renameExtension, tempFilename);
    }

    /**
     * 
     * Test the commit function
     * 
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     * @throws ResourceException
     * @throws ClientCommandRenameException
     * @throws ClientException 
     */
    public void testCommit() throws ClientCommandPwdException, ClientCommandCdException,
            ClientCommandPutException, ClientCommandLsException, URISyntaxException, ResourceException,
            ClientCommandRenameException, ClientException
    {

        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);
                //check if file to be delivered is already there
                one(client).ls(".");
                // put the new file
                one(client).put(fileName + renameExtension, content);
                // ls to check that it really is there
                one(client).ls(".");
                will(returnValue(resultingDirectoryListing));
                // return back to the starting dir
                one(client).cd(startingDir);
                // commit interaction
                one(client).pwd();
                will(returnValue(startingDir));
                one(client).ensureConnection();
                // cd to the output dir
                one(client).cd(outputDir);
                // look for the file to rename
                one(client).ls(".");
                one(client).ensureConnection();
                one(client).deleteRemoteFile(fileName);
                one(client).ensureConnection();
                one(client).rename(fileName + renameExtension, fileName);
                // look for the renamed file
                one(client).ls(".");
                // return back to the starting dir
                one(client).cd(startingDir);
                //disconnect
                one(client).disconnect();

            }
        });

        BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord(fileName, content.length, "checksum", "checksumAlg", new Date(),
            content);
        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.BASE_FILE_TRANSFER_MAPPED_RECORD, record);
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 5));

        // execute the command
        deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0], 0));

        // commit the command
        deliverFileCommand.commit();

    }

    /**
     * Test the rollback function
     * 
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     * @throws ResourceException
     * @throws ClientException
     */
    public void testRollback_PutFileAttemptedSuccessfully() throws ClientCommandPwdException,
            ClientCommandCdException, ClientCommandPutException, ClientCommandLsException,
            URISyntaxException, ResourceException, ClientException
    {

        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);
                //check if file to be delivered already exists
                one(client).ls(".");
                // put the new file
                one(client).put(fileName + renameExtension, content);
                // ls to check that it really is there
                one(client).ls(".");
                will(returnValue(resultingDirectoryListing));
                // return back to the starting dir
                one(client).cd(startingDir);

                // rollback interaction
                one(client).pwd();
                will(returnValue(startingDir));
                one(client).ensureConnection();
                // cd to the output dir
                one(client).cd(outputDir);
                // look for the file to rename
                one(client).ls(".");
                one(client).ensureConnection();
                one(client).deleteRemoteFile(fileName + renameExtension);
                // look for the renamed file
                one(client).ls(".");
                // return back to the starting dir
                one(client).cd(startingDir);
            }
        });

        BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord(fileName, content.length, "checksum", "checksumAlg", new Date(),
            content);
        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.BASE_FILE_TRANSFER_MAPPED_RECORD, record);
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 5));

        // execute the command
        deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0], 0));

        // rollback the command
        deliverFileCommand.rollback();
    }

    /**
     * Test the rollback function, when the put was attempted, but no file was
     * actually left on the target system
     * 
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     * @throws ResourceException
     */
    public void testRollback_PutFileAttemptedUnsuccessfully() throws ClientCommandPwdException,
            ClientCommandCdException, ClientCommandPutException, ClientCommandLsException,
            URISyntaxException, ResourceException
    {

        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);
                //check if file to be delivered already exists
                one(client).ls(".");
                // put the new file
                one(client).put(fileName + renameExtension, content);
                // ls to check that it really is there
                one(client).ls(".");
                will(returnValue(resultingDirectoryListing));
                // return back to the starting dir
                one(client).cd(startingDir);
                // rollback interaction
                one(client).pwd();
                will(returnValue(startingDir));
                one(client).ensureConnection();
                // cd to the output dir
                one(client).cd(outputDir);
                // look for the file to delete
                one(client).ls(".");
                // but of course in this case it wasn't there, so we stop here
            }
        });

        BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord(fileName, content.length, "checksum", "checksumAlg", new Date(),
            content);
        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.BASE_FILE_TRANSFER_MAPPED_RECORD, record);
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 5));

        // execute the command
        deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0], 0));

        // rollback the command
        deliverFileCommand.rollback();
    }

    /**
     * Test the rollback function, when the put had not even been attempted
     * Should not carry out any operations on the connection whatsoever
     * 
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ResourceException
     */
    public void testRollback_PutFileNotAttempted() throws ClientCommandPwdException, ClientCommandCdException,
            ResourceException
    {
        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);
                will(throwException(new ClientCommandCdException("could not change directory"))); //$NON-NLS-1$
                // and thats it for execute rollback interaction
                // nothing!!
            }
        });

        BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord(fileName, content.length, "checksum", "checksumAlg", new Date(),
            content);
        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.BASE_FILE_TRANSFER_MAPPED_RECORD, record);
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 5));

        // execute the command
        try
        {
            deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0], 0));
        }
        catch (ResourceException re)
        {
            // don't worry, its expected
            assertTrue(re.getCause() instanceof ClientCommandCdException);
        }

        // rollback the command
        deliverFileCommand.rollback();

    }

    /**
     * Tests that the command successfully handles a non chunked data in the
     * form of an input stream. Output directory is different from the default directory.
     * 
     * 
     * @throws ResourceException
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     * @throws ClientCommandMkdirException
     */
    public void testExecute_withInputStream() throws ResourceException, ClientCommandPwdException,
            ClientCommandCdException, ClientCommandPutException, ClientCommandLsException,
            URISyntaxException, ClientCommandMkdirException
    {
        final InputStream inputStream = new ByteArrayInputStream(content);

        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);
                // put the new file
                one(client).putWithOutputStream(nestedFile.getPath() + renameExtension, inputStream);
                // return back to the starting dir
                one(client).cd(startingDir);
            }
        });

        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.FILE_INPUT_STREAM, inputStream);
        context.put(ExecutionContext.RELATIVE_FILE_PATH_PARAM, nestedFile.getPath());
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 3));
        ExecutionOutput executionOutput = deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0],
            0));

        String tempFilename = (String) executionOutput.getResult();

        assertEquals("temp filename should be the filname with the rename extension", outputDir + FILE_SEPARATOR + nestedFile.getPath() + renameExtension, //$NON-NLS-1$
            tempFilename);
    }

    /**
     * Tests that the command successfully handles a non chunked data in the
     * form of an input stream. Output directory is the same as the default directory.
     * 
     * 
     * @throws ResourceException
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     * @throws ClientCommandMkdirException
     */
    public void testExecute_withInputStream_toDefaultDirectory() 
        throws ResourceException, ClientCommandPwdException,
            ClientCommandCdException, ClientCommandPutException, ClientCommandLsException,
            URISyntaxException, ClientCommandMkdirException
    {
        final InputStream inputStream = new ByteArrayInputStream(content);
        final String sameOutputDir = "startingDir";
        deliverFileCommand = new DeliverFileCommand(sameOutputDir, renameExtension, overwriteExisting);

        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(sameOutputDir));
                // put the new file
                one(client).putWithOutputStream(nestedFile.getPath() + renameExtension, inputStream);
            }
        });

        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.FILE_INPUT_STREAM, inputStream);
        context.put(ExecutionContext.RELATIVE_FILE_PATH_PARAM, nestedFile.getPath());
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 3));
        ExecutionOutput executionOutput = deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0],
            0));

        String tempFilename = (String) executionOutput.getResult();

        assertEquals("temp filename should be the filname with the rename extension", sameOutputDir + FILE_SEPARATOR + nestedFile.getPath() + renameExtension, //$NON-NLS-1$
            tempFilename);
    }

    /**
     * Tests that the command successfully handles a non chunked data in the
     * form of an input stream. Output directory is dot.
     * 
     * 
     * @throws ResourceException
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     * @throws ClientCommandMkdirException
     */
    public void testExecute_withInputStream_toDefaultDotDirectory() 
        throws ResourceException, ClientCommandPwdException,
            ClientCommandCdException, ClientCommandPutException, ClientCommandLsException,
            URISyntaxException, ClientCommandMkdirException
    {
        final InputStream inputStream = new ByteArrayInputStream(content);
        final String dotOutputDir = ".";
        deliverFileCommand = new DeliverFileCommand(dotOutputDir, renameExtension, overwriteExisting);

        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(dotOutputDir));
                // put the new file
                one(client).putWithOutputStream(nestedFile.getPath() + renameExtension, inputStream);
            }
        });

        ExecutionContext context = new ExecutionContext();
        context.put(ExecutionContext.FILE_INPUT_STREAM, inputStream);
        context.put(ExecutionContext.RELATIVE_FILE_PATH_PARAM, nestedFile.getPath());
        deliverFileCommand.setExecutionContext(context);

        deliverFileCommand.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(deliverFileCommand, 3));
        ExecutionOutput executionOutput = deliverFileCommand.execute(client, new XidImpl(new byte[0], new byte[0],
            0));

        String tempFilename = (String) executionOutput.getResult();

        assertEquals("temp filename should be the filname with the rename extension", dotOutputDir + FILE_SEPARATOR + nestedFile.getPath() + renameExtension, //$NON-NLS-1$
            tempFilename);
    }

}
