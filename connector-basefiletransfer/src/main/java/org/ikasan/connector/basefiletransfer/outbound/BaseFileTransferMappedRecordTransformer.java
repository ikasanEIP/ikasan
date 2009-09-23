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
package org.ikasan.connector.basefiletransfer.outbound;

import java.util.Date;

import org.apache.log4j.Logger;

import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.ServiceLocator;
import org.ikasan.common.component.Spec;
import org.ikasan.common.util.FileUtils;
import org.ikasan.connector.ResourceLoader;
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
    
    /**
     * Method used to map an <code>BaseFileTransferMappedRecord</code> object to a
     * <code>Payload</code> object.
     * 
     * @param record The record as returned from the File Transfer Client
     * @return A payload constructed from the record.
     */
    public static Payload mappedRecordToPayload(BaseFileTransferMappedRecord record)
    {
        // TODO global service locator
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        Payload payload = serviceLocator.getPayloadFactory().newPayload(record.getName(),
                Spec.BYTE_PLAIN, MetaDataInterface.UNDEFINED, record.getContent());

        // Don't set the Checksum, the client doesn't calculate checksum as the payload does it
        // Don't set the name
        
        // Set the Size
        payload.setSize(record.getSize());
        // Set the source system name
        String componentGroupName = ResourceLoader.getInstance().getProperty("component.group.name");
        payload.setSrcSystem(componentGroupName);
        // Set the format to null
        payload.setFormat(null);

        // Don't set priority, defaults to medium
        // Don't set NoNamespaceSchemaLocation
        // Don't set SchemaInstanceNSURI
        // Don't set timestamp
        // Don't set Encoding, defaults to noenc;
        // Don't set Charset, it defaults to system
        
        // The spec is important as it is used by plugins for verification
        // purposes. Currently all supported spec types are set based on file
        // extension.
        int extSeparatorLoc = record.getName().indexOf('.');
        if (extSeparatorLoc == -1)
        {
            // No extension, so set to plain bytes
            payload.setSpec(Spec.BYTE_PLAIN.toString());
        }
        else
        {
            String extension = record.getName().substring(extSeparatorLoc + 1,
                record.getName().length());
            if (extension.equalsIgnoreCase(FileUtils.ZIP_EXT))
            {
                payload.setSpec(Spec.BYTE_ZIP.toString());
            }
            else if (extension.equalsIgnoreCase(FileUtils.JAR_EXT))
            {
                payload.setSpec(Spec.BYTE_JAR.toString());
            }
            else if (extension.equalsIgnoreCase(FileUtils.TEXT_EXT))
            {
                payload.setSpec(Spec.TEXT_PLAIN.toString());
            }
            else if (extension.equalsIgnoreCase(FileUtils.XML_EXT))
            {
                payload.setSpec(Spec.TEXT_XML.toString());
            }
            else if (extension.equalsIgnoreCase(FileUtils.HTML_EXT)
                    || extension.equalsIgnoreCase(FileUtils.HTM_EXT))
            {
                payload.setSpec(Spec.TEXT_HTML.toString());
            }
            else if (extension.equalsIgnoreCase(FileUtils.CSV_EXT))
            {
                payload.setSpec(Spec.TEXT_CSV.toString());
            }
            else
            {
                logger.info("File extension unknown, setting Spec to: [" //$NON-NLS-1$
                        + Spec.BYTE_PLAIN.toString() + "]"); //$NON-NLS-1$
                payload.setSpec(Spec.BYTE_PLAIN.toString());
            }
        }
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
        record.setChecksum(payload.getChecksum(), payload.getChecksumAlg());
        record.setName(payload.getName());
        record.setSize(payload.getSize());
        record.setCreatedDayTime(new Date(payload.getTimestamp()));
        record.setRecordName(payload.getName());
        record.setRecordShortDescription(null);
        return record;
    }
}
