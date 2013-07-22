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
package org.ikasan.console.pointtopointflow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ikasan.console.module.Module;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.Assert;

/**
 * JUnit based test class for testing PointToPointFlow
 * 
 * @author Ikasan Development Team
 */
public class PointToPointFlowTest
{
    /**
     * The context that the tests run in, allows for mocking actual concrete
     * classes
     */
    private Mockery context = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test PointToPointFlowProfile Getters and Setters
     */
    @Test
    public void testPointToPointFlowProfileGettersAndSetters()
    {
        // Setup
        PointToPointFlow pointToPointFlow = new PointToPointFlow();
        PointToPointFlowProfile pointToPointFlowProfile = context.mock(PointToPointFlowProfile.class);
        Module fromModule = context.mock(Module.class);
        Module toModule = context.mock(Module.class);
        
        // Test (including the private setter via Reflection)
        Class<?> cls = null;
        try
        {
            cls = Class.forName("org.ikasan.console.pointtopointflow.PointToPointFlow");
        }
        catch (ClassNotFoundException e)
        {
            Assert.fail("ClassNotFoundException");
        }
        try
        {
            if (cls != null)
            {
                for (Method method : cls.getDeclaredMethods())
                {
                    if ("setId".equals(method.getName()))
                    {
                        method.setAccessible(true);
                        try
                        {
                            method.invoke(pointToPointFlow, new Long(1));
                        }
                        catch (IllegalArgumentException e)
                        {
                            Assert.fail("IllegalArgumentException");
                        }
                        catch (IllegalAccessException e)
                        {
                            Assert.fail("IllegalAccessException");
                        }
                        catch (InvocationTargetException e)
                        {
                            Assert.fail("InvocationTargetException");
                        }
                    }
                }
            }
            else
            {
                Assert.fail("cls is NULL");
            }
        }
        catch (SecurityException e)
        {
            Assert.fail("SecurityException");
        }

        pointToPointFlow.setFromModule(fromModule);
        pointToPointFlow.setToModule(toModule);
        pointToPointFlow.setPointToPointFlowProfile(pointToPointFlowProfile);
        
        // Test and Verify
        Assert.assertEquals(1, pointToPointFlow.getId());
        Assert.assertNotNull(pointToPointFlow.getFromModule());
        Assert.assertNotNull(pointToPointFlow.getToModule());
        Assert.assertNotNull(pointToPointFlow.getPointToPointFlowProfile());
    }
}
