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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.resource.ResourceException;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.payload.service.PayloadInputStreamAcquirer;
import org.ikasan.framework.payload.service.PayloadPublisher;

/**
 * <code>PayloadPublisher<code> that knows how to deliver an referenced 
 * <code>InputStream</code> to the File System
 * 
 * @author Ikasan Development Team
 */
public class FileSystemInputStreamPayloadPublisher implements PayloadPublisher
{
    /** The input stream provider for the payload */
    private PayloadInputStreamAcquirer payloadInputStreamProvider;

    /** The output directory */
    private String outputDir;

    /** The logger */
    private Logger logger = Logger.getLogger(FileSystemInputStreamPayloadPublisher.class);

    /**
     * Constructor
     * 
     * @param outputDir - directory path to which to write the file
     * @param payloadInputStreamProvider - factory for the output content stream
     */
    public FileSystemInputStreamPayloadPublisher(String outputDir, PayloadInputStreamAcquirer payloadInputStreamProvider)
    {
        super();
        this.outputDir = outputDir;
        this.payloadInputStreamProvider = payloadInputStreamProvider;
    }

    public void publish(Payload payload) throws ResourceException
    {
        File parentDir = new File(outputDir);
        if (!parentDir.exists())
        {
            logger.error("directory does not exist [" + parentDir.getAbsolutePath() + "]");
            throw new ResourceException("Parent directory does not exist on local file system ["
                    + parentDir.getAbsolutePath() + "]");
        }
        if (!parentDir.isDirectory())
        {
            logger.error("path does not refer to a directory [" + parentDir.getAbsolutePath() + "]");
            throw new ResourceException("Parent directory is not a directory [" + parentDir.getAbsolutePath() + "]");
        }
        File file = new File(parentDir, payload.getName());
        FileOutputStream fileOutputStream = null;
        try
        {
            file.createNewFile();
            InputStream inputStream = payloadInputStreamProvider.acquireInputStream(payload);
            fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            int result = 0;
            while (result != -1)
            {
                result = inputStream.read();
                if (result != -1)
                {
                    bufferedOutputStream.write(result);
                }
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        }
        catch (FileNotFoundException e)
        {
            logger.error("Exception writing to fileSystem", e);
            throw new ResourceException(e);
        }
        catch (IOException e)
        {
            logger.error("Exception writing to fileSystem", e);
            throw new ResourceException(e);
        }
        finally
        {
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                }
                catch (IOException e)
                {
                    logger.error("Exception closing FileOutputStream", e);
                    throw new ResourceException(e);
                }
            }
        }
    }
}
