/*
 * $Id: EnvTest.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/test/java/org/ikasan/common/util/EnvTest.java $
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
 * @author Jun Suetake
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
