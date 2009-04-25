/*
 * $Id: FileHandle.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/outbound/command/util/FileHandle.java $
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
