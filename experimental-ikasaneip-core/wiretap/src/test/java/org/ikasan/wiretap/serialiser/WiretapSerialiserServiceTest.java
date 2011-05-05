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
package org.ikasan.wiretap.serialiser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>WiretapSerialiserService</code> class.
 * 
 * @author Ikasan Development Team
 */
public class WiretapSerialiserServiceTest
{
    /** serialisers to be supported by the service */
    private Map serialisers;
    
    /** serialiser service instance */
    private WiretapSerialiser serialiserService;

    @Before 
    public void setup()
    {
        // create a map of required serialisers
        this.serialisers = new ConcurrentHashMap();
        serialisers.put(Integer.class, IntegerSerialiser.getInstance());
        
        // create a wiretap serialiser service instance passing the supported serialisers
        this.serialiserService = new WiretapSerialiserService(serialisers);
    }
    
    /**
     * Test successful serialiser invocation.
     */
    @Test
    public void test_successful_serialiser()
    {
        // create example object for serialisation
        Integer integer = new Integer(10);
        
        // test the serialiser service
        Assert.assertTrue("10".equals( new String(serialiserService.serialise(integer)) ) );
    }

    /**
     * Test failed serialiser invocation as the serialiser for StringBuilder is 
     * not in the supported serialisers map. Null should be returned
     */
    @Test
    public void test_failed_serialiser_due_to_serialiser_not_found_so_null_returned()
    {
        // create example object for serialisation
        StringBuilder stringBuilder = new StringBuilder(10);
        
        // test the serialiser service
        Assert.assertNull(serialiserService.serialise(stringBuilder));
    }
    
    /**
     * Simple example serialiser implementation of the wiretap serialiser contract.
     * This serialises an Integer object to a byte[] for wiretapping.
     * @author Ikasan Development Team
     *
     */
    private static class IntegerSerialiser implements WiretapSerialiser<Integer>
    {
        /** singleton */
        private static IntegerSerialiser integerSerialiser;

        /**
         * Get singleton instance
         * @return IntegerSerialiser
         */
        public static IntegerSerialiser getInstance()
        {
            if(integerSerialiser == null)
            {
                integerSerialiser = new IntegerSerialiser();
            }
            return integerSerialiser;
        }
        
        /**
         * Constructor
         */
        private IntegerSerialiser()
        {
            // nothing to do
        }
        
        /**
         * Serialiser implementation for Integer wiretapping.
         * @param Integer
         * @return byte[] 
         */
        public byte[] serialise(Integer source)
        {
            return source.toString().getBytes();
        }
        
    }
    
}
