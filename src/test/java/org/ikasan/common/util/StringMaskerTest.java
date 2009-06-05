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
package org.ikasan.common.util;
import java.util.regex.PatternSyntaxException;

import org.ikasan.common.util.StringMasker;

import junit.framework.TestCase;


/**
 * Test class for the StringMasker class
 * @author Ikasan Development Team
 *
 */
public class StringMaskerTest extends TestCase
{
    
    /**
     * Test that a string containing matchable sections are correctly masked
     */
    public void testMask_mathces(){
        
        String mask ="####";

        String regexp = "temp_[a-z0-9\\-]+";
        
        String originalText1 = "Failed to create & navigate to path element [temp_7472836b-83ab-47cc-9d16-c70fe518bfa9] of path [temp_7472836b-83ab-47cc-9d16-c70fe518bfa9]";
        String originalText2 = "Failed to create & navigate to path element [temp_d173e279-f3f7-4280-bde9-cd0c1bccbb2a] of path [temp_d173e279-f3f7-4280-bde9-cd0c1bccbb2a]";
        
        
        String expectedOutput = "Failed to create & navigate to path element ["+mask+"] of path ["+mask+"]";
        
        StringMasker stringMasker = new StringMasker(regexp, mask);
        
        assertEquals(expectedOutput, stringMasker.mask(originalText1));
        assertEquals(expectedOutput, stringMasker.mask(originalText2));
    }
    
    /**
     * Test that a string that does not contain any matchable sections is returned unchanged
     */
    public void testMask_no_mathces(){
        
        String mask ="####";
        String regexp = "temp_[a-z0-9\\-]+";
        
        String originalText = "Failed to create & navigate to path element [blah_7472836b-83ab-47cc-9d16-c70fe518bfa9] of path [temp*7472836b-83ab-47cc-9d16-c70fe518bfa9]";
        
        StringMasker stringMasker = new StringMasker(regexp, mask);
        String output = stringMasker.mask(originalText);
        
        assertEquals(originalText, output);
    }
    
    /**
     * Test that when an illegal regexp is used, a PatternSyntaxException is thrown by the constructor
     */
    public void testMask_illegalRegexp(){
        
        String mask ="####";
        String illegalRegexp = "[]";

        Throwable throwable = null;
        try{
            new StringMasker(illegalRegexp, mask);
            fail("expected an exception with an illegal regexp");
        } catch(Throwable t){
            //thats what we expected
            throwable = t;
        }
        assertTrue(throwable instanceof PatternSyntaxException);

    }
}
