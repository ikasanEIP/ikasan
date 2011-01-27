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
package org.ikasan.framework.payload.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.payload.dao.DatabasePayloadDao;
import org.ikasan.framework.payload.model.DatabasePayload;

/**
 * Implementation of <code>PayloadProvider</code> that discovers new Payloads from the Database by way of unconsumed
 * <code>DatabasePayload</code>s
 * 
 * @author Ikasan Development Team
 */
public class DatabasePayloadProvider implements PayloadProvider
{
    /** Data access object for persisting <code>DatabasePayload</code>s */
    private DatabasePayloadDao dao;

    /** Factory for <code>Payload</code>s */
    private PayloadFactory payloadFactory;

    /**
     * Flag for destructive reading - when set, <code>DatabasePayload</code>s will be deleted immediately upon
     * consumption
     */
    private boolean destructiveRead;

    /** Housekeeper for cleaning up old <code>DatabasePayload</code>s */
    private DatabaseHousekeeper housekeeper;



    /** Logger instance */
    private Logger logger = Logger.getLogger(DatabasePayloadProvider.class);

    /**
     * Constructor
     * 
     * @param dao data access object
     * @param payloadFactory for the construction of new <code>Payload</code>s
     * @param databasePayloadHouseKeepingMatcher used for identifying entries to housekeep
     * @param destructiveRead when set to true, consumed <code>DatabasePayload</code>s will be deleted
     */
    public DatabasePayloadProvider(DatabasePayloadDao dao, PayloadFactory payloadFactory,
            DatabaseHousekeeper databasePayloadHouseKeepingMatcher, boolean destructiveRead)
    {
        if ((databasePayloadHouseKeepingMatcher != null) && (destructiveRead))
        {
            throw new IllegalArgumentException("Destructive Read and housekeeping are mutually exclusive");
        }
        this.dao = dao;
        if (this.dao == null)
        {
            throw new IllegalArgumentException("dao cannot be 'null'");
        }

        this.payloadFactory = payloadFactory;
        if (this.payloadFactory == null)
        {
            throw new IllegalArgumentException("payloadFactory cannot be 'null'");
        }
        this.housekeeper = databasePayloadHouseKeepingMatcher;
        this.destructiveRead = destructiveRead;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.event.service.PayloadProvider#getNextRelatedPayloads()
     */
    public List<Payload> getNextRelatedPayloads()
    {
        List<Payload> result = null;
        logger.debug("about to request unconsumed from dao");
        List<DatabasePayload> unconsumedPayloads = dao.findUnconsumed();
        logger.debug("back from request for unconsumed from dao");
        if (!unconsumedPayloads.isEmpty())
        {
            result = new ArrayList<Payload>();
            for (DatabasePayload databasePayload : unconsumedPayloads)
            {
                logger.info("consuming DatabasePayload with id [" + databasePayload.getId() + "]");
                byte[] payloadContent = databasePayload.getEvent().getBytes();
                Payload payload = payloadFactory.newPayload(databasePayload.getId().toString(),  payloadContent);
                payload.setContent(payloadContent);
                result.add(payload);
                databasePayload.setConsumed(true);
                databasePayload.setLastUpdated(new Date());
                if (destructiveRead)
                {
                    logger.info("deleting (destructive read) DatabasePayload with id [" + databasePayload.getId() + "]");
                    dao.delete(databasePayload);
                }
                else
                {
                    dao.save(databasePayload);
                }
            }
        }
        // housekeeping, if configured
        if (housekeeper != null)
        {
            logger.debug("attempting to housekeep");
            housekeeper.housekeep();
        }
        else
        {
            logger.debug("housekeeping not configured");
        }
        return result;
    }
}
