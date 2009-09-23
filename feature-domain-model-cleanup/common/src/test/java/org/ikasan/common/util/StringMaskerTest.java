/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common.util;
import java.util.regex.PatternSyntaxException;

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
