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
