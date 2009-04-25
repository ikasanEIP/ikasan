/*
 * $Id: ExecutionContext.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/base/command/ExecutionContext.java $
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
package org.ikasan.connector.base.command;

import java.util.HashMap;

import javax.resource.ResourceException;

import org.apache.log4j.Logger;

/**
 * Class to capture the invocational context of a Command
 * 
 * @author Ikasan Development Team
 */
public class ExecutionContext extends HashMap<String, Object>
{
    /** UID */
    private static final long serialVersionUID = -7248180806934670586L;
    
    // TODO need a better place to put these constants
    /** retrievableFileParam */
    public static final String RETRIEVABLE_FILE_PARAM = "retrievableFileParam";
    /** renamableFilePathParam */
    public static final String RENAMABLE_FILE_PATH_PARAM = "renamableFilePathParam";
    /** newFilePathParam */
    public static final String NEW_FILE_PATH_PARAM = "newFilePathParam";
    /** deliveredFilePathParam */
    public static final String DELIVERED_FILE_PATH_PARAM = "deliveredFilePathParam";
    /** relativeFilePathPara, */
    public static final String RELATIVE_FILE_PATH_PARAM = "relativeFilePathParam";
    /** baseFileTransferMappedRecord */
    public static final String BASE_FILE_TRANSFER_MAPPED_RECORD = "baseFileTransferMappedRecord";
    /** file input stream */
    public static final String FILE_INPUT_STREAM = "fileInputStream";
    
    /** payload */
    public static final String PAYLOAD = "payload";
    /** clientId */
    public static final String CLIENT_ID = "clientId";
    /** batchedFileProvider */
    public static final String BATCHED_FILE_PROVIDER = "batchedFileProvider";
    /** batchedFileName */
    public static final String BATCHED_FILE_NAME = "batchedFileName";
    /** fileChunkHeader */
    public static final String FILE_CHUNK_HEADER = "fileChunkHeader";
    
    /** Logger */
    private static Logger logger = Logger.getLogger(ExecutionContext.class);
    
    @Override
    public Object put(String arg0, Object arg1)
    {
        logger.debug("setting: [" + arg0 + "], with value: [" + arg1 + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return super.put(arg0, arg1);
    }

    /**
     * Returns a required String parameter
     * 
     * @param key
     * @return String parameter
     * @throws ResourceException 
     */
    public String getRequiredString(String key) throws ResourceException
    {
        String value = (String) getRequired(key);
        return value;
    }

    /**
     * Checks that a returned value exists
     * 
     * @param key
     * @param value
     * @throws ResourceException
     */
    private void checkExists(String key, Object value) throws ResourceException
    {
        if (value == null)
        {
            throw new ResourceException("Could not find required execution parameter: [" + key + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Returns a String parameter
     * @param key
     * @return parameter as a String or null if non existent
     */
    public String getString(String key)
    {
        String value = (String) get(key);
        return value;
    }
    
    /**
     * Returns a required Object parameter
     * @param key
     * @return String parameter
     * @throws ResourceException 
     */
    public Object getRequired(String key) throws ResourceException
    {
        Object value = get(key);
        checkExists(key, value);
        return value;
    }
    
}
