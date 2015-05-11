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
package org.ikasan.serialiser.service;

import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for SerialiserFactoryKryoImpl.
 * 
 * @author Ikasan Development Team
 */
public class GenericKryoToBytesSerialiserTest
{
    /** one serialiser service should provide all serialiser instances */
    SerialiserFactory serialiserFactory = new SerialiserFactoryKryoImpl();

    /**
     * Test
     */
    @Test
    public void test_getSerialiser_for_string_successful()
    {

        // object for serialise/deserialise test
        String str = new String("test");

        // get a serialiser
        Serialiser<String,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(str);

        // deserialise it
        String restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(str));
    }

    /**
     * Test
     */
    @Test
    public void test_getSerialiser_for_integer_successful()
    {
        // object for serialise/deserialise test
        Integer myInt = new Integer(10);

        // get a serialiser
        Serialiser<Integer,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(myInt);

        // deserialise it
        Integer restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(myInt));
    }

    /**
     * Test
     */
    @Test
    public void test_getSerialiser_for_primitiveClass_successful()
    {
        // object for serialise/deserialise test
        PrimitiveClass primitiveClass = new PrimitiveClass();

        // get a serialiser
        Serialiser<PrimitiveClass,byte[]> serialiser = serialiserFactory.getDefaultSerialiser();

        // serialise it
        byte[] bytes = serialiser.serialise(primitiveClass);

        // deserialise it
        PrimitiveClass restored = serialiser.deserialise(bytes);
        Assert.assertTrue(restored.equals(primitiveClass));
    }
}
