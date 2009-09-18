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
package org.ikasan.connector.basefiletransfer.outbound.command.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.ikasan.common.FilePayloadAttributeNames;
import org.ikasan.common.Payload;
import org.jmock.Expectations;
import org.jmock.Mockery;

/**
 * Test class for FilenameRegexpMatchedTargetDirectorySelector
 * 
 * @author Ikasan Development Team
 */
public class FilenameRegexpMatchedTargetDirectorySelectorTest extends TestCase
{

    /**
     * Test that the selector returns null if a map of mappings was never provided
     */
    public void testGetTargetDirectory_NullMap()
    {
        final String fileName = "abc.txt";
        final Payload payload = getPayload(fileName);
        
        TargetDirectorySelector selector = new FilenameRegexpMatchedTargetDirectorySelector(null);
        assertNull("selector should return null if no mapping was provided", selector.getTargetDirectory(payload)); //$NON-NLS-1$
    }
    
    /**
     * Test that the selector returns parentDir if a map of mappings was never provided
     */
    public void testGetTargetDirectory_WithParent_NullMap()
    {
        final String fileName = "abc.txt";
        final Payload payload = getPayload(fileName);
        final String parentDir = "a/parent/path";
        
        TargetDirectorySelector selector = new FilenameRegexpMatchedTargetDirectorySelector(null);
        assertEquals("selector should return parentDir if no mapping was provided", parentDir, selector.getTargetDirectory(payload, parentDir)); //$NON-NLS-1$
    }
    
    /**
     * Test that the selector returns null if there is no match on the filename in
     * the map of mappings
     */
    public void testGetTargetDirectory_NoMatch()
    {
        final String fileName = "abc.txt";
        final Payload payload = getPayload(fileName);
        
        Map<String, String> mappings = new HashMap<String, String>();
        mappings.put("someRandomRegexp", "some/meaningless/path");
        
        TargetDirectorySelector selector = new FilenameRegexpMatchedTargetDirectorySelector(mappings);
        assertNull("selector should return null if no match was found", selector.getTargetDirectory(payload));  //$NON-NLS-1$
    }
    
    /**
     * Test that the selector returns parentDir if there is no match on the filename in
     * the map of mappings
     */
    public void testGetTargetDirectory_WithParent_NoMatch()
    {
        
        final String fileName = "abc.txt";
        final Payload payload = getPayload(fileName);
        final String parentDir = "a/parent/path";
        
        Map<String, String> mappings = new HashMap<String, String>();
        mappings.put("someRandomRegexp", "some/meaningless/path");
        
        TargetDirectorySelector selector = new FilenameRegexpMatchedTargetDirectorySelector(mappings);
        
        assertEquals("selector should return parentDir if no match was found", parentDir, selector.getTargetDirectory(payload, parentDir));  //$NON-NLS-1$
    }
 
    /**
     * Tests that a match will be returned  if foud
     */
    public void testGetTargetDirectory_Match()
    {
        
        final String fileName = "abc.txt";
        final String exactlyMatchingRegExp = fileName;
        
        assertMatches(fileName, exactlyMatchingRegExp);
        
        final Payload payload = getPayload(fileName);
        
        Map<String, String> mappings = new HashMap<String, String>();
        String PATH_FOR_FIRST_MATCHING_ENTRY = "path/for/first/matching/entry";
        mappings.put(exactlyMatchingRegExp, PATH_FOR_FIRST_MATCHING_ENTRY);
        
        TargetDirectorySelector selector = new FilenameRegexpMatchedTargetDirectorySelector(mappings);
        
        assertEquals("selector should return the path for a matching entry", PATH_FOR_FIRST_MATCHING_ENTRY, selector.getTargetDirectory(payload));  //$NON-NLS-1$
    }  
    
    /**
     * Tests that a match will be relative to the parent dir, if supplied
     */
    public void testGetTargetDirectory_WithParent_Match()
    {
        
        final String fileName = "abc.txt";
        final String exactlyMatchingRegExp = fileName;
        final String parentDir = "a/parent/path";
        
        assertMatches(fileName, exactlyMatchingRegExp);
 
        final Payload payload = getPayload(fileName);
        
        Map<String, String> mappings = new HashMap<String, String>();
        String PATH_FOR_FIRST_MATCHING_ENTRY = "path/for/first/matching/entry";
        mappings.put(exactlyMatchingRegExp, PATH_FOR_FIRST_MATCHING_ENTRY);
        
        String fullPath = parentDir+"/"+PATH_FOR_FIRST_MATCHING_ENTRY;
        
        TargetDirectorySelector selector = new FilenameRegexpMatchedTargetDirectorySelector(mappings);
        assertEquals("selector should return the path for a matching entry, relative to a parent path if supplied", fullPath, selector.getTargetDirectory(payload, parentDir));  //$NON-NLS-1$
    }    
    
    /**
     * Test that if the selector contains multiple matching entries, then the alphabetically 
     * earliest match (of the regexp itself) will be the match used.
     * 
     * Note that this is not really a feature, but ensures a consistent result whilst
     * ordering is not guaranteed when iterating over a <code>java.util.Set</code>
     */
    public void testGetTargetDirectory_MultipleMatch()
    {
        
        final String fileName = "abc.txt";
        final String exactlyMatchingRegExp = fileName;
        final String alsoMatchingRegExp = fileName+"*";
        
        assertMatches(fileName, exactlyMatchingRegExp);
        assertMatches(fileName, alsoMatchingRegExp);
        
        final Payload payload = getPayload(fileName);
        
        Map<String, String> mappings = new HashMap<String, String>();
        String PATH_FOR_FIRST_MATCHING_ENTRY = "path/for/first/matching/entry";
        String PATH_FOR_LATER_MATCHING_ENTRY = "path/for/later/matching/entry";
        
        mappings.put(exactlyMatchingRegExp, PATH_FOR_FIRST_MATCHING_ENTRY);
        mappings.put(alsoMatchingRegExp, PATH_FOR_LATER_MATCHING_ENTRY);
        
        TargetDirectorySelector selector = new FilenameRegexpMatchedTargetDirectorySelector(mappings);
        
        assertEquals("selector should return the first match found assuming that patterns are mathced in sorted order", PATH_FOR_FIRST_MATCHING_ENTRY, selector.getTargetDirectory(payload));  //$NON-NLS-1$
    }

    /**
     * Checks that the value supplied matches the regexp supplied
     * @param value
     * @param regExpString
     */
    private void assertMatches(String value, String regExpString)
    {
        assertTrue(Pattern.compile(regExpString).matcher(value).matches());
    }

    /**
     * Mocks a payload representing a file with a specified name 
     * 
     * @param fileName
     * @return mocked Payload
     */
    private Payload getPayload(final String fileName)
    {
        Mockery context = new Mockery();
        final Payload payload = context.mock(Payload.class);
        
        context.checking(new Expectations()
        {
            {
                one(payload).getAttribute(FilePayloadAttributeNames.FILE_NAME);
                will(returnValue(fileName));
            }
        });
        return payload;
    }
}
