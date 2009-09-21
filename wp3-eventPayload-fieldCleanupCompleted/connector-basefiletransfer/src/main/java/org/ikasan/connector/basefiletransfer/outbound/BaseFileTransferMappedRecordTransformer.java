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
package org.ikasan.connector.basefiletransfer.outbound;

import java.util.Date;

import org.apache.log4j.Logger;
import org.ikasan.common.FilePayloadAttributeNames;
import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.common.factory.PayloadFactoryImpl;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;

/**
 * Utility Transformer class for transforming between BaseFileTransferMappedRecords
 * and other data types (such as Payloads)
 * 
 * @author Ikasan Development Team
 */
public class BaseFileTransferMappedRecordTransformer
{
    /** Logger */
    private static Logger logger = Logger.getLogger(BaseFileTransferMappedRecordTransformer.class);
    
    private static PayloadFactory payloadFactory = new PayloadFactoryImpl();
    
    /**
     * Method used to map an <code>BaseFileTransferMappedRecord</code> object to a
     * <code>Payload</code> object.
     * 
     * @param record The record as returned from the File Transfer Client
     * @return A payload constructed from the record.
     */
    public static Payload mappedRecordToPayload(BaseFileTransferMappedRecord record)
    {


    	Date createdDayTime = record.getCreatedDayTime();
    	
    	//calculate a payload id based on the filename and created date
    	int id = createdDayTime.hashCode();
    	id = (37 * id) + (record.getName()).hashCode();
    	
        Payload payload = payloadFactory.newPayload(""+id,
                record.getContent());
        payload.setAttribute(FilePayloadAttributeNames.FILE_NAME, record.getName());

        // Don't set the Checksum, the client doesn't calculate checksum as the payload does it
        // Don't set the name
        

        
        
        return payload;
    }

    /**
     * Method used to map an <code>Payload</code> object to a
     * <code>BaseFileTransferMappedRecord</code> object.
     * 
     * @param payload The payload to transform
     * @return A BaseFileTransferMappedRecord constructed from the payload.
     */
    public static BaseFileTransferMappedRecord payloadToMappedRecord(Payload payload)
    {
        BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord();
        record.setContent(payload.getContent());
        //record.setChecksum(payload.getChecksum(), payload.getChecksumAlg());
        record.setName(payload.getAttribute(FilePayloadAttributeNames.FILE_NAME));
        record.setSize(payload.getSize());
        //record.setCreatedDayTime(new Date(payload.getTimestamp()));
        record.setRecordName(payload.getAttribute(FilePayloadAttributeNames.FILE_NAME));
        record.setRecordShortDescription(null);
        return record;
    }
}
