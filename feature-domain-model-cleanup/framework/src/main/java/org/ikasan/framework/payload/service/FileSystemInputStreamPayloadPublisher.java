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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.resource.ResourceException;

import org.apache.log4j.Logger;
import org.ikasan.common.FilePayloadAttributeNames;
import org.ikasan.common.Payload;

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
        File file = new File(parentDir, payload.getAttribute(FilePayloadAttributeNames.FILE_NAME));
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
