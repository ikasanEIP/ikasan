/*
 * $Id: FilenameRegexpMatchedTargetDirectorySelector.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/outbound/command/util/FilenameRegexpMatchedTargetDirectorySelector.java $
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
