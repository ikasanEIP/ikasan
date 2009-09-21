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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.resource.ResourceException;

import org.apache.log4j.Logger;
import org.ikasan.common.FilePayloadAttributeNames;
import org.ikasan.common.Payload;

/**
 * Publishes a Payload to a File System.
 * 
 * Please note that this is *NOT* a JCA connector and should not be used as a primary method of delivering Payloads to a
 * file system.
 * 
 * This service should only be used for a secondary (backup) delivery.
 * 
 * @author Ikasan Development Team
 */
public class FileSystemPayloadPublisher implements PayloadPublisher
{
    /** Output path (directory) to write the Payload to */
    String outputPath = null;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(FileSystemPayloadPublisher.class);

    /**
     * Constructor
     * 
     * @param outputPath Output path (directory) to write the Payload to
     */
    public FileSystemPayloadPublisher(String outputPath)
    {
        this.outputPath = outputPath;
    }

    /**
     * Publishes the payload to the FileSystem.
     * 
     * - Checks to see if the Directory it is publishing to is valid - Checks to see if the character encoding is usable
     * - Fails in an inelegant manner, simply throws a ResourceException
     * 
     * @param payload The payload to publish
     */
    public void publish(Payload payload) throws ResourceException
    {
        logger.info("publishing payload [" + payload.getId() + "] with name [" + payload.getAttribute(FilePayloadAttributeNames.FILE_NAME) + "]");
        // If the outputDirectory is invalid then throw a ResourceException
        if (isOutputPathADirectory() == false)
        {
            logger.warn("Output path [" + this.outputPath + "] is not a valid directory.");
            throw new ResourceException("Output path [" + this.outputPath + "] is not a valid directory");
        }
        // Get the content from the payload into a temp holding structure
        byte[] payloadContent = payload.getContent();
        ByteBuffer inByteBuffer = ByteBuffer.wrap(payloadContent);
        // Get the file name from the payload
        String fileName = payload.getAttribute(FilePayloadAttributeNames.FILE_NAME);
        // Stream and Channel and temp holding structures for data
        FileOutputStream fos = null;
        FileChannel ofc = null;
        try
        {
            File outputFile = new File(this.outputPath);
            logger.info("Publishing to [" + outputFile.getAbsolutePath() + "]");
            fos = new FileOutputStream(outputFile.getAbsolutePath() + File.separator + fileName);
            ofc = fos.getChannel();
            ofc.write(inByteBuffer);
            ofc.force(false);
        }
        catch (IOException e)
        {
            throw new ResourceException(e);
        }
        finally
        {
            try
            {
                if (ofc != null)
                {
                    ofc.close();
                }
                if (fos != null)
                {
                    fos.close();
                }
            }
            catch (IOException ioe)
            {
                throw new ResourceException(ioe);
            }
        }
    }

    /**
     * Check if the outputPath is a valid directory, return true if so, else false
     * 
     * @return true if the outputPath is a valid directory, else false
     */
    private boolean isOutputPathADirectory()
    {
        if (this.outputPath == null)
        {
            return false;
        }
        File outputPathDirectory = new File(this.outputPath);
        return outputPathDirectory.isDirectory();
    }
}
