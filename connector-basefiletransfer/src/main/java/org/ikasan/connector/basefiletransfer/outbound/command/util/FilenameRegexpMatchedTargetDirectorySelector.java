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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.ikasan.common.Payload;

/**
 * <code>TargetDirectorySelector</code> implementation that determines the
 * target directory based on a regexp pattern match of the filename
 * 
 * @author Ikasan Development Team 
 */
public class FilenameRegexpMatchedTargetDirectorySelector implements TargetDirectorySelector
{
    /**
     * Map of target directories keyed by the regular expressions for filenmes
     * that should be delivered to that directory
     */
    private Map<String, String> patternTargetDirectoryMap;

    /**
     * Constructor
     * 
     * @param patternTargetDirectoryMap
     */
    public FilenameRegexpMatchedTargetDirectorySelector(Map<String, String> patternTargetDirectoryMap)
    {
        super();
        this.patternTargetDirectoryMap = patternTargetDirectoryMap;
    }

    public String getTargetDirectory(Payload payload)
    {
        String targetDirectory = null;
        if (patternTargetDirectoryMap != null)
        {
            String fileName = payload.getName();

            // sort the regexp values so that we get the same result everytime,
            // rather than get possibly different matches because java doesnt
            // guarantee
            // order of java.util.Set values

            List<String> sortedList = new ArrayList<String>(patternTargetDirectoryMap.keySet());
            Collections.sort(sortedList);

            // look for the first match
            for (String regexpString : sortedList)
            {
                Pattern pattern = Pattern.compile(regexpString);

                if (pattern.matcher(fileName).matches())
                {
                    targetDirectory = patternTargetDirectoryMap.get(regexpString);
                    // one match is enough, lets get out
                    break;
                }
            }
        }
        return targetDirectory;
    }

    public String getTargetDirectory(Payload payload, String parentDir)
    {
        String result = parentDir;

        String selectorResult = getTargetDirectory(payload);
        if (selectorResult != null)
        {
            result = parentDir + "/" + selectorResult;
        }
        return result;
    }
}
