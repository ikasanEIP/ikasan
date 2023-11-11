/*
 * $Id:$
 * $URL:$
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

import org.ikasan.connector.base.command.AbstractTransactionalResourceCommand;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.basefiletransfer.net.*;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.resource.ResourceException;
import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ikasan Development Team
 */
class RetrieveFileCommandTest
{

    /**
     * Testing the constructor with moveOnSuccess, renameOnSuccess, and destructive, all set to false.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    @Test
    void testConstructor_FalseAllFlags()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extension = null;
        boolean move = false;
        String newPath = null;
        boolean destructive = false;
        IllegalArgumentException exception = null;
        RetrieveFileCommand command = null;
        try
        {
            command = new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNull(exception, "When moveOnSuccess, renameOnSuccess, and destructive are all false, no exception should be thrown."); //$NON-NLS-1$
        assertEquals(command.isDestructive(), destructive, "The destructive flag is expected to be false.");//$NON-NLS-1$
        assertEquals(command.isRenameOnSuccess(), rename, "The renameOnSuccess flag is expected to be false.");//$NON-NLS-1$
        assertEquals(command.isMoveOnSuccess(), move, "The moveOnSuccess flag is expected to be false.");//$NON-NLS-1$
    }

    /**
     * Testing the constructor with only renameOnSuccess set to true.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    @Test
    void testConstructor_TrueRename()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extension = ".renamed"; //$NON-NLS-1$
        boolean move = false;
        String newPath = null;
        boolean destructive = false;
        IllegalArgumentException exception = null;
        RetrieveFileCommand command = null;
        try
        {
            command = new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNull(exception, "When only renameOnSuccess is set to true, no exception should be thrown."); //$NON-NLS-1$
        assertEquals(command.isDestructive(), destructive, "The destructive flag is expected to be false.");//$NON-NLS-1$
        assertEquals(command.isRenameOnSuccess(), rename, "The renameOnSuccess flag is expected to be true.");//$NON-NLS-1$
        assertEquals(command.isMoveOnSuccess(), move, "The moveOnSuccess flag is expected to be false.");//$NON-NLS-1$
    }

    /**
     * Testing the constructor with only destructive set to true.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    @Test
    void testConstructor_TrueDestructive()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extension = null;
        boolean move = false;
        String newPath = null;
        boolean destructive = true;
        IllegalArgumentException exception = null;
        RetrieveFileCommand command = null;
        try
        {
            command = new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNull(exception, "When only destructive is set to true, no exception should be thrown."); //$NON-NLS-1$
        assertEquals(command.isDestructive(), destructive, "The destructive flag is expected to be true.");//$NON-NLS-1$
        assertEquals(command.isRenameOnSuccess(), rename, "The renameOnSuccess flag is expected to be false.");//$NON-NLS-1$
        assertEquals(command.isMoveOnSuccess(), move, "The moveOnSuccess flag is expected to be false.");//$NON-NLS-1$
    }

    /**
     * Testing the constructor with only moveOnSuccess set to true.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    @Test
    void testConstructor_TrueMove()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extension = null;
        boolean move = true;
        String newPath = "/archDir"; //$NON-NLS-1$
        boolean destructive = false;
        IllegalArgumentException exception = null;
        RetrieveFileCommand command = null;
        try
        {
            command = new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNull(exception, "When only moveOnSuccess is set to true, no exception should be thrown."); //$NON-NLS-1$
        assertEquals(command.isDestructive(), destructive, "The destructive flag is expected to be false.");//$NON-NLS-1$
        assertEquals(command.isRenameOnSuccess(), rename, "The renameOnSuccess flag is expected to be false.");//$NON-NLS-1$
        assertEquals(command.isMoveOnSuccess(), move, "The moveOnSuccess flag is expected to be true.");//$NON-NLS-1$
    }

    /**
     * Testing the constructor with moveOnSuccess, renameOnSuccess, and destructive all set to true.
     * This path is expected to not throw any exceptions and successfully create a RetrieveFileCommand object
     */
    @Test
    void testConstructor_TrueRenameAndMoveAndDestructive()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extension = null;
        boolean move = true;
        String newPath = null;
        boolean destructive = true;
        IllegalArgumentException exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertNotNull(
                exception,
                "When moveOnSuccess, renameOnSuccess, and destructive are all true,an IllegalArgumentException should be thrown."); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with renameOnSuccess true but a null renameOnSuccessextension
     * Expected to throw an IllegalArgumentException.
     */
    @Test
    void testConstructor_RenameNullextension()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extension = null;
        boolean move = false;
        String newPath = null;
        boolean destructive = false;
        IllegalArgumentException exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertEquals("renameExtension has not been configured.", exception.getMessage()); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with moveOnSuccess true but a null moveOnSuccessNewPath
     * Expected to throw an IllegalArgumentException.
     */
    @Test
    void testConstructor_MoveNullNewPath()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extension = null;
        boolean move = true;
        String newPath = null;
        boolean destructive = false;
        Exception exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertEquals("moveOnSuccessNewPath has not been configured.", exception.getMessage()); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with both moveOnSuccess and renameOnSuccess set to true.
     * Expected to throw an IllegalArgumentException
     */
    @Test
    void testConstructor_TrueMoveAndRename()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extension = null;
        boolean move = true;
        String newPath = null;
        boolean destructive = false;
        Exception exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertEquals("Moving the file and renaming it are mutually exclusive.", exception.getMessage()); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with both moveOnSuccess and destructive set to true.
     * Expected to throw an IllegalArgumentExcepton
     */
    @Test
    void testConstructor_TrueMoveAndDestrucive()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = false;
        String extension = null;
        boolean move = true;
        String newPath = null;
        boolean destructive = true;
        Exception exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertEquals("Moving the file and Get Destructive are mutually exclusive.", exception.getMessage()); //$NON-NLS-1$
    }

    /**
     * Testing the constructor with both renameOnSuccess and destructive set to true.
     * Expected to throw an IllegalArgumentException.
     */
    @Test
    void testConstructor_TrueRenameAndDestrucive()
    {
        Mockery interfaceMockery = new Mockery();
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        boolean rename = true;
        String extension = null;
        boolean move = false;
        String newPath = null;
        boolean destructive = true;
        Exception exception = null;
        try
        {
            new RetrieveFileCommand(dao,rename,extension,move,newPath,destructive);
            fail("Caught IllegalArgumentException."); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertEquals("RenameOnSuccess and Get Destructive are mutually exclusive.", exception.getMessage()); //$NON-NLS-1$
    }

    /**
     * Tests just the execute method of the RetrieveFileCommand
     *
     * @throws ResourceException -
     * @throws ClientCommandCdException -
     * @throws ClientCommandGetException -
     * @throws URISyntaxException -
     */
    @Test
    void testExecute() throws ResourceException, ClientCommandCdException,
        ClientCommandGetException, URISyntaxException
    {
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
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
                oneOf(client).ensureConnection();
                oneOf(client).cd(sourceDir);
                oneOf(client).get(entry);
                will(returnValue(file));
            }
        });
        // mock the dao
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                oneOf(dao).persistClientListEntry(entry);
            }
        });
        // create the command
        RetrieveFileCommand command = new RetrieveFileCommand(dao,
             false, null, false, null, false);
        assertEquals(AbstractTransactionalResourceCommand.INITIALISED_STATE.getName(), command
                .getState(), "command state should be initialised");
        //create the ExecutionContext
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.RETRIEVABLE_FILE_PARAM, entry);

        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 3));
        command.setExecutionContext(executionContext);

        // execute the command
        ExecutionOutput output = command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
        BaseFileTransferMappedRecord result = (BaseFileTransferMappedRecord) output.getResult();
        assertNotNull(result, "result should not be null"); //$NON-NLS-1$
        assertEquals(file, result, "result should be file defined above"); //$NON-NLS-1$
        assertEquals(AbstractTransactionalResourceCommand.EXECUTED_STATE.getName(), command
                .getState(), "command state should be executed");
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
    @Test
    void testCommit_Rename() throws
        ResourceException, ClientCommandCdException,
        ClientCommandGetException, URISyntaxException,
        ClientCommandRenameException
    {
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
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
                oneOf(client).ensureConnection();
                oneOf(client).cd(sourceDir);
                oneOf(client).get(entry);
                will(returnValue(file));
                oneOf(client).ensureConnection();
                oneOf(client).rename(platformFriendly(srcPath), platformFriendly(renamePath));
                //oneOf(client).rename(srcPath, movePath);
            }
        });
        // mock the dao
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                oneOf(dao).persistClientListEntry(entry);
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

        assertEquals(platformFriendly(srcPath), command.getSourcePath(), "source path stored in command should match that from the list entry"); //$NON-NLS-1$

        assertEquals(AbstractTransactionalResourceCommand.EXECUTED_STATE.getName(), command
                        .getState(), "command state should be executed");
        // commit the command
        command.commit();

        assertEquals(AbstractTransactionalResourceCommand.COMPLETED_COMMITTING.getName(), command
                .getState(), "command state should be committed");
    }

    /**
     * Fix paths to allow tests to run on Windows or UNIX
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
    @Test
    void testCommit_Move() throws
        ResourceException, ClientCommandCdException,
        ClientCommandGetException, URISyntaxException,
        ClientCommandRenameException
    {
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
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
                oneOf(client).ensureConnection();
                oneOf(client).cd(sourceDir);
                oneOf(client).get(entry);
                will(returnValue(file));
                oneOf(client).ensureConnection();
                oneOf(client).rename(platformFriendly(srcPath), platformFriendly(movePath));
            }
        });
        // mock the dao
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                oneOf(dao).persistClientListEntry(entry);
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

        assertEquals(platformFriendly(srcPath), command.getSourcePath(), "source path stored in command should match that from the list entry"); //$NON-NLS-1$

        assertEquals(AbstractTransactionalResourceCommand.EXECUTED_STATE.getName(), command.getState(), "command state should be executed");
        // commit the command
        command.commit();

        assertEquals(AbstractTransactionalResourceCommand.COMPLETED_COMMITTING.getName(), command.getState(), "command state should be committed");
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
    @Test
    void testRollback() throws
        ResourceException, ClientCommandCdException,
        ClientCommandGetException, URISyntaxException,
        ClientCommandRenameException
    {
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
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
                oneOf(client).ensureConnection();
                oneOf(client).cd(sourceDir);
                oneOf(client).get(entry);
                will(returnValue(file));
                oneOf(client).ensureConnection();
                oneOf(client).rename(srcPath, renamePath);
                oneOf(client).ensureConnection();
                oneOf(client).rename(renamePath, srcPath);
            }
        });
        // mock the dao
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                oneOf(dao).persistClientListEntry(entry);
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

        assertEquals(platformFriendly(srcPath), command.getSourcePath(), "source path stored in command should match that from the list entry"); //$NON-NLS-1$

        command.rollback();

        assertEquals(AbstractTransactionalResourceCommand.ROLLED_BACK_STATE.getName(),
            command.getState(),
            "command state should be rolledback");
    }

    @Test
    void testExpandExtension_yyyyMMdd()
    {
        RetrieveFileCommand retrieveFileCommand = new RetrieveFileCommand(null, true, ".done./yyyyMMdd/", false, null, false);
        DateTimeUtils.setCurrentMillisFixed(1450177200000L);
        String extension = retrieveFileCommand.expandRenameExtension();
        assertEquals(".done.20151215", extension);
    }

    @Test
    void testExpandExtension_yyyyMMdd_in_bst()
    {
        RetrieveFileCommand retrieveFileCommand = new RetrieveFileCommand(null, true, ".done./yyyyMMdd/", false, null, false);
        DateTimeUtils.setCurrentMillisFixed(1439337600000L);
        String extension = retrieveFileCommand.expandRenameExtension();
        assertEquals(".done.20150812", extension);
    }

    @Test
    void testExpandExtension_yyyyMMdd_changeTo_yyyyMMddHHmmss()
    {
        RetrieveFileCommand retrieveFileCommand = new RetrieveFileCommand(null, true, ".done./yyyyMMdd/", false, null, false);
        DateTimeUtils.setCurrentMillisFixed(1450177200000L);
        String extension = retrieveFileCommand.expandRenameExtension();
        assertEquals(".done.20151215", extension);
        retrieveFileCommand.setRenameExtension(".done./yyyyMMddHHmmss/");
        String extension2 = retrieveFileCommand.expandRenameExtension();
        assertEquals(".done.20151215110000", extension2);

    }

    @Test
    void testExpandExtension_no_timestamp()
    {
        RetrieveFileCommand retrieveFileCommand = new RetrieveFileCommand(null, true, ".done", false, null, false);
        String extension = retrieveFileCommand.expandRenameExtension();
        assertEquals(".done", extension);
    }

    @AfterEach
    void after_test()
    {
        // be nice to other tests
        DateTimeUtils.setCurrentMillisSystem();
    }

}
