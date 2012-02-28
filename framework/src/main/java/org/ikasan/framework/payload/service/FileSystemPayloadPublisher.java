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
