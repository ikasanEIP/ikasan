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

import java.io.File;
import java.net.URISyntaxException;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import org.ikasan.connector.base.command.AbstractTransactionalResourceCommand;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
import org.ikasan.connector.basefiletransfer.net.ClientCommandCdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandGetException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandRenameException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.ikasan.connector.basefiletransfer.outbound.command.RetrieveFileCommand;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;

/**
 * @author Ikasan Development Team
 */
public class RetrieveFileCommandTest extends TestCase
{
    /**
     * Testing the constructor with moveOnSuccess, renameOnSuccess, and destructive, all set to false.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    @SuppressWarnings("null")
    public void testConstructor_FalseAllFlags()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extention = null;
        boolean move = false;
        String newPath = null;
        boolean destructive = false;
        IllegalArgumentException exception = null;
        RetrieveFileCommand command = null;
        try
        {
            command = new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNull("When moveOnSuccess, renameOnSuccess, and destructive are all false, no exception should be thrown.", exception); //$NON-NLS-1$
        assertEquals("The destructive flag is expected to be false.", command.isDestructive(), destructive);//$NON-NLS-1$
        assertEquals("The renameOnSuccess flag is expected to be false.", command.isRenameOnSuccess(), rename);//$NON-NLS-1$
        assertEquals("The moveOnSuccess flag is expected to be false.", command.isMoveOnSuccess(), move);//$NON-NLS-1$
    }

    /**
     * Testing the constructor with only renameOnSuccess set to true.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    @SuppressWarnings("null")
    public void testConstructor_TrueRename()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extention = ".renamed"; //$NON-NLS-1$
        boolean move = false;
        String newPath = null;
        boolean destructive = false;
        IllegalArgumentException exception = null;
        RetrieveFileCommand command = null;
        try
        {
            command = new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNull("When only renameOnSuccess is set to true, no exception should be thrown.", exception); //$NON-NLS-1$
        assertEquals("The destructive flag is expected to be false.", command.isDestructive(), destructive);//$NON-NLS-1$
        assertEquals("The renameOnSuccess flag is expected to be true.", command.isRenameOnSuccess(), rename);//$NON-NLS-1$
        assertEquals("The moveOnSuccess flag is expected to be false.", command.isMoveOnSuccess(), move);//$NON-NLS-1$
    }

    /**
     * Testing the constructor with only destructive set to true.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    @SuppressWarnings("null")
    public void testConstructor_TrueDestructive()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extention = null;
        boolean move = false;
        String newPath = null;
        boolean destructive = true;
        IllegalArgumentException exception = null;
        RetrieveFileCommand command = null;
        try
        {
            command = new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNull("When only destructive is set to true, no exception should be thrown.", exception); //$NON-NLS-1$
        assertEquals("The destructive flag is expected to be true.", command.isDestructive(), destructive);//$NON-NLS-1$
        assertEquals("The renameOnSuccess flag is expected to be false.", command.isRenameOnSuccess(), rename);//$NON-NLS-1$
        assertEquals("The moveOnSuccess flag is expected to be false.", command.isMoveOnSuccess(), move);//$NON-NLS-1$
    }

    /**
     * Testing the constructor with only moveOnSuccess set to true.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    @SuppressWarnings("null")
    public void testConstructor_TrueMove()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extention = null;
        boolean move = true;
        String newPath = "/archDir"; //$NON-NLS-1$
        boolean destructive = false;
        IllegalArgumentException exception = null;
        RetrieveFileCommand command = null;
        try
        {
            command = new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNull("When only moveOnSuccess is set to true, no exception should be thrown.", exception); //$NON-NLS-1$
        assertEquals("The destructive flag is expected to be false.", command.isDestructive(), destructive);//$NON-NLS-1$
        assertEquals("The renameOnSuccess flag is expected to be false.", command.isRenameOnSuccess(), rename);//$NON-NLS-1$
        assertEquals("The moveOnSuccess flag is expected to be true.", command.isMoveOnSuccess(), move);//$NON-NLS-1$
    }

    /**
     * Testing the constructor with moveOnSuccess, renameOnSuccess, and destructive all set to true.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    public void testConstructor_TrueRenameAndMoveAndDestructive()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extention = null;
        boolean move = true;
        String newPath = null;
        boolean destructive = true;
        IllegalArgumentException exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNotNull("When moveOnSuccess, renameOnSuccess, and destructive are all true,an IllegalArgumentException should be thrown.", exception); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with renameOnSuccess true but a null renameOnSuccessExtention
     * Expected to throw an IllegalArgumentException.
     */
    @SuppressWarnings("null")
    public void testConstructor_RenameNullExtention()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extention = null;
        boolean move = false;
        String newPath = null;
        boolean destructive = false;
        IllegalArgumentException exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals("renameExtension has not been configured.")); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with moveOnSuccess true but a null moveOnSuccessNewPath
     * Expected to throw an IllegalArgumentException.
     */
    @SuppressWarnings("null")
    public void testConstructor_MoveNullNewPath()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extention = null;
        boolean move = true;
        String newPath = null;
        boolean destructive = false;
        Exception exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals("moveOnSuccessNewPath has not been configured.")); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with both moveOnSuccess and renameOnSuccess set to true.
     * Expected to throw an IllegalArgumentException
     */
    @SuppressWarnings("null")
    public void testConstructor_TrueMoveAndRename()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extention = null;
        boolean move = true;
        String newPath = null;
        boolean destructive = false;
        Exception exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals("Moving the file and renaming it are mutually exclusive.")); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with both moveOnSuccess and destructive set to true.
     * Expected to throw an IllegalArgumentExcepton
     */
    @SuppressWarnings("null")
    public void testConstructor_TrueMoveAndDestrucive()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extention = null;
        boolean move = true;
        String newPath = null;
        boolean destructive = true;
        Exception exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals("Moving the file and Get Destructive are mutually exclusive.")); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with both renameOnSuccess and destructive set to true.
     * Expected to throw an IllegalArgumentException.
     */
    @SuppressWarnings("null")
    public void testConstructor_TrueRenameAndDestrucive()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extention = null;
        boolean move = false;
        String newPath = null;
        boolean destructive = true;
        Exception exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extention,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertTrue(exception.getMessage().equals("RenameOnSuccess and Get Destructive are mutually exclusive.")); //$NON-NLS-1$
    }

    /**
     * Tests just the execute method of the RetrieveFileCommand
     * 
     * @throws ResourceException - 
     * @throws ClientCommandCdException -
     * @throws ClientCommandGetException -
     * @throws URISyntaxException -
     */
    public void testExecute() throws ResourceException, ClientCommandCdException,
            ClientCommandGetException, URISyntaxException
    {
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        Mockery interfaceMockery = new Mockery();
        // mock the client
        final FileTransferClient client = context.mock(FileTransferClient.class);
        final String sourceDir = "srcDir";//$NON-NLS-1$
        final String fileName = "fileName";//$NON-NLS-1$
        final ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry(sourceDir + "/" + fileName);//$NON-NLS-1$
        final BaseFileTransferMappedRecord file = new BaseFileTransferMappedRecord();
 
        context.checking(new Expectations()
        {
            {
                one(client).ensureConnection();
                one(client).cd(sourceDir);
                one(client).get(entry);
                will(returnValue(file));
            }
        });
        // mock the dao
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                one(dao).persistClientListEntry(entry);
            }
        });
        // create the command
        RetrieveFileCommand command = new RetrieveFileCommand(dao,
             false, null, false, null, false);
        assertEquals("command state should be initialised", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.INITIALISED_STATE.getName(), command
                .getState());
        //create the ExecutionContext
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.RETRIEVABLE_FILE_PARAM, entry);
        
        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 3));
        command.setExecutionContext(executionContext);
        
        // execute the command
        ExecutionOutput output = command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
        BaseFileTransferMappedRecord result = (BaseFileTransferMappedRecord) output.getResult();
        assertNotNull("result should not be null", result); //$NON-NLS-1$
        assertEquals("result should be file defined above", file, result); //$NON-NLS-1$
        assertEquals("command state should be executed", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.EXECUTED_STATE.getName(), command
                .getState());
    }

    /**
     * Tests that the commit function works correctly with renaming the file.
     * 
     * @throws ResourceException -
     * @throws ClientCommandCdException -
     * @throws ClientCommandGetException -
     * @throws URISyntaxException -
     * @throws ClientCommandRenameException -
     */
    public void testCommit_Rename() throws 
            ResourceException, ClientCommandCdException,
            ClientCommandGetException, URISyntaxException,
            ClientCommandRenameException
    {
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        Mockery interfaceMockery = new Mockery();
        // mock the client
        final FileTransferClient client = context.mock(FileTransferClient.class);
        final String sourceDir = "srcDir";//$NON-NLS-1$
        final String fileName = "fileName";//$NON-NLS-1$
        final String archiveDir = "archDir";//$NON-NLS-1$
        final ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry(sourceDir + "/" + fileName);//$NON-NLS-1$
        final BaseFileTransferMappedRecord file = new BaseFileTransferMappedRecord();
        final String renameExtension = ".ren";//$NON-NLS-1$
        final String srcPath = entry.getUri().getPath();
        final String renamePath = srcPath + renameExtension;
        final String movePath = archiveDir + File.separator + fileName;//$NON-NLS-1$
        

        context.checking(new Expectations()
        {
            {
                one(client).ensureConnection();
                one(client).cd(sourceDir);
                one(client).get(entry);
                will(returnValue(file));
                one(client).ensureConnection();
                one(client).rename(platformFriendly(srcPath), platformFriendly(renamePath));
                //one(client).rename(srcPath, movePath);
            }
        });
        // mock the dao
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                one(dao).persistClientListEntry(entry);
            }
        });
        // create the command
        RetrieveFileCommand command = new RetrieveFileCommand(dao,
             true, renameExtension, false, movePath, false);
        //create the ExecutionContext
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.RETRIEVABLE_FILE_PARAM, entry);
        command.setExecutionContext(executionContext);
        
        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 5));

        // execute the command
        command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
       
        assertEquals("source path stored in command should match that from the list entry", platformFriendly(srcPath), command.getSourcePath()); //$NON-NLS-1$

        assertEquals("command state should be executed", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.EXECUTED_STATE.getName(), command
                .getState());
        // commit the command
        command.commit();

        assertEquals("command state should be committed", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.COMPLETED_COMMITTING.getName(), command
                .getState());
    }

    /**
     * Fix paths to allow tests to run on Windows or UNIX
     * 
     * @param path
     * @return
     */
    private String platformFriendly(String path) {
		return new File(path).getPath();
	}

	/**
     * Tests that the commit function works correctly with moving the file.
     * 
     * @throws ResourceException -
     * @throws ClientCommandCdException-
     * @throws ClientCommandGetException -
     * @throws URISyntaxException -
     * @throws ClientCommandRenameException -
     */
    public void testCommit_Move() throws 
        ResourceException, ClientCommandCdException,
        ClientCommandGetException, URISyntaxException,
        ClientCommandRenameException
    {
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        Mockery interfaceMockery = new Mockery();
        //  mock the client
        final FileTransferClient client = context.mock(FileTransferClient.class);
        final String sourceDir = "srcDir";//$NON-NLS-1$
        final String fileName = "fileName";//$NON-NLS-1$
        final String archiveDir = "archDir";//$NON-NLS-1$
        final ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry(sourceDir + "/" + fileName);//$NON-NLS-1$
        final BaseFileTransferMappedRecord file = new BaseFileTransferMappedRecord();
        final String renameExtension = ".ren";//$NON-NLS-1$
        final String srcPath = entry.getUri().getPath();
        final String movePath = archiveDir + File.separator + fileName;//$NON-NLS-1$

        context.checking(new Expectations()
        {
            {
                one(client).ensureConnection();
                one(client).cd(sourceDir);
                one(client).get(entry);
                will(returnValue(file));
                one(client).ensureConnection();
                one(client).rename(platformFriendly(srcPath), platformFriendly(movePath));
            }
        });
        // mock the dao
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                one(dao).persistClientListEntry(entry);
            }
        });
        // create the command
        RetrieveFileCommand command = new RetrieveFileCommand(dao,
            false, renameExtension, true, movePath, false);
        //create the ExecutionContext
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.RETRIEVABLE_FILE_PARAM, entry);
        command.setExecutionContext(executionContext);
        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 5));

        // execute the command
        command.execute(client, new XidImpl(new byte[0], new byte[0], 0));

        assertEquals("source path stored in command should match that from the list entry", platformFriendly(srcPath), command.getSourcePath()); //$NON-NLS-1$
        
        assertEquals("command state should be executed", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.EXECUTED_STATE.getName(), command.getState());
        // commit the command
        command.commit();

        assertEquals("command state should be committed", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.COMPLETED_COMMITTING.getName(), command.getState());
    }

    /**
     * Tests that the rollback function works correctly
     * 
     * @throws ResourceException -
     * @throws ClientCommandCdException -
     * @throws ClientCommandGetException -
     * @throws URISyntaxException -
     * @throws ClientCommandRenameException -
     */
    public void testRollback() throws 
            ResourceException, ClientCommandCdException,
            ClientCommandGetException, URISyntaxException,
            ClientCommandRenameException
    {
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        Mockery interfaceMockery = new Mockery();
        // mock the client
        final FileTransferClient client = context.mock(FileTransferClient.class);
        final String sourceDir = "srcDir";//$NON-NLS-1$
        final String fileName = "fileName";//$NON-NLS-1$
        final ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry(sourceDir + "/" + fileName);//$NON-NLS-1$
        final BaseFileTransferMappedRecord file = new BaseFileTransferMappedRecord();
        final String renameExtension = ".ren";//$NON-NLS-1$
        final String srcPath = entry.getUri().getPath();
        final String renamePath = srcPath + renameExtension;

        context.checking(new Expectations()
        {
            {
                one(client).ensureConnection();
                one(client).cd(sourceDir);
                one(client).get(entry);
                will(returnValue(file));
                one(client).ensureConnection();
                one(client).rename(srcPath, renamePath);
                one(client).ensureConnection();
                one(client).rename(renamePath, srcPath);
            }
        });
        // mock the dao
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                one(dao).persistClientListEntry(entry);
            }
        });
        // create the command
        RetrieveFileCommand command = new RetrieveFileCommand(dao,
             true, renameExtension,false, null,  false);
        //create the ExecutionContext
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.RETRIEVABLE_FILE_PARAM, entry);
        command.setExecutionContext(executionContext);

        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 5));
        
        // execute the command
        command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
        
        assertEquals("source path stored in command should match that from the list entry", platformFriendly(srcPath), command.getSourcePath()); //$NON-NLS-1$
        
        command.rollback();
        
        assertEquals("command state should be rolledback", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.ROLLED_BACK_STATE.getName(),
            command.getState());
    }
}
