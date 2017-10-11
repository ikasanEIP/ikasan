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
package org.ikasan.connector.util.chunking.model.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.ikasan.filetransfer.util.checksum.DigestChecksum;
import org.ikasan.filetransfer.util.checksum.Md5Checksum;
import org.ikasan.connector.util.chunking.model.FileChunk;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;

/**
 * Hibernate implementation of the FileChunkDao
 * 
 * @author Ikasan Development Team
 * 
 */
public class HibernateFileChunkDao implements FileChunkDao
{
    /** Logger */
    private static Logger logger = LoggerFactory.getLogger(HibernateFileChunkDao.class);
    /**
     * Hibernate fileName parameter
     */
    private static final String FILE_NAME_PARAMETER = "fileName";
    /**
     * Hibernate chunkTimeStamp parameter
     */
    private static final String CHUNK_TIME_STAMP_PARAMETER = "chunkTimeStamp";
    /**
     * Hibernate chunkTimeStamp parameter
     */
    private static final String SEQUENCE_LENGTH_PARAMETER = "sequenceLength";
    /**
     * Hibernate chunkTimeStamp parameter
     */
    private static final String MAX_AGE_PARAMETER = "maxAge";
    /**
     * Hibernate query for finding all related FileChunks by fileName
     */
    private static final String FIND_RELATED_CHUNKS = "select f.id, f.ordinal, f.fileChunkHeader from FileChunk f where f.fileChunkHeader.fileName = :"
            + FILE_NAME_PARAMETER + " and f.fileChunkHeader.chunkTimeStamp = :" + CHUNK_TIME_STAMP_PARAMETER;
    /**
     * Hibernate query for finding the latest timestamp for a given filename
     */
    private static final String FIND_LATEST_TIMESTAMP = "select max(chunkTimeStamp) from FileChunkHeader h where h.fileName = :"
            + FILE_NAME_PARAMETER;
    /**
     * Hibernate id parameter
     */
    private static final String ID_PARAMETER = "id";
    /**
     * Hibernate query for finding FileChunk by id
     */
    private static final String FIND_CHUNK_BY_ID = "from FileChunk f where f.id = :" + ID_PARAMETER;
    /**
     * Hibernate query for finding FileChunkHeader by id
     */
    private static final String FIND_CHUNK_HEADER_BY_ID = "from FileChunkHeader f where f.id = :" + ID_PARAMETER;
    /**
     * and hibernate query keyword
     */
    private static final String AND = " and ";
    /**
     * clause to match sequence length
     */
    private static final String CLAUSE_MATCH_SEQUENCE_LENGTH = "f.fileChunkHeader.sequenceLength = :"
            + SEQUENCE_LENGTH_PARAMETER;
    /**
     * clause to restrict to max age
     */
    private static final String CLAUSE_MAX_AGE = "f.fileChunkHeader.chunkTimeStamp > :" + MAX_AGE_PARAMETER;
    /**
     * Session Factory for the Hibernate Session
     */
    private SessionFactory sessionFactory;

    /**
     * Constructor for DAO
     * 
     * @param sf
     */
    public HibernateFileChunkDao(SessionFactory sf)
    {
        this.sessionFactory = sf;
    }

    /**
     * @param sesssionFactoryJndiPath
     * @throws ResourceException
     */
    public HibernateFileChunkDao(String sesssionFactoryJndiPath) throws ResourceException
    {

    }

    /**
     * Constructor for DAO
     * 
     * @param sff
     * @throws ResourceException
     */
    public HibernateFileChunkDao(HibernateSessionFactoryFactory sff) throws ResourceException
    {
        this.sessionFactory = sff.getSessionFactory();
    }

    /**
     * for debug purposes
     */
    private Long firstSaveTime;

    public void save(FileChunk fileChunk)
    {
        // logger.debug("save called with fileChunk [" + fileChunk + "]");
        // logger.debug("free memory [" + fileChunk.getOrdinal() + ","
        // + Runtime.getRuntime().freeMemory() + "]");

        long saveStartTime = System.currentTimeMillis();

        if (firstSaveTime == null)
        {
            firstSaveTime = saveStartTime;
        }
        fileChunk.calculateChecksum();

        Session session = sessionFactory.openSession();        
        /*
         * NOTE:  We do not start a Txn here because we should be using a
         * Txn manager, which alread has us involved in a txn
         */
        //session.beginTransaction();
        session.save(fileChunk);
        /*
         * NOTE:  We do not commit a Txn here because we should be using a
         * Txn manager, which commits for us
         */
        //session.getTransaction().commit();
        session.close();

        //long saveEndTime = System.currentTimeMillis();
        //logger.debug("completed chunk save in
        //["+(saveEndTime-saveStartTime)+"] ms, time since chunking started
        //=["+(saveEndTime-firstSaveTime)+"] ms");
    }

    /*
     * (non-Javadoc)
     * 
     * @see chunkedFtp.dao.FileChunkDao#load(chunkedFtp.window.FileConstituentHandle)
     */
    public FileChunk load(FileConstituentHandle fileConstituentHandle) throws ChunkLoadException
    {
        FileChunk result = null;
        Session querySession = sessionFactory.openSession();
        Query query = querySession.createQuery(FIND_CHUNK_BY_ID);
        query.setParameter(ID_PARAMETER, fileConstituentHandle.getId());
        result = (FileChunk) query.uniqueResult();
        querySession.close();
        DigestChecksum localCheckSum = new Md5Checksum();
        localCheckSum.update(result.getContent());
        if (!result.getMd5Hash().equals(localCheckSum.digestToString()))
        {
            throw new ChunkLoadException("DigestChecksum failed on chunk load!"); //$NON-NLS-1$
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see chunkedFtp.dao.FileChunkDao#findChunks(java.lang.String,
     *      java.lang.Long)
     */
    public List<FileConstituentHandle> findChunks(final String fileName, Long chunkTimeStamp, Long noOfChunks,
            Long maxAge)
    {
        List<FileConstituentHandle> result = new ArrayList<FileConstituentHandle>();
        Long version = chunkTimeStamp;
        if (version == null)
        {
            // get the latest chunk timestamp for this filename
            version = getLatestTimestamp(fileName);
        }
        Session querySession = sessionFactory.openSession();
        StringBuffer queryString = new StringBuffer(FIND_RELATED_CHUNKS);
        if (noOfChunks != null)
        {
            queryString.append(AND);
            queryString.append(CLAUSE_MATCH_SEQUENCE_LENGTH);
        }
        if (maxAge != null)
        {
            queryString.append(AND);
            queryString.append(CLAUSE_MAX_AGE);
        }
        Query query = querySession.createQuery(queryString.toString());
        query.setParameter(FILE_NAME_PARAMETER, fileName);
        query.setParameter(CHUNK_TIME_STAMP_PARAMETER, version);
        if (noOfChunks != null)
        {
            query.setParameter(SEQUENCE_LENGTH_PARAMETER, noOfChunks);
        }
        if (maxAge != null)
        {
            query.setParameter(MAX_AGE_PARAMETER, System.currentTimeMillis() - maxAge);
        }
        List<?> results = query.list();
        for (Iterator<?> iter = results.iterator(); iter.hasNext();)
        {
            Object[] row = (Object[]) iter.next();
            Long primaryKey = (Long) row[0];
            Long ordinal = (Long) row[1];
            FileChunkHeader fileChunkHeader = (FileChunkHeader) row[2];
            result.add(new FileChunk(fileChunkHeader, ordinal, primaryKey));
        }
        querySession.close();
        logger.debug("HibernateFileChunkDao.findChunks returning result of length:" + result.size()); //$NON-NLS-1$
        Collections.sort(result);
        return result;
    }

    /**
     * Retrieves the latest timestamp on any chunks persisted for a given
     * fileName
     * 
     * @param fileName
     * @return Long timestamp as yyyyMMddHHmmss
     */
    private Long getLatestTimestamp(String fileName)
    {
        Long result = null;
        Session querySession = sessionFactory.openSession();
        Query query = querySession.createQuery(FIND_LATEST_TIMESTAMP);
        query.setParameter(FILE_NAME_PARAMETER, fileName);
        result = (Long) query.uniqueResult();
        querySession.close();
        return result;
    }

    public void save(FileChunkHeader fileChunkHeader)
    {
        logger.debug("save called with:" + fileChunkHeader); //$NON-NLS-1$
        Session session = startSession();
        session.saveOrUpdate(fileChunkHeader);
        /*
         * NOTE:  We do not commit a Txn here because we should be using a
         * Txn manager, which commits for us
         */
        //session.getTransaction().commit();
        session.close();
    }

    public FileChunkHeader load(Long id) throws ChunkHeaderLoadException
    {
        FileChunkHeader result = null;
        Session querySession = sessionFactory.openSession();
        Query query = querySession.createQuery(FIND_CHUNK_HEADER_BY_ID);
        query.setParameter(ID_PARAMETER, id);
        result = (FileChunkHeader) query.uniqueResult();
        querySession.close();

        if (result == null)
        {
            throw new ChunkHeaderLoadException("Could not find FileChunkHeader with id [" + id + "]");  //$NON-NLS-1$//$NON-NLS-2$
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.connector.util.chunking.window.dao.FileChunkDao#delete(org.ikasan.connector.util.chunking.window.FileChunkHeader)
     */
    public void delete(FileChunkHeader fileChunkHeader)
    {
        // Not quite a straight forward cascading delete this, as the
        // relationship between the header and the chunks is deliberately 
        // decoupled to allow the header to be loaded and manipulated 
        // without needing to load the potentially heavy weight chunks.

        // Therefore, need to identify the chunks first, delete them, then
        // delete the header
        List<FileConstituentHandle> chunkHandles = findChunks(fileChunkHeader.getFileName(), fileChunkHeader
            .getChunkTimeStamp(), null, null);

        Session session = null;
        try
        {
            session = startSession();

            // reload each of the chunks and delete it
            Query query = session.createQuery(FIND_CHUNK_BY_ID);
            FileChunk fileChunk = null;
            int counter = 0;
            int size = 0;
            for (FileConstituentHandle handle : chunkHandles)
            {
                query.setParameter(ID_PARAMETER, handle.getId());
                fileChunk = (FileChunk) query.uniqueResult();
                size = fileChunk.getContent().length;
                logger.debug("Chunk is [" + size + "] number of bytes in size"); //$NON-NLS-1$ //$NON-NLS-2$
                session.delete(fileChunk);
                counter++;
                logger.debug("Deleted chunk [" + counter + "] of [" + chunkHandles.size() + "]");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                // Bug fix - we need to add the flush here otherwise Hibernate holds on to the chunks until 
                // the transaction commits.
                session.flush();
            }

            // lastly delete the header record
            session.delete(fileChunkHeader);
            session.flush();

            /*
             * NOTE:  We do not commit a Txn here because we should be using a
             * Txn manager, which commits for us
             */
            //session.getTransaction().commit();
        }
        catch (HibernateException e)
        {
            logger.error(e.getMessage(),e);
            throw e;
        }
        finally
        {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

    }

    /**
     * Initiates a new session
     * 
     * @return Session
     */
    private Session startSession()
    {
        Session newSession = sessionFactory.openSession();
        /*
         * NOTE:  We do not begin a Txn here because we should be using a
         * transactional XA data source connection 
         */
        //newSession.beginTransaction();
        return newSession;
    }
}
