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

// Imported junit classes
import org.ikasan.common.CommonEnvironment;
import org.ikasan.common.util.Env;
import org.ikasan.common.ResourceLoader;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * unit tests for the {@link org.ikasan.common.util.Env} class.
 *
 * @author Ikasan Development Team
 */
public class EnvTest
    extends TestCase
{
    
    /** Provide a common environment */
    CommonEnvironment env = null;
    
    static
    {
        System.setProperty("user.name", "dude");
        System.setProperty("thing", "Italian girl");
    }

    @Override
    protected void setUp() throws Exception
    {
        env = ResourceLoader.getInstance().newEnvironment();
        super.setUp();
    }
    
    /**
     * A unit test to check whether all variables can be expanded correctly.
     */
    public void testExpandEnvVar()
    {
        String testStr = "Hey ${user.name}! Where is my ${thing}?";
        assertEquals("Hey dude! Where is my Italian girl?", env.expandEnvVar(testStr));
    }

    /**
     * A unit test to check whether null string is handled as expected.
     */
    public void testNullString()
    {
        String testStr = null;
        assertEquals(null, env.expandEnvVar(testStr));
    }

    /**
     * A unit test to check whether all variables can be expanded correctly.
     */
    public void testDifferentVariableMarkers()
    {
        String testStr = "Hey $<user.name>! Where is my $<thing>?";
        assertEquals("Hey dude! Where is my Italian girl?",
                     env.expandEnvVar(testStr, "$<", ">"));
    }

    /**
     * A unit test to check whether the start variable marker is null.
     */
    public void testNullStartVariableMarker()
    {
        String testStr = "Hey ${user.name}! Where is my ${thing}?";
        try
        {
            env.expandEnvVar(testStr, null, Env.END_MARKER);
            fail("Expected NullPointerException "
               + "due to null start variable marker");
        }
        catch (NullPointerException e)
        {
            // TODO Deal with Exception
        }
    }

    /**
     * A unit test to check whether the start variable marker is empty.
     */
    public void testEmptyStartVariableMarker()
    {
        String testStr = "Hey ${user.name}! Where is my ${thing}?";
        try
        {
            env.expandEnvVar(testStr, "", Env.END_MARKER);
            fail("Expected NullPointerException "
               + "due to empty start variable marker");
        }
        catch (NullPointerException e)
        {
            // TODO Deal with Exception
        }
    }

    /**
     * A unit test to check whether the end variable marker is null.
     */
    public void testNullEndVariableMarker()
    {
        String testStr = "Hey ${user.name}! Where is my ${thing}?";
        try
        {
            env.expandEnvVar(testStr, Env.START_MARKER, null);
            fail("Expected NullPointerException "
               + "due to null end variable marker");
        }
        catch (NullPointerException e)
        {
            // TODO Deal with Exception
        }
    }

    /**
     * A unit test to check whether the end variable marker is empty.
     */
    public void testEmptyEndVariableMarker()
    {
        String testStr = "Hey ${user.name}! Where is my ${thing}?";
        try
        {
            env.expandEnvVar(testStr, Env.START_MARKER, "");
            fail("Expected NullPointerException "
               + "due to empty end variable marker");
        }
        catch (NullPointerException e)
        {
            // TODO Deal with Exception
        }
    }

    /**
     * A unit test to verify the variable syntax - missing end variable marker.
     */
    public void testScreamOnMissingEndVariableMarker()
    {
        String testStr = "Hey ${user.name! Where is my ${thing}?";
        try
        {
            env.expandEnvVar(testStr);
            fail("Expected IllegalArgumentException "
               + "due to mising end variable marker, '${user.name'");
        }
        catch (IllegalArgumentException e)
        {
            // TODO Deal with Exception
        }
    }

    /**
     * A unit test to continue despite variable syntax error
     * - missing end variable marker.
     */
    public void testContinueOnMissingEndVariableMarker()
    {
        String testStr = "Hey ${user.name! Where is my ${thing}?";
        try
        {
            assertEquals("Hey ${user.name! Where is my Italian girl?",
                         env.expandEnvVar(testStr, false));
        }
        catch (IllegalArgumentException e)
        {
            fail("Didn't expect IllegalArgumentException "
               + "as screamOnError is set to false");
        }
    }

    /**
     * A unit test to check whether the given variable name is empty.
     */
    public void testScreamOnEmptyVariableName()
    {
        String testStr = "Hey ${}! Where is my ${thing}?";
        try
        {
            env.expandEnvVar(testStr);
            fail("Expected IllegalArgumentException "
               + "due to the empty variable name, '${}'");
        }
        catch (IllegalArgumentException e)
        {
            // TODO Deal with Exception
        }
    }

    /**
     * A unit test to continue despite the given variable name being empty.
     */
    public void testContinueOnEmptyVariableName()
    {
        String testStr = "Hey ${}! Where is my ${thing}?";
        try
        {
            assertEquals("Hey ${}! Where is my Italian girl?",
                         env.expandEnvVar(testStr, false));
        }
        catch (IllegalArgumentException e)
        {
            fail("Didn't expect IllegalArgumentException "
               + "as screamOnError is set to false");
        }
    }

    /**
     * A unit test to verify the given variable name containing whitespace.
     */
    public void testScreamOnVariableNameWithWhitespace()
    {
        String testStr = "Hey ${user name}! Where is my ${thing}?";
        try
        {
            env.expandEnvVar(testStr);
            fail("Expected IllegalArgumentException due to "
               + "the variable name containg whitespace, '${user name}'");
        }
        catch (IllegalArgumentException e)
        {
            // Do Nothing
        }
    }

    /**
     * A unit test to continue despite the given variable name
     * containing whitespace.
     */
    public void testContinueOnVariableNameWithWhitespace()
    {
        String testStr = "Hey ${user name}! Where is my ${thing}?";
        try
        {
            assertEquals("Hey ${user name}! Where is my Italian girl?",
                         env.expandEnvVar(testStr, false));
        }
        catch (IllegalArgumentException e)
        {
            fail("Didn't expect IllegalArgumentException "
               + "as screamOnError is set to false");
        }
    }

    /**
     * A unit test to check if the given variable name starts with a digit.
     */
    public void testScreamOnVariableNameStartsWithDigit()
    {
        String testStr = "Hey ${666user.name}! Where is my ${thing}?";
        try
        {
            env.expandEnvVar(testStr);
            fail("Expected IllegalArgumentException as "
               + "the variable name starts with a digit, '${666user.name}'");
        }
        catch (IllegalArgumentException e)
        {
            // Do Nothing
        }
    }

    /**
     * A unit test to check if the given variable name starts with a digit.
     */
    public void testContinueOnVariableNameStartsWithDigit()
    {
        String testStr = "Hey ${666user.name}! Where is my ${thing}?";
        try
        {
            assertEquals("Hey ${666user.name}! Where is my Italian girl?",
                         env.expandEnvVar(testStr, false));
        }
        catch (IllegalArgumentException e)
        {
            fail("Didn't expect IllegalArgumentException "
               + "as screamOnError is set to false");
        }
    }

    /**
     * A unit test to check if there is no corresponding property value.
     * A warning message is expected to be displayed.
     */
    public void testLogWarningOnNoPropertyValue()
    {
        String testStr = "Hey ${user.name.foo.bar}! Where is my ${thing}?";
        env.expandEnvVar(testStr);
    }

    /**
     * Runs suite test.
     * @return Test suite
     */
    public static Test suite()
    {
        TestSetup setup = new TestSetup(new TestSuite(EnvTest.class))
        {
            @Override
            protected void setUp()
            {
                // do your one-time setup here!
            }

            @Override
            protected void tearDown()
            {
                // do your one-time tear down here!
            }
        };

        return setup;
    }

}
