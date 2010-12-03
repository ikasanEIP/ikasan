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
package org.ikasan.connector.basefiletransfer.outbound.command.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Simple handle class for providing lightweight access to files.
 * 
 * Refers to file content via an <code>InputStream</code> rather than ever needing 
 * to refer to the entire content at once
 * 
 * Written with the intention for use within functionality that unzips files over FileTransfer 
 * protocols whereby we do not want to hold the entire file itself in memory 
 * at any given time, rather a handle to an IO stream
 * 
 * @author Ikasan Development Team 
 *
 */
public class FileHandle
{
    
    /** Internal object for encapsulating the meta data for the file */
    private File file;
    
    /** Provides access to the content of the file */
    private InputStream inputStream;
    
    /** Directory flag, true if this handle refers to a directory */
    private boolean isDirectory;

    /**
     * Constructor
     * 
     * @param path
     * @param inputStream
     * @param isDirectory 
     */
    public FileHandle(String path, InputStream inputStream, boolean isDirectory){
        this.inputStream = inputStream;
        this.file = new File(path);
        this.isDirectory=isDirectory;
    }
    
    /**
     * Constructor 
     * 
     * @param path
     * @param byteArrayInputStream
     */
    public FileHandle(String path, ByteArrayInputStream byteArrayInputStream)
    {
        this(path, byteArrayInputStream, false);
    }

    /**
     * Returns the path of the file as a String
     * @return String 
     */
    public String getPath()
    {
        return file.getPath();
    }

    /**
     * Returns the content by way of an InputStream
     * 
     * Note that as with all <code>InputStream</code>s, once read, there is no way to rewind.
     * A new instance of the handle would be required.
     * 
     * @return InputStream
     */
    public InputStream getContentAsInputStream()
    {
        return inputStream;
    }

    /**
     * Accessor for isDirectory flag
     * @return isDirectory
     */
    public boolean isDirectory()
    {
        return isDirectory;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
            .append("isDirectory", this.isDirectory).append("file", this.file).toString(); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
