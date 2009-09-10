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
package org.ikasan.framework.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.ExceptionType;
import org.ikasan.common.Payload;
import org.ikasan.framework.exception.DefaultExceptionDefinition;
import org.ikasan.framework.exception.ExceptionContext;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.ikasan.framework.exception.IkasanExceptionResolution;
import org.ikasan.framework.exception.IkasanExceptionResolutionImpl;
import org.ikasan.framework.exception.IkasanExceptionResolver;
import org.ikasan.framework.exception.IkasanExceptionResolverImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>IkasanExceptionHandlerImplTest</code> concrete
 * implementation class.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionHandlerImplTest
{
    
    
    /**
     * Mockery for interfaces
     */
    private Mockery mockery = new Mockery();
    //
    // empty collection instances
    /** module Exception definitions */
    List<DefaultExceptionDefinition> moduleExceptionDefs = 
        new ArrayList<DefaultExceptionDefinition>();
    /** specific Exception definitions */
    List<DefaultExceptionDefinition> componentExceptionDefs =
        new ArrayList<DefaultExceptionDefinition>();
    /** component specific Exception definitions */
    Map<String,List<DefaultExceptionDefinition>> componentExceptionDefsMap =
        new HashMap<String,List<DefaultExceptionDefinition>>();

    //
    // dummy static test data
    /** module name */
    private static String moduleName = "moduleName";
    /** component name 1 */
    static String componentName = "componentName";
    /** module exception id */    
    private static String moduleExceptionId = "moduleExceptionId";
    /** exception type */
    private static ExceptionType type = CommonExceptionType.UNDEFINED;
    /** action for NullPointer */
    private static IkasanExceptionAction nullPointerExceptionAction = 
        new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP,
                new Long(10), new Integer(10));
    /** resolution for NullPointer */
    private static IkasanExceptionResolution nullPointerExceptionResolution = 
        new IkasanExceptionResolutionImpl(moduleExceptionId, nullPointerExceptionAction);
    
    /** action for Exception */
    private static IkasanExceptionAction exceptionAction = 
        new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY,
                new Long(10), new Integer(10));
    /** resolution for Exception */
    private static IkasanExceptionResolution exceptionResolution = 
        new IkasanExceptionResolutionImpl(moduleExceptionId, exceptionAction);

    /** action for NumberFormatException */
    private static IkasanExceptionAction numberFormatExceptionAction = 
        new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY,
                new Long(10), new Integer(10));
    /** resolution for NumberFormatException */
    private static IkasanExceptionResolution numberFormatExceptionResolution = 
        new IkasanExceptionResolutionImpl(moduleExceptionId, numberFormatExceptionAction);

   /** dummy event for testing with event signature */
    private Event event;

    // mutable test data
    /** numberFormatExceptionClassName */
    private String numberFormatExceptionClassName = "java.lang.NumberFormatException";
    /** exceptionClassName */
    private String exceptionClassName = "java.lang.Exception";
    /** nullPointerExceptionClassName */
    private String nullPointerExceptionClassName = "java.lang.NullPointerException";
    /** DefaultExceptionDefinition */
    private DefaultExceptionDefinition exceptionDefinition;
    /** IkasanExceptionResolver */
    private IkasanExceptionResolver ikasanExceptionResolver;
    /** IkasanExceptionHandler */
    private IkasanExceptionHandler ikasanExceptionHandler;
    /** Movked userExceptionHandler */
    private UserExceptionHandler userExceptionHandler = mockery.mock(UserExceptionHandler.class);
    
    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        //
        // set up module exception defs

        /*
         * module exceptions include
         * java.lang.NumberFormatException
         * java.lang.Exception
         */
        exceptionDefinition = new DefaultExceptionDefinition(numberFormatExceptionResolution, numberFormatExceptionClassName, type);
        moduleExceptionDefs.add(exceptionDefinition);

        exceptionDefinition = new DefaultExceptionDefinition(exceptionResolution, exceptionClassName, type);
        moduleExceptionDefs.add(exceptionDefinition);
        
        //
        // set up component exception defs

        /*
         * componentId 1 exceptions include
         * java.lang.NullPointerException
         */
        exceptionDefinition = new DefaultExceptionDefinition(nullPointerExceptionResolution, nullPointerExceptionClassName, type);
        componentExceptionDefs.add(exceptionDefinition);

        /*
         * componentId 2 exceptions include
         * java.lang.Exception
         */
        exceptionDefinition = new DefaultExceptionDefinition(exceptionResolution, exceptionClassName, type);
        componentExceptionDefs.add(exceptionDefinition);

        //
        // create the map
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);
        
        //
        // create the resolver instance
        ikasanExceptionResolver = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        
        //
        // create the exception handler
        ikasanExceptionHandler = new IkasanExceptionHandlerImpl(moduleName, ikasanExceptionResolver, null);
        
        //
        // create a dummy event
        event = new Event(null,null,"myEvent1",new ArrayList<Payload>());
    }

    /**
     * Test happy constructor as a measure of the potential failed constructor tests.
     */
    @Test
    public void testHappyConstructor()
    {
        new IkasanExceptionHandlerImpl(moduleName, ikasanExceptionResolver, userExceptionHandler);
    }

    /**
     * Test failed constructor due to 'null' moduleName.
     * @throws CommonRuntimeException
     */
    @Test(expected = IllegalArgumentException.class)    
    public void testFailedConstructorWithNullModuleName()
    {
        new IkasanExceptionHandlerImpl(null, ikasanExceptionResolver, userExceptionHandler);
    }

    /**
     * Test failed constructor due to 'null' exceptionResolver.
     * @throws CommonRuntimeException 
     */
    @Test(expected = IllegalArgumentException.class)    
    public void testFailedConstructorWithNullExceptionResolver()
    {
        new IkasanExceptionHandlerImpl(moduleName, null, userExceptionHandler);
    }

    /**
     * Test invoke based on a NullPointerException which must
     * result in a nullPointerExceptionAction.
     */
    @Test
    public void test1InvokeForSuccessfulActionResolutionNoEvent()
    {
        Throwable t = new java.lang.NullPointerException("test");
        IkasanExceptionAction action = ikasanExceptionHandler.invoke(componentName, t);
        Assert.assertTrue(action.equals(nullPointerExceptionAction));
    }

    /**
     * Test invoke based on an Exception which must
     * result in an exceptionAction.
     */
    @Test
    public void test2InvokeForSuccessfulActionResolutionNoEvent()
    {
        Throwable t = new java.lang.Exception("test");
        IkasanExceptionAction action = ikasanExceptionHandler.invoke(componentName, t);
        Assert.assertTrue(action.equals(exceptionAction));
    }

    /**
     * Test invoke based on an Exception which must
     * result in an exceptionAction.
     */
    @Test
    public void test3InvokeForSuccessfulActionResolutionNoEvent()
    {
        Throwable t = new java.lang.NumberFormatException("test");
        IkasanExceptionAction action = ikasanExceptionHandler.invoke(componentName, t);
        Assert.assertTrue(action.equals(numberFormatExceptionAction));
    }

    /**
     * Test invoke based on no exception match, so the default emergencyStop()
     * action should be returned.
     */
    @Test
    public void test4InvokeForUnmatchedExceptionResolutionNoEvent()
    {
        // get the default emergency resolution
        IkasanExceptionResolution emergencyResolution = 
            IkasanExceptionResolutionImpl.getEmergencyResolution();

        // invoke a resolver which will fail and hopefully do the emergency stuff
        Throwable t = new java.lang.InstantiationException("test");
        IkasanExceptionAction action = ikasanExceptionHandler.invoke(componentName, t);

        // did it fail and resolve to the emergency resolution ?
        Assert.assertTrue(action.equals(emergencyResolution.getAction()));
    }

    /**
     * Test invoke based on a NullPointerException which must
     * result in a nullPointerExceptionAction.
     */
    @Test
    public void test1InvokeForSuccessfulActionResolutionWithEvent()
    {
        Throwable t = new java.lang.NullPointerException("test");
        IkasanExceptionAction action = ikasanExceptionHandler.invoke(componentName, event, t);
        Assert.assertTrue(action.equals(nullPointerExceptionAction));
    }

    /**
     * Test invoke based on an Exception which must
     * result in an exceptionAction.
     */
    @Test
    public void test2InvokeForSuccessfulActionResolutionWithEvent()
    {
        Throwable t = new java.lang.Exception("test");
        IkasanExceptionAction action = ikasanExceptionHandler.invoke(componentName, event, t);
        Assert.assertTrue(action.equals(exceptionAction));
    }

    /**
     * Test invoke based on an Exception which must
     * result in an exceptionAction.
     */
    @Test
    public void test3InvokeForSuccessfulActionResolutionWithEvent()
    {
        Throwable t = new java.lang.NumberFormatException("test");
        IkasanExceptionAction action = ikasanExceptionHandler.invoke(componentName, event, t);
        Assert.assertTrue(action.equals(numberFormatExceptionAction));
    }

    /**
     * Test invoke based on no exception match, so the default emergencyStop()
     * action should be returned.
     */
    @Test
    public void test4InvokeForUnmatchedExceptionResolutionWithEvent()
    {
        // get the default emergency resolution
        IkasanExceptionResolution emergencyResolution = 
            IkasanExceptionResolutionImpl.getEmergencyResolution();

        // invoke a resolver which will fail and hopefully do the emergency stuff
        Throwable t = new java.lang.InstantiationException("test");
        IkasanExceptionAction action = ikasanExceptionHandler.invoke(componentName, event, t);

        // did it fail and resolve to the emergency resolution ?
        Assert.assertTrue(action.equals(emergencyResolution.getAction()));
    }

    /**
     * @throws Exception
     */
    public void testInvokePropgatesToUserExceptionHandler() throws Exception{
        final IkasanExceptionResolver exceptionResolver = mockery.mock(IkasanExceptionResolver.class);
        final UserExceptionHandler usrExceptionHandler = mockery.mock(UserExceptionHandler.class);
        final Throwable throwable = new Throwable();
        final ExceptionContext exceptionContext = new ExceptionContext(throwable, event,componentName);
        final String frameworkResoultionnId = "frameworkResoultionnId";
        final IkasanExceptionAction ikasanExceptionAction = mockery.mock(IkasanExceptionAction.class);
        
        exceptionContext.setResolutionId(frameworkResoultionnId);
        
        mockery.checking(new Expectations()
        {
            {    
                one(exceptionResolver).resolve(componentName, throwable);will(returnValue(ikasanExceptionAction));
                one(usrExceptionHandler).invoke(with(equal(exceptionContext)));
            }
        });
        IkasanExceptionHandlerImpl ikasanExceptionHandlerImpl = new IkasanExceptionHandlerImpl("moduleName", exceptionResolver, usrExceptionHandler);
        

        ikasanExceptionHandlerImpl.invoke(componentName, event, throwable);
        mockery.assertIsSatisfied();
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        ikasanExceptionResolver = null;
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(IkasanExceptionHandlerImplTest.class);
    }
}
