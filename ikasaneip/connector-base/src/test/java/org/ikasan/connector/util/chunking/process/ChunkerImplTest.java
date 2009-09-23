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
package org.ikasan.connector.util.chunking.process;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;

import org.ikasan.connector.util.chunking.model.FileChunk;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;
import org.ikasan.connector.util.chunking.model.dao.ChunkLoadException;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.connector.util.chunking.provider.ChunkableDataProvider;
import org.ikasan.connector.util.chunking.provider.ChunkableDataProviderAccessException;
import org.ikasan.connector.util.chunking.provider.ChunkableDataSourceException;

/**
 * Test class for ChunkerImpl
 * 
 * @author Ikasan Development Team
 * 
 */
public class ChunkerImplTest extends TestCase {

	/**
	 * no of bytes in 1MB
	 */
	private static final int ONE_MB = 1024 * 1024;

	/**
	 * fileName
	 */
	String fileName = "fileName";
	
	/**
	 * file size
	 */
	long fileSize = (ONE_MB * 2) + 1;
	
	/**
	 * handles
	 */
	private final  List<FileConstituentHandle> handles = new ArrayList<FileConstituentHandle>();
	
	/**
	 * no of chunks
	 */
	long noOfChunks = 3;
	
	/**
	 * remote dir
	 */
	String remoteDir = "dir";
   
	/**
	 * Constructor
	 */
	public ChunkerImplTest(){

	    FileChunk fileChunk = new FileChunk(null, 1l, new byte[0]);
        handles.add(fileChunk);
	}

	/**
     * Creates initial expectations for calls to the ChunkableDataProvider
     * 
     * @param interfaceMockery
     * @param chunkableDataProvider
     * @param sequence
     * @throws ChunkableDataProviderAccessException
     * @throws ChunkableDataSourceException
     */
    private void expectDataProviderInitialisation(Mockery interfaceMockery,
            final ChunkableDataProvider chunkableDataProvider,
            final Sequence sequence) throws ChunkableDataProviderAccessException, ChunkableDataSourceException
    {
        
        interfaceMockery.checking(new Expectations(){
            {  
                one(chunkableDataProvider).connect(); inSequence(sequence); //connect to the data provider
                one(chunkableDataProvider).getFileSize(remoteDir, fileName); inSequence(sequence); //determine file size up front
                will(returnValue(fileSize));
           }
       }); 
    }


	/**
	 * test the chunkFile method
	 * @throws ChunkableDataProviderAccessException 
	 */
	public void testChunkFile_CannotConnect() throws ChunkableDataProviderAccessException {
	      Mockery interfaceMockery = new Mockery();
	        final Sequence sequence = interfaceMockery.sequence("sequence");
	        
	        final ChunkableDataProvider chunkableDataProvider = interfaceMockery.mock(ChunkableDataProvider.class);

	        
	      final ChunkableDataProviderAccessException chunkableDataProviderAccessException = new ChunkableDataProviderAccessException(
          "an exception", null);
	        
	        interfaceMockery.checking(new Expectations(){
	            {  
	                one(chunkableDataProvider).connect(); inSequence(sequence); //connect to the data provider
	                will(throwException(chunkableDataProviderAccessException));
	           }
	       }); 
	    
		ChunkerImpl chunkerImpl = new ChunkerImpl(null,chunkableDataProvider,Chunker.MODE_INPUT_STREAM);

		try {
			chunkerImpl.chunkFile(null, null, ONE_MB);
			fail("exception should have been thrown");
		} catch (ChunkException e) {
			// check that the underlying exception is the access exception
			assertEquals(
					"Cause of exception should have been the ChunkableDataProviderAccessException thrown by the provider",
					chunkableDataProviderAccessException, e.getCause());
		}
	}

    /**
	 * test the chunkFile method
	 * @throws ChunkableDataSourceException 
	 * @throws ChunkableDataProviderAccessException 
	 */
	public void testChunkFile_CannotDisconnect() throws ChunkableDataProviderAccessException, ChunkableDataSourceException {
	    
	        Mockery interfaceMockery = new Mockery();
	        final Sequence sequence = interfaceMockery.sequence("sequence");
	        final ChunkableDataProvider chunkableDataProvider = interfaceMockery.mock(ChunkableDataProvider.class);
	        final FileChunkDao fileChunkDao = interfaceMockery.mock(FileChunkDao.class);

            final ChunkableDataProviderAccessException chunkableDataProviderAccessException = new ChunkableDataProviderAccessException("an exception", null);
	        
            
            
            //create expectations
	        expectDataProviderInitialisation(interfaceMockery, chunkableDataProvider, sequence);
	        expectNoExistingChunksNewHeader(interfaceMockery, sequence, fileChunkDao);
	        expectSourceChunks_InputStream(interfaceMockery, sequence, chunkableDataProvider);
	        expectSaveHeader(interfaceMockery, sequence, fileChunkDao);
	        
	        expectDisconnectUnsuccessful(interfaceMockery, sequence, chunkableDataProvider, chunkableDataProviderAccessException);
	        

	    //execute the method
		ChunkerImpl chunkerImpl = new ChunkerImpl(fileChunkDao, chunkableDataProvider,
				Chunker.MODE_INPUT_STREAM, null);

		try {
			chunkerImpl.chunkFile(remoteDir, fileName, ONE_MB);
			fail("an exception should have been thrown");
		} catch (ChunkException e) {
			// check that the underlying exception is the access exception
			assertEquals(
					"Cause of exception should have been the ChunkableDataProviderAccessException thrown by the provider",
					chunkableDataProviderAccessException, e.getCause());
		}
	}

	/**
	 * test the chunkFile method
	 * @throws ChunkableDataSourceException 
	 * @throws ChunkableDataProviderAccessException 
	 * @throws ChunkLoadException 
	 */
	public void testChunkFile_ExceptionDuringReplaying() throws ChunkableDataProviderAccessException, ChunkableDataSourceException, ChunkLoadException {
	    
        Mockery interfaceMockery = new Mockery();
        final Sequence sequence = interfaceMockery.sequence("sequence");
        final ChunkableDataProvider chunkableDataProvider = interfaceMockery.mock(ChunkableDataProvider.class);
        final FileChunkDao fileChunkDao = interfaceMockery.mock(FileChunkDao.class);

        final ChunkLoadException chunkLoadException = new ChunkLoadException("an exception");
 
        //create expectations
        expectDataProviderInitialisation(interfaceMockery, chunkableDataProvider, sequence);
        expectExistingChunksLoadThrowsException(interfaceMockery, sequence, fileChunkDao, chunkLoadException);

      //execute the method
		ChunkerImpl chunkerImpl = new ChunkerImpl(fileChunkDao, chunkableDataProvider, Chunker.MODE_INPUT_STREAM);

		try {
			chunkerImpl.chunkFile(remoteDir, fileName, ONE_MB);
			fail("exception should have been thrown");
		} catch (ChunkException e) {
			// check that the underlying exception is the load exception
			assertEquals(
					"Cause of exception should have been the chunkLoadException thrown by the provider",
					chunkLoadException, e.getCause());
		}
	}

	/**
	 * test the chunkFile method
	 * @throws ChunkableDataSourceException 
	 * @throws ChunkableDataProviderAccessException 
	 */
	public void testChunkFile_ExceptionDuringSourcing() throws ChunkableDataProviderAccessException, ChunkableDataSourceException {
	    
        Mockery interfaceMockery = new Mockery();
        final Sequence sequence = interfaceMockery.sequence("sequence");
        final ChunkableDataProvider chunkableDataProvider = interfaceMockery.mock(ChunkableDataProvider.class);
        final FileChunkDao fileChunkDao = interfaceMockery.mock(FileChunkDao.class);

        final ChunkableDataSourceException chunkableDataSourceException = new ChunkableDataSourceException("an exception", null);
        
        
        
        //create expectations
        expectDataProviderInitialisation(interfaceMockery, chunkableDataProvider, sequence);
        expectNoExistingChunksNewHeader(interfaceMockery, sequence, fileChunkDao);
        expectSourceChunks_InputStream_ThrowsException(interfaceMockery, sequence, chunkableDataProvider, chunkableDataSourceException);

        //execute the method
		ChunkerImpl chunkerImpl = new ChunkerImpl(fileChunkDao,chunkableDataProvider,
				Chunker.MODE_INPUT_STREAM);

		try {
			chunkerImpl.chunkFile(remoteDir, fileName, ONE_MB);
			fail("exception should have been thrown");
		} catch (ChunkException e) {
			// check that the underlying exception is the access exception
			assertEquals(
					"Cause of exception should have been the ChunkableDataSourceException thrown by the provider",
					chunkableDataSourceException, e.getCause());
		}
	}

	/**
	 * test the chunkFile method
	 * @throws ChunkableDataSourceException 
	 * @throws ChunkableDataProviderAccessException 
	 * @throws ChunkLoadException 
	 */
	public void testChunkFile_ExceptionUnsupportedResume() throws ChunkableDataProviderAccessException, ChunkableDataSourceException, ChunkLoadException {
	    
        Mockery interfaceMockery = new Mockery();
        final Sequence sequence = interfaceMockery.sequence("sequence");
        final ChunkableDataProvider chunkableDataProvider = interfaceMockery.mock(ChunkableDataProvider.class);
        final FileChunkDao fileChunkDao = interfaceMockery.mock(FileChunkDao.class);

 
        //create expectations
        expectDataProviderInitialisation(interfaceMockery, chunkableDataProvider, sequence);
        expectExistingChunks(interfaceMockery, sequence, fileChunkDao, handles);
        expectExistingChunkLoad(interfaceMockery, sequence, fileChunkDao, handles);
        

        //execute the method
		ChunkerImpl chunkerImpl = new ChunkerImpl(fileChunkDao, chunkableDataProvider, Chunker.MODE_INPUT_STREAM);

		try {
			chunkerImpl.chunkFile(remoteDir, fileName, ONE_MB);
			fail("exception should have been thrown");
		} catch (ChunkException e) {
			// check that the underlying exception is a source exception
			// concerning the resume functionality
			assertTrue(
					"Underlying exception should have been a ChunkableDataSourceException",
					(e.getCause() instanceof ChunkableDataSourceException));
			assertTrue(
					"Underlying exception should have concerned the resume functionality",
					e.getCause().getMessage().indexOf("resume") > -1);
		}
	}

	/**
	 * test the chunkFile method with an invalid mode
	 * @throws ChunkableDataSourceException 
	 * @throws ChunkableDataProviderAccessException 
	 * 
	 */
	public void testChunkFile_InvalidMode() throws ChunkableDataProviderAccessException, ChunkableDataSourceException {

	       Mockery interfaceMockery = new Mockery();
	       final Sequence sequence = interfaceMockery.sequence("sequence");
	       final ChunkableDataProvider chunkableDataProvider = interfaceMockery.mock(ChunkableDataProvider.class);
	       final FileChunkDao fileChunkDao = interfaceMockery.mock(FileChunkDao.class);

	        //create expectations
	        expectDataProviderInitialisation(interfaceMockery, chunkableDataProvider, sequence);
	        expectNoExistingChunksNewHeader(interfaceMockery, sequence, fileChunkDao);
	    

		ChunkerImpl chunkerImpl = new ChunkerImpl(fileChunkDao, chunkableDataProvider,
				-1);
		try {
			chunkerImpl.chunkFile(remoteDir, fileName, ONE_MB);
		} catch (ChunkException e) {
			// check that the underlying exception is a source exception
			// concerning an unknown mode
			assertTrue(
					"Underlying exception should have been a ChunkableDataSourceException",
					(e.getCause() instanceof ChunkableDataSourceException));
			assertTrue(
					"Underlying exception should have concerned an unknown mode",
					e.getCause().getMessage().indexOf("Unknown mode") > -1);
		}

	}

	/**
	 * test the chunkFile method
	 * 
	 * @throws ChunkException
	 * @throws ChunkableDataProviderAccessException 
	 * @throws ChunkableDataSourceException 
	 */
	public void testChunkFile_UsingInputStream() throws ChunkException, ChunkableDataSourceException, ChunkableDataProviderAccessException {
	   
	    Mockery interfaceMockery = new Mockery();
	    final Sequence sequence = interfaceMockery.sequence("sequence");
	    final ChunkableDataProvider chunkableDataProvider = interfaceMockery.mock(ChunkableDataProvider.class);
	    final FileChunkDao fileChunkDao = interfaceMockery.mock(FileChunkDao.class);
	    
	    //create expectations
	    expectDataProviderInitialisation(interfaceMockery, chunkableDataProvider, sequence);
	    expectNoExistingChunksNewHeader(interfaceMockery, sequence, fileChunkDao);
	    expectSourceChunks_InputStream(interfaceMockery, sequence, chunkableDataProvider);
	    expectSaveHeader(interfaceMockery, sequence, fileChunkDao);
	    expectDisconnectSuccessful(interfaceMockery, sequence,
            chunkableDataProvider);
	    

	    //execute the method
		ChunkerImpl chunkerImpl = new ChunkerImpl(fileChunkDao, chunkableDataProvider,
				Chunker.MODE_INPUT_STREAM);
		chunkerImpl.chunkFile(remoteDir, fileName, ONE_MB);
	}
	
	
    /**
     * test the chunkFile method with an invalid mode
     * 
     * @throws ChunkableDataSourceException 
     * @throws ChunkableDataProviderAccessException 
     * 
     */
    public void testChunkFile_InvalidChunkSize() throws ChunkableDataProviderAccessException, ChunkableDataSourceException {

           Mockery interfaceMockery = new Mockery();
           final Sequence sequence = interfaceMockery.sequence("sequence");
           final ChunkableDataProvider chunkableDataProvider = interfaceMockery.mock(ChunkableDataProvider.class);
           final FileChunkDao fileChunkDao = interfaceMockery.mock(FileChunkDao.class);

            //create expectations
            expectDataProviderInitialisation(interfaceMockery, chunkableDataProvider, sequence);
            expectNoExistingChunksNewHeader(interfaceMockery, sequence, fileChunkDao);
        

        ChunkerImpl chunkerImpl = new ChunkerImpl(fileChunkDao, chunkableDataProvider,
                -1);
        try {
            chunkerImpl.chunkFile(remoteDir, fileName, 0);
            fail("exception should have been thrown as an invalid chunk size was used");
        } catch (ChunkException e) {
            assertTrue(
                    "Underlying exception should have concerned an invalid chunk size",
                    e.getMessage().indexOf("chunkSize") > -1);
        }

    }	
	


	/**
	 * test the chunkFile method
	 * 
	 * @throws ChunkException
	 * @throws ChunkableDataSourceException 
	 * @throws ChunkableDataProviderAccessException 
	 */
	public void testChunkFile_UsingOuputStream() throws ChunkException, ChunkableDataProviderAccessException, ChunkableDataSourceException {

        Mockery interfaceMockery = new Mockery();
        final Sequence sequence = interfaceMockery.sequence("sequence");
        final ChunkableDataProvider chunkableDataProvider = interfaceMockery.mock(ChunkableDataProvider.class);
        final FileChunkDao fileChunkDao = interfaceMockery.mock(FileChunkDao.class);

    
        //create expectations
        expectDataProviderInitialisation(interfaceMockery, chunkableDataProvider, sequence);
        expectNoExistingChunksNewHeader(interfaceMockery, sequence, fileChunkDao);
        expectSourceChunks_OutputStream(interfaceMockery, sequence, chunkableDataProvider);
        expectSaveHeader(interfaceMockery, sequence, fileChunkDao);
        
        expectDisconnectSuccessful(interfaceMockery, sequence, chunkableDataProvider);
	        
        //execute the method
		ChunkerImpl chunkerImpl = new ChunkerImpl(fileChunkDao, chunkableDataProvider,
				Chunker.MODE_OUTPUT_STREAM);
		chunkerImpl.chunkFile(remoteDir, fileName, ONE_MB);

	}

    /**
	 * test the handleChunk method
	 */
	public void testHandleChunk() {

		FileChunkDao dao = new MockFileChunkDao(new ArrayList<FileChunk>());
		ChunkerImpl chunkerImpl = new ChunkerImpl(dao, null, 0);

		byte[] payload = new byte[] { 1, 2, 3 };
		chunkerImpl.handleChunk(payload, 1, 10);
		FileChunk lastSaved = ((MockFileChunkDao) dao).getLastSaved();
		assertEquals("FileChunk saved should have th same payload as the bytes passed in", payload, lastSaved.getContent());

	}
	
    /**
     * Expects the chunkableDataProvider to successfully disconnect
     * 
     * @param interfaceMockery
     * @param sequence
     * @param chunkableDataProvider
     * @throws ChunkableDataProviderAccessException
     */
    private void expectDisconnectSuccessful(Mockery interfaceMockery,
            final Sequence sequence,
            final ChunkableDataProvider chunkableDataProvider)
            throws ChunkableDataProviderAccessException
    {
        interfaceMockery.checking(new Expectations(){
            {                
               one(chunkableDataProvider).disconnect(); inSequence(sequence); //finally disconnect from the data provider
           }
       });
    }

    /**
     * Expects the chunkableDataProvider to fail on disconnect
     * 
     * @param interfaceMockery
     * @param sequence
     * @param chunkableDataProvider
     * @param chunkableDataProviderAccessException
     * @throws ChunkableDataProviderAccessException
     */
    private void expectDisconnectUnsuccessful(Mockery interfaceMockery,
            final Sequence sequence,
            final ChunkableDataProvider chunkableDataProvider,
            final ChunkableDataProviderAccessException chunkableDataProviderAccessException)
            throws ChunkableDataProviderAccessException
    {
        interfaceMockery.checking(new Expectations(){
            {                
               one(chunkableDataProvider).disconnect(); inSequence(sequence); //finally disconnect from the data provider
               will(throwException(chunkableDataProviderAccessException));
           }
       });
    }

    /**
     * Expects the fileChunkDao to return existing chunks
     * 
     * @param interfaceMockery
     * @param sequence
     * @param fileChunkDao
     * @param fileConstituentHandles
     * @throws ChunkLoadException
     */
    private void expectExistingChunkLoad(Mockery interfaceMockery,
            final Sequence sequence, final FileChunkDao fileChunkDao,
            final List<FileConstituentHandle> fileConstituentHandles) throws ChunkLoadException
    {
        interfaceMockery.checking(new Expectations(){
            {  

               one(fileChunkDao).load((FileConstituentHandle)(with(a(FileConstituentHandle.class))));inSequence(sequence); //try to load the first chunk
               will(returnValue(fileConstituentHandles.get(0)));
           }
       });
        
    }
    
    /**
     * Expects the fileChunkDao to know about an existing set of chunks
     * 
     * @param interfaceMockery
     * @param sequence
     * @param fileChunkDao
     * @param fileConstituentHandle
     */
    private void expectExistingChunks(Mockery interfaceMockery,
            final Sequence sequence, final FileChunkDao fileChunkDao, final  List<FileConstituentHandle> fileConstituentHandle) 
    {
        
        
        interfaceMockery.checking(new Expectations(){
                {  
                   one(fileChunkDao).findChunks(fileName, null, noOfChunks, ChunkerImpl.ONE_HOUR); inSequence(sequence); //check for any existing chunked data
                   will(returnValue(fileConstituentHandle));
               }
           });
    }
    
    /**
     * Expects the fileChunkDao to fail on returning of existing chunks
     * 
     * @param interfaceMockery
     * @param sequence
     * @param fileChunkDao
     * @param exception
     * @throws ChunkLoadException
     */
    private void expectExistingChunksLoadThrowsException(Mockery interfaceMockery,
            final Sequence sequence, final FileChunkDao fileChunkDao, final Exception exception) throws ChunkLoadException
    {
        
        expectExistingChunks(interfaceMockery, sequence, fileChunkDao, handles);
        
        interfaceMockery.checking(new Expectations(){
                {  

                   one(fileChunkDao).load((FileConstituentHandle)(with(a(FileConstituentHandle.class))));inSequence(sequence); //try to load the first chunk
                   will(throwException(exception));
               }
           });
    }

    /**
     * Expects there to not be any existing chunks, and a new FileChunkHeader to be created and saved
     * 
     * @param interfaceMockery
     * @param sequence
     * @param fileChunkDao
     */
    private void expectNoExistingChunksNewHeader(Mockery interfaceMockery,
            final Sequence sequence, final FileChunkDao fileChunkDao)
    {
        interfaceMockery.checking(new Expectations(){
                {  
                   one(fileChunkDao).findChunks(fileName, null, noOfChunks, ChunkerImpl.ONE_HOUR); inSequence(sequence); //check for any existing chunked data
                   one(fileChunkDao).save((FileChunkHeader)(with(a(FileChunkHeader.class)))); inSequence(sequence); //initially save the file chunk header
               }
           });
    }
    
    /**
     * Expects the fileChunkHeader to be saved
     * 
     * @param interfaceMockery
     * @param sequence
     * @param fileChunkDao
     */
    private void expectSaveHeader(Mockery interfaceMockery,
            final Sequence sequence, final FileChunkDao fileChunkDao)
    {
        interfaceMockery.checking(new Expectations(){
                {                
                   one(fileChunkDao).save((FileChunkHeader)(with(a(FileChunkHeader.class)))); inSequence(sequence); //save the filechunk header again (checksums, etc)
               }
           });
    }
    
    /**
     * Expects the InputStream mode of sourcing chunks to be used from the chunkableDataProvider
     * 
     * @param interfaceMockery
     * @param sequence
     * @param chunkableDataProvider
     * @throws ChunkableDataSourceException
     */
    private void expectSourceChunks_InputStream(Mockery interfaceMockery,
            final Sequence sequence,
            final ChunkableDataProvider chunkableDataProvider)
            throws ChunkableDataSourceException
    {
        interfaceMockery.checking(new Expectations(){
                {  
                   one(chunkableDataProvider).sourceChunkableData(remoteDir, fileName); inSequence(sequence); //source the data itself               
               }
           });
    }
    
    /**
     * Expects the OutputStream mode of sourcing chunks to be used from the chunkableDataProvider
     * 
     * @param interfaceMockery
     * @param sequence
     * @param chunkableDataProvider
     * @throws ChunkableDataSourceException
     */
    private void expectSourceChunks_OutputStream(Mockery interfaceMockery,
            final Sequence sequence,
            final ChunkableDataProvider chunkableDataProvider)
            throws ChunkableDataSourceException
    {
        interfaceMockery.checking(new Expectations(){
                {  
                one(chunkableDataProvider).sourceChunkableData(with(equal(remoteDir)), with(equal(fileName)), (OutputStream)(with(a(OutputStream.class))), (Long)with(equal(0l))); inSequence(sequence); //source the data itself               
               }
           });
    }   


    /**
     * Expects a given Exception to be thrown by the ChunkableDataProvider when sourcing chunks using InputStreams
     * 
     * @param interfaceMockery
     * @param sequence
     * @param chunkableDataProvider
     * @param chunkableDataSourceException
     * @throws ChunkableDataSourceException
     */
    private void expectSourceChunks_InputStream_ThrowsException(Mockery interfaceMockery,
            final Sequence sequence,
            final ChunkableDataProvider chunkableDataProvider,
            final ChunkableDataSourceException chunkableDataSourceException)
            throws ChunkableDataSourceException
    {
        interfaceMockery.checking(new Expectations(){
                {  
                   one(chunkableDataProvider).sourceChunkableData(remoteDir, fileName); inSequence(sequence); //source the data itself               
                   will(throwException(chunkableDataSourceException));
                }
           });
    }

}
