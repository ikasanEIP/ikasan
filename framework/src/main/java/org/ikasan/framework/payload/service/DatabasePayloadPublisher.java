/*
 * $Id: DatabasePayloadPublisher.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/payload/service/DatabasePayloadPublisher.java $
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

import java.util.Date;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.payload.dao.DatabasePayloadDao;
import org.ikasan.framework.payload.model.DatabasePayload;

/**
 * Publishes an Ikasan Event to a Database
 * 
 * @author Ikasan Development Team
 */
public class DatabasePayloadPublisher implements PayloadPublisher
{
    /** Data access object for <code>DatabasePayload</code> persistence */
    protected DatabasePayloadDao dao;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(DatabasePayloadPublisher.class);

    /**
     * Constructor
     * 
     * @param dao data access object for <code>DatabasePayload</code> persistence
     */
    public DatabasePayloadPublisher(DatabasePayloadDao dao)
    {
        super();
        this.dao = dao;
    }

    public void publish(Payload payload)
    {
        logger.info("publishing payload [" + payload.getId() + "] to database");
        String payloadString = new String(payload.getContent());
        dao.save(new DatabasePayload(payloadString, new Date()));
    }
}
