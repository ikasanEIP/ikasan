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
package org.ikasan.error.reporting.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ErrorOccurrencesLinkedHashMap.
 * 
 * @author Ikasan Development Team
 */
class ErrorOccurrencesLinkedHashMapTestImpl
{
    /**
     * Test roilloff
     */
    @Test
    void test_rolloff()
    {
        ErrorOccurrencesLinkedHashMap<String,String> errorOccurrences = new ErrorOccurrencesLinkedHashMap<String,String>(5);
        errorOccurrences.put("1", "one");
        errorOccurrences.put("2", "two");
        errorOccurrences.put("3", "three");
        errorOccurrences.put("4", "four");
        errorOccurrences.put("5", "five");

        assertEquals(5, errorOccurrences.size(), "Should be five entries");
        assertTrue(errorOccurrences.containsKey("1"), "Should contain entry 1");
        assertTrue(errorOccurrences.containsKey("2"), "Should contain entry 2");
        assertTrue(errorOccurrences.containsKey("3"), "Should contain entry 3");
        assertTrue(errorOccurrences.containsKey("4"), "Should contain entry 4");
        assertTrue(errorOccurrences.containsKey("5"), "Should contain entry 5");

        errorOccurrences.put("6", "six");
        assertEquals(5, errorOccurrences.size(), "Should be five entries");
        assertFalse(errorOccurrences.containsKey("1"), "Should not contain entry 1");
        assertTrue(errorOccurrences.containsKey("2"), "Should contain entry 2");
        assertTrue(errorOccurrences.containsKey("3"), "Should contain entry 3");
        assertTrue(errorOccurrences.containsKey("4"), "Should contain entry 4");
        assertTrue(errorOccurrences.containsKey("5"), "Should contain entry 5");
        assertTrue(errorOccurrences.containsKey("6"), "Should contain entry 6");

    }

}
