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
package org.ikasan.framework.payload.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
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

    /** Payload specification for sourced <code>DatabasePayload</code>s */
    private Spec payloadSpec;

    /** Payload originating system name for <code>DatabasePayload</code>s */
    private String payloadSrcSystem;

    /** Logger instance */
    private Logger logger = Logger.getLogger(DatabasePayloadProvider.class);

    /**
     * Constructor
     * 
     * @param dao data access object
     * @param payloadFactory for the construction of new <code>Payload</code>s
     * @param databasePayloadHouseKeepingMatcher used for identifying entries to housekeep
     * @param destructiveRead when set to true, consumed <code>DatabasePayload</code>s will be deleted
     * @param payloadSpec detailing the nature of the payload i.e. text/xml
     * @param payloadSrcSystem name of the system of origination
     */
    public DatabasePayloadProvider(DatabasePayloadDao dao, PayloadFactory payloadFactory,
            DatabaseHousekeeper databasePayloadHouseKeepingMatcher, boolean destructiveRead, Spec payloadSpec,
            String payloadSrcSystem)
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
        this.payloadSpec = payloadSpec;
        this.payloadSrcSystem = payloadSrcSystem;
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
                Payload payload = payloadFactory.newPayload(databasePayload.getId().toString(), this.payloadSpec, this.payloadSrcSystem, payloadContent);
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
